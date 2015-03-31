/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2015 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */

package com.pentaho.profiling.model;

import com.pentaho.profiling.api.AggregateProfile;
import com.pentaho.profiling.api.MessageUtils;
import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.Profile;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusMessage;
import com.pentaho.profiling.api.ProfileStatusReadOperation;
import com.pentaho.profiling.api.ProfileStatusReader;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.datasource.DataSourceReference;
import com.pentaho.profiling.api.metrics.MetricContributor;
import com.pentaho.profiling.api.metrics.MetricContributors;
import com.pentaho.profiling.api.metrics.MetricContributorsFactory;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.ProfileFieldProperties;
import com.pentaho.profiling.api.metrics.field.DataSourceField;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldManager;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import com.pentaho.profiling.api.stats.Statistic;
import org.pentaho.osgi.notification.api.NotificationListener;
import org.pentaho.osgi.notification.api.NotificationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by bryan on 3/5/15.
 */
public class AggregateProfileImpl implements AggregateProfile {
  private static final Logger LOGGER = LoggerFactory.getLogger( AggregateProfileImpl.class );
  private static String KEY_PATH = MessageUtils.getId( "data-profiling-model", AggregateProfileImpl.class );
  private final DataSourceReference dataSourceReference;
  private final ProfileStatusManager profileStatusManager;
  private final ProfilingServiceImpl profilingService;
  private final ReadWriteLock readWriteLock;
  private final List<String> childProfileIdList;
  private final Set<String> childProfileIdSet;
  private final List<MetricContributor> metricContributorList;
  private final NotificationListener notificationListener;
  private final AtomicBoolean running;
  private final AtomicBoolean refreshQueued;
  private final AtomicLong lastRefresh;
  private ExecutorService executorService;

  public AggregateProfileImpl( DataSourceReference dataSourceReference, ProfileStatusManager profileStatusManager,
                               ProfilingServiceImpl profilingService,
                               MetricContributorsFactory metricContributorsFactory,
                               MetricContributors metricContributors ) {
    this.dataSourceReference = dataSourceReference;
    this.profileStatusManager = profileStatusManager;
    this.profilingService = profilingService;
    this.metricContributorList = metricContributorsFactory.construct( metricContributors );
    this.childProfileIdList = new ArrayList<String>();
    this.childProfileIdSet = new HashSet<String>();
    this.readWriteLock = new ReentrantReadWriteLock();
    this.running = new AtomicBoolean( false );
    this.refreshQueued = new AtomicBoolean( false );
    this.lastRefresh = new AtomicLong( 0L );
    profileStatusManager.write( new ProfileStatusWriteOperation<Void>() {
      @Override public Void write( MutableProfileStatus profileStatus ) {
        List<ProfileFieldProperty> intrinsicProperties = Arrays.asList( ProfileFieldProperties.LOGICAL_NAME,
          ProfileFieldProperties.PHYSICAL_NAME, ProfileFieldProperties.FIELD_TYPE,
          ProfileFieldProperties.COUNT_FIELD );
        List<ProfileFieldProperty> profileFieldProperties = new ArrayList<ProfileFieldProperty>( intrinsicProperties );
        for ( MetricContributor metricContributor : metricContributorList ) {
          for ( ProfileFieldProperty profileFieldProperty : metricContributor.getProfileFieldProperties() ) {
            profileFieldProperties.add( profileFieldProperty );
          }
        }
        profileStatus.setProfileFieldProperties( profileFieldProperties );
        return null;
      }
    } );
    notificationListener = new NotificationListener() {
      @Override public void notify( NotificationObject notificationObject ) {
        Lock readLock = readWriteLock.readLock();
        readLock.lock();
        try {
          if ( childProfileIdSet.contains( notificationObject.getId() ) ) {
            queueRefresh();
          }
        } finally {
          readLock.unlock();
        }
      }
    };
  }

  @Override public String getId() {
    return profileStatusManager.getId();
  }

  @Override public String getName() {
    return profileStatusManager.getName();
  }

  @Override public void start( ExecutorService executorService ) {
    if ( !running.getAndSet( true ) ) {
      this.executorService = executorService;
      profilingService.register( notificationListener );
      queueRefresh();
    } else {
      LOGGER.warn( "Tried to start an already running aggregate profile: " + getId() );
    }
  }

  @Override public void stop() {
    running.set( false );
    profilingService.unregister( notificationListener );
  }

  @Override public boolean isRunning() {
    return running.get();
  }

  @Override public List<Profile> getChildProfiles() {
    List<Profile> result = new ArrayList<Profile>();
    Lock readLock = readWriteLock.readLock();
    readLock.lock();
    try {
      for ( String profileId : childProfileIdList ) {
        result.add( profilingService.getProfile( profileId ) );
      }
    } finally {
      readLock.unlock();
    }
    return result;
  }

  private void merge( DataSourceFieldManager dataSourceFieldManagerInto,
                      DataSourceFieldManager dataSourceFieldManagerFrom ) {
    for ( DataSourceField intoField : dataSourceFieldManagerInto.getDataSourceFields() ) {
      if ( LOGGER.isDebugEnabled() ) {
        LOGGER.debug( "Merging field " + intoField.getLogicalName() );
      }
      DataSourceField fromField =
        dataSourceFieldManagerFrom.getPathToDataSourceFieldMap().get( intoField.getPhysicalName() );
      if ( fromField != null ) {
        if ( LOGGER.isDebugEnabled() ) {
          LOGGER.debug( "Field exists in both into and from" );
        }
        Set<String> overlappingTypes = intoField.getMetricManagerTypes();
        overlappingTypes.retainAll( fromField.getMetricManagerTypes() );
        for ( String type : overlappingTypes ) {
          DataSourceMetricManager metricManagerForType = intoField.getMetricManagerForType( type );
          Number existing = metricManagerForType.getValueNoDefault( Statistic.COUNT );
          Number from = fromField.getMetricManagerForType( type ).getValueNoDefault( Statistic.COUNT );
          long value = existing.longValue() + from.longValue();
          if ( LOGGER.isDebugEnabled() ) {
            LOGGER.debug( "Updating count from " + existing + " to " + value );
          }
          metricManagerForType.setValue( value, Statistic.COUNT );
        }
      }
    }
    for ( MetricContributor metricContributor : metricContributorList ) {
      try {
        metricContributor.merge( dataSourceFieldManagerInto, dataSourceFieldManagerFrom );
      } catch ( MetricMergeException e ) {
        LOGGER.error( e.getMessage(), e );
      }
    }
    for ( MetricContributor metricContributor : metricContributorList ) {
      try {
        metricContributor.setDerived( dataSourceFieldManagerInto );
      } catch ( ProfileActionException e ) {
        LOGGER.error( e.getMessage(), e );
      }
    }
  }

  private void queueRefresh() {
    if ( running.get() && executorService != null ) {
      if ( !refreshQueued.getAndSet( true ) ) {
        executorService.submit( new Runnable() {
          @Override public void run() {
            try {
              Thread.sleep( Math.max( 0L, lastRefresh.get() + 1000L - System.currentTimeMillis() ) );
            } catch ( InterruptedException e ) {
              refreshQueued.set( false );
              return;
            }
            Lock readLock = readWriteLock.readLock();
            readLock.lock();
            try {
              synchronized ( lastRefresh ) {
                lastRefresh.set( System.currentTimeMillis() );
                refreshQueued.set( false );
                final DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager();
                final List<ProfileStatusMessage> newStatusMessages = new ArrayList<ProfileStatusMessage>();
                int num = 1;
                for ( String profileId : childProfileIdList ) {
                  ProfileStatusReader profileStatusReader = profilingService.getProfileUpdate( profileId );
                  final int finalNum = num;
                  profileStatusReader.read( new ProfileStatusReadOperation<Void>() {
                    @Override public Void read( ProfileStatus profileStatus ) {
                      DataSourceFieldManager newManager = new DataSourceFieldManager( profileStatus.getFields() );
                      List<ProfileStatusMessage> statusMessages = profileStatus.getStatusMessages();
                      if ( statusMessages != null && statusMessages.size() > 0 ) {
                        newStatusMessages
                          .add( new ProfileStatusMessage( KEY_PATH, "ChildProfile", Arrays.asList( "" + finalNum ) ) );
                        newStatusMessages.addAll( statusMessages );
                      }
                      merge( dataSourceFieldManager, newManager );
                      return null;
                    }
                  } );
                  num++;
                }
                profileStatusManager.write( new ProfileStatusWriteOperation<Void>() {
                  @Override public Void write( MutableProfileStatus profileStatus ) {
                    profileStatus.setFields( dataSourceFieldManager.getProfilingFields() );
                    profileStatus.setStatusMessages( newStatusMessages );
                    return null;
                  }
                } );
              }
            } finally {
              readLock.unlock();
            }
          }
        } );
      }
    }
  }

  @Override public void addChildProfile( String profileId ) {
    Profile childProfile = profilingService.getProfile( profileId );
    if ( childProfile != null ) {
      Lock writeLock = readWriteLock.writeLock();
      writeLock.lock();
      try {
        if ( childProfileIdSet.add( profileId ) ) {
          childProfileIdList.add( profileId );
          queueRefresh();
        } else {
          LOGGER.warn( "Tried to add same child profile id more than once: " + profileId );
        }
      } finally {
        writeLock.unlock();
      }
    } else {
      LOGGER.warn( "Tried to add nonexistent child profile with id: " + profileId );
    }
  }
}
