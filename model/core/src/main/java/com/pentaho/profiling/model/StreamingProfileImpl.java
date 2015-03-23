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

import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.StreamingProfile;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricContributor;
import com.pentaho.profiling.api.metrics.MetricContributors;
import com.pentaho.profiling.api.metrics.MetricContributorsFactory;
import com.pentaho.profiling.api.metrics.ProfileFieldProperties;
import com.pentaho.profiling.api.metrics.field.DataSourceField;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldManager;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import com.pentaho.profiling.api.stats.Statistic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by bryan on 3/23/15.
 */
public class StreamingProfileImpl implements StreamingProfile {
  private static final Logger LOGGER = LoggerFactory.getLogger( StreamingProfileImpl.class );
  private final AtomicBoolean isRunning = new AtomicBoolean( false );
  private final AtomicBoolean refreshQueued = new AtomicBoolean( false );
  private final AtomicLong lastRefresh = new AtomicLong( 0L );
  private final ProfileStatusManager profileStatusManager;
  private final List<MetricContributor> metricContributorList;
  private final DataSourceFieldManager dataSourceFieldManager;
  private ExecutorService executorService;

  public StreamingProfileImpl( ProfileStatusManager profileStatusManager,
                               MetricContributorsFactory metricContributorsFactory,
                               MetricContributors metricContributors ) {
    this.profileStatusManager = profileStatusManager;
    this.metricContributorList = metricContributorsFactory.construct( metricContributors );
    dataSourceFieldManager = new DataSourceFieldManager();
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
  }

  @Override public synchronized void processRecord( List<DataSourceFieldValue> dataSourceFieldValues )
    throws ProfileActionException {
    for ( DataSourceFieldValue dataSourceFieldValue : dataSourceFieldValues ) {
      DataSourceField dataSourceField =
        dataSourceFieldManager.getPathToDataSourceFieldMap().get( dataSourceFieldValue.getPhysicalName() );
      if ( dataSourceField == null ) {
        dataSourceField = new DataSourceField();
        dataSourceField.setLogicalName( dataSourceFieldValue.getLogicalName() );
        dataSourceField.setPhysicalName( dataSourceFieldValue.getPhysicalName() );
        dataSourceFieldManager.addDataSourceField( dataSourceField );
      }
      DataSourceMetricManager fieldType =
        dataSourceField.getMetricManagerForType( dataSourceFieldValue.getFieldTypeName(), true );
      fieldType.setValue( fieldType.getValue( 0L, Statistic.COUNT ).longValue() + 1L, Statistic.COUNT );
    }
    for ( MetricContributor metricContributor : metricContributorList ) {
      metricContributor.processFields( dataSourceFieldManager, dataSourceFieldValues );
    }
    queueRefresh();
  }

  private synchronized void doRefresh() {
    lastRefresh.set( System.currentTimeMillis() );
    refreshQueued.set( false );
    for ( MetricContributor metricContributor : metricContributorList ) {
      try {
        metricContributor.setDerived( dataSourceFieldManager );
      } catch ( ProfileActionException e ) {
        LOGGER.error( e.getMessage(), e );
      }
    }
    profileStatusManager.write( new ProfileStatusWriteOperation<Void>() {
      @Override public Void write( MutableProfileStatus profileStatus ) {
        profileStatus.setFields( dataSourceFieldManager.getProfilingFields() );
        return null;
      }
    } );

  }

  private void queueRefresh() {
    if ( isRunning.get() && executorService != null ) {
      if ( !refreshQueued.getAndSet( true ) ) {
        executorService.submit( new Runnable() {
          @Override public void run() {
            try {
              Thread.sleep( Math.max( 0L, lastRefresh.get() + 1000L - System.currentTimeMillis() ) );
            } catch ( InterruptedException e ) {
              refreshQueued.set( false );
              return;
            }
            doRefresh();
          }
        } );
      }
    }
  }

  @Override public String getId() {
    return profileStatusManager.getId();
  }

  @Override public void start( ExecutorService executorService ) {
    this.executorService = executorService;
    isRunning.set( true );
  }

  @Override public void stop() {
    isRunning.set( false );
  }

  @Override public boolean isRunning() {
    return isRunning.get();
  }
}
