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

package org.pentaho.profiling.kettle.integration.core.preview;

import org.pentaho.plugin.util.DataSourceFieldValueCreator;
import org.pentaho.profiling.api.AggregateProfileService;
import org.pentaho.profiling.api.MutableProfileStatus;
import org.pentaho.profiling.api.ProfileCreationException;
import org.pentaho.profiling.api.ProfileStatusManager;
import org.pentaho.profiling.api.ProfileStatusWriteOperation;
import org.pentaho.profiling.api.ProfilingService;
import org.pentaho.profiling.api.StreamingProfile;
import org.pentaho.profiling.api.StreamingProfileService;
import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.configuration.ProfileConfiguration;
import org.pentaho.profiling.api.configuration.core.StreamingProfileMetadata;
import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.debug.BreakPointListener;
import org.pentaho.di.trans.debug.StepDebugMeta;
import org.pentaho.di.trans.debug.TransDebugMeta;
import org.pentaho.di.trans.step.RowListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bryan on 3/24/15.
 */
public class PreviewProfileStreamerListener implements RowListener, BreakPointListener {
  public static final String UNABLE_TO_CREATE_STREAMING_PROFILE = "Unable to create streaming profile.";
  public static final String UNABLE_TO_ADD_FIELD = "Unable to add field ";
  public static final String UNABLE_TO_PROCESS_RECORD = "Unable to process record: ";
  private static final Logger LOGGER = LoggerFactory.getLogger( PreviewProfileStreamerListener.class );
  private final ProfilingService profilingService;
  private final StreamingProfileService streamingProfileService;
  private final String aggregateProfileId;
  private final AggregateProfileService aggregateProfileService;
  private final DataSourceFieldValueCreator dataSourceFieldValueCreator;
  private ProfileStatusManager streamingProfileStatusManager;
  private StreamingProfile streamingProfile;
  private int rowCount = 0;

  public PreviewProfileStreamerListener( ProfilingService profilingService,
                                         StreamingProfileService streamingProfileService, String aggregateProfileId,
                                         AggregateProfileService aggregateProfileService ) {
    this.profilingService = profilingService;
    this.streamingProfileService = streamingProfileService;
    this.aggregateProfileId = aggregateProfileId;
    this.aggregateProfileService = aggregateProfileService;
    this.dataSourceFieldValueCreator = new DataSourceFieldValueCreator();
    createStreamingProfile();
  }

  private void createStreamingProfile() {
    if ( streamingProfile != null ) {
      streamingProfile.stop();
      streamingProfileStatusManager.write( new ProfileStatusWriteOperation<Void>() {
        @Override public Void write( MutableProfileStatus profileStatus ) {
          profileStatus.setName( profileStatus.getName() + rowCount );
          return null;
        }
      } );
    }
    try {
      streamingProfileStatusManager = profilingService.create(
        new ProfileConfiguration( new StreamingProfileMetadata(), null, null ) );
      String streamingProfileId = streamingProfileStatusManager.getId();
      aggregateProfileService.addChild( aggregateProfileId, streamingProfileId );
      this.streamingProfile = streamingProfileService.getStreamingProfile( streamingProfileId );
      streamingProfile.perform( new ProfileStatusWriteOperation<Void>() {
        @Override public Void write( MutableProfileStatus profileStatus ) {
          profileStatus.setName( ( rowCount + 1 ) + " - " );
          return null;
        }
      } );
    } catch ( ProfileCreationException e ) {
      LOGGER.error( UNABLE_TO_CREATE_STREAMING_PROFILE, e );
    }
  }

  @Override public synchronized void breakPointHit( TransDebugMeta transDebugMeta, final StepDebugMeta stepDebugMeta,
                                                    RowMetaInterface rowMetaInterface, List<Object[]> list ) {
    createStreamingProfile();
  }

  @Override public void rowReadEvent( RowMetaInterface rowMetaInterface, Object[] objects ) throws KettleStepException {

  }

  @Override public synchronized void rowWrittenEvent( RowMetaInterface rowMetaInterface, Object[] objects )
    throws KettleStepException {
    List<DataSourceFieldValue> dataSourceFieldValues = new ArrayList<DataSourceFieldValue>( objects.length );
    dataSourceFieldValueCreator.createDataSourceFields( dataSourceFieldValues, rowMetaInterface, objects );
    try {
      streamingProfile.processRecord( dataSourceFieldValues );
      rowCount++;
    } catch ( ProfileActionException e ) {
      LOGGER.error( UNABLE_TO_PROCESS_RECORD + dataSourceFieldValues, e );
    }
  }

  @Override public void errorRowWrittenEvent( RowMetaInterface rowMetaInterface, Object[] objects )
    throws KettleStepException {

  }
}
