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

import com.pentaho.profiling.api.IllegalTransactionException;
import com.pentaho.profiling.api.MutableProfileField;
import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileState;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusMessage;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.StreamingCommitStrategy;
import com.pentaho.profiling.api.StreamingProfile;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.mapper.HasStatusMessages;
import com.pentaho.profiling.api.metrics.MetricContributor;
import com.pentaho.profiling.api.metrics.MetricContributors;
import com.pentaho.profiling.api.metrics.MetricContributorsFactory;
import com.pentaho.profiling.api.metrics.ProfileFieldProperties;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.streaming.LinearTimeCommitStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by bryan on 3/23/15.
 */
public class StreamingProfileImpl implements StreamingProfile {
  private static final Logger LOGGER = LoggerFactory.getLogger( StreamingProfileImpl.class );
  private final AtomicBoolean isRunning = new AtomicBoolean( false );
  private final ProfileStatusManager profileStatusManager;
  private final List<MetricContributor> metricContributorList;
  private StreamingCommitStrategy streamingCommitStrategy;
  private long currentRefresh;
  private long nextRefresh;
  private boolean isTimestamp;
  private ExecutorService executorService;
  private HasStatusMessages hasStatusMessages;
  private MutableProfileStatus transaction;

  public StreamingProfileImpl( ProfileStatusManager profileStatusManager,
                               MetricContributorsFactory metricContributorsFactory,
                               MetricContributors metricContributors ) {
    this.profileStatusManager = profileStatusManager;
    this.metricContributorList = metricContributorsFactory.construct( metricContributors );
    setCommitStrategy( new LinearTimeCommitStrategy( 1000 ) );
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
        profileStatus.setTotalEntities( 0L );
        return null;
      }
    } );
  }

  @Override public void setCommitStrategy( StreamingCommitStrategy streamingCommitStrategy ) {
    this.streamingCommitStrategy = streamingCommitStrategy;
    currentRefresh = 0L;
    nextRefresh = streamingCommitStrategy.getNextCommit( currentRefresh );
    this.isTimestamp = streamingCommitStrategy.isTimestamp();
  }

  @Override public synchronized void processRecord( final List<DataSourceFieldValue> dataSourceFieldValues )
    throws ProfileActionException {
    if ( isRunning.get() ) {
      Long totalEntities = transaction.getTotalEntities() + 1;
      transaction.setTotalEntities( totalEntities );
      for ( DataSourceFieldValue dataSourceFieldValue : dataSourceFieldValues ) {
        MutableProfileField field = transaction
          .getOrCreateField( dataSourceFieldValue.getPhysicalName(), dataSourceFieldValue.getLogicalName() );
        field.getOrCreateValueTypeMetrics( dataSourceFieldValue.getFieldTypeName() ).incrementCount();
      }
      for ( MetricContributor metricContributor : metricContributorList ) {
        metricContributor.processFields( transaction, dataSourceFieldValues );
      }
      try {
        if ( isTimestamp ) {
          long currentTimeMillis = System.currentTimeMillis();
          if ( currentTimeMillis >= nextRefresh ) {
            currentRefresh = currentTimeMillis;
            nextRefresh = streamingCommitStrategy.getNextCommit( currentRefresh );
            doRefresh();
          }
        } else {
          if ( totalEntities >= nextRefresh ) {
            currentRefresh = totalEntities;
            nextRefresh = streamingCommitStrategy.getNextCommit( currentRefresh );
            doRefresh();
          }
        }
      } catch ( IllegalTransactionException e ) {
        throw new ProfileActionException( null, e );
      }
    } else {
      throw new ProfileActionException( null, null );
    }
  }

  @Override public void setHasStatusMessages( HasStatusMessages hasStatusMessages ) {
    this.hasStatusMessages = hasStatusMessages;
  }

  private void doRefresh() throws IllegalTransactionException {
    HasStatusMessages hasStatusMessages = StreamingProfileImpl.this.hasStatusMessages;
    if ( hasStatusMessages != null ) {
      transaction.setStatusMessages( hasStatusMessages.getStatusMessages() );
    }
    for ( MetricContributor metricContributor : metricContributorList ) {
      try {
        metricContributor.setDerived( transaction );
      } catch ( Exception e ) {
        LOGGER.error( e.getMessage(), e );
      }
    }
    profileStatusManager.commit( transaction );
    if ( isRunning() ) {
      transaction = profileStatusManager.startTransaction();
    } else {
      transaction = null;
    }
  }

  @Override public String getId() {
    return profileStatusManager.getId();
  }

  @Override public String getName() {
    return profileStatusManager.getName();
  }

  @Override public void start( ExecutorService executorService ) {
    this.executorService = executorService;
    try {
      transaction = profileStatusManager.startTransaction();
    } catch ( IllegalTransactionException e ) {
      LOGGER.error( "Unable to create transaction", e );
    }
    isRunning.set( true );
  }

  @Override public void stop() {
    isRunning.set( false );
    try {
      doRefresh();
    } catch ( IllegalTransactionException e ) {
      LOGGER.error( e.getMessage(), e );
    }
    profileStatusManager.write( new ProfileStatusWriteOperation<Void>() {
      @Override public Void write( MutableProfileStatus profileStatus ) {
        profileStatus.setProfileState( ProfileState.STOPPED );
        profileStatus.setStatusMessages( new ArrayList<ProfileStatusMessage>() );
        return null;
      }
    } );
  }

  @Override public boolean isRunning() {
    return isRunning.get();
  }
}
