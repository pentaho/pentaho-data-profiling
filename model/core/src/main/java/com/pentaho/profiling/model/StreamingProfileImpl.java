/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package com.pentaho.profiling.model;

import com.pentaho.profiling.api.IllegalTransactionException;
import com.pentaho.profiling.api.MutableProfileField;
import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileState;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusMessage;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.StreamingProfile;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.commit.CommitAction;
import com.pentaho.profiling.api.commit.CommitStrategy;
import com.pentaho.profiling.api.commit.strategies.LinearTimeCommitStrategy;
import com.pentaho.profiling.api.mapper.HasStatusMessages;
import com.pentaho.profiling.api.metrics.MetricContributor;
import com.pentaho.profiling.api.metrics.MetricContributors;
import com.pentaho.profiling.api.metrics.MetricContributorsFactory;
import com.pentaho.profiling.api.metrics.ProfileFieldProperties;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
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
  private ExecutorService executorService;
  private CommitStrategy commitStrategy;
  private HasStatusMessages hasStatusMessages;
  private MutableProfileStatus transaction;
  private final CommitAction commitAction = new CommitAction() {
    @Override public void perform() {
      commit();
    }
  };

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

  @Override public void setCommitStrategy( CommitStrategy commitStrategy ) {
    this.commitStrategy = commitStrategy;
    this.commitStrategy.init( commitAction, executorService );
  }

  @Override public synchronized <T> T perform( ProfileStatusWriteOperation<T> profileStatusWriteOperation ) {
    boolean startTransaction = transaction != null;
    if ( startTransaction ) {
      try {
        profileStatusManager.commit( transaction );
        try {
          return profileStatusManager.write( profileStatusWriteOperation );
        } finally {
          if ( startTransaction ) {
            transaction = profileStatusManager.startTransaction();
          }
        }
      } catch ( IllegalTransactionException e ) {
        e.printStackTrace();
      }
    }
    return null;
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
      commitStrategy.eventProcessed();
    } else {
      throw new ProfileActionException( null, null );
    }
  }

  @Override public void setHasStatusMessages( HasStatusMessages hasStatusMessages ) {
    this.hasStatusMessages = hasStatusMessages;
  }

  private void doRefresh() throws IllegalTransactionException {
    if ( transaction != null ) {
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
  }

  @Override public String getId() {
    return profileStatusManager.getId();
  }

  @Override public String getName() {
    return profileStatusManager.getName();
  }

  @Override public synchronized void start( ExecutorService executorService ) {
    this.executorService = executorService;
    setCommitStrategy( this.commitStrategy );
    try {
      transaction = profileStatusManager.startTransaction();
    } catch ( IllegalTransactionException e ) {
      LOGGER.error( "Unable to create transaction", e );
    }
    isRunning.set( true );
  }

  @Override public synchronized void commit() {
    try {
      doRefresh();
    } catch ( IllegalTransactionException e ) {
      LOGGER.error( "Unable to commit", e );
    }
  }

  @Override public synchronized void stop() {
    if ( !isRunning.getAndSet( false ) ) {
      return;
    }
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
