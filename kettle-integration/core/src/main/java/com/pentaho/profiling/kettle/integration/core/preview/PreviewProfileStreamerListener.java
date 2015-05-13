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

package com.pentaho.profiling.kettle.integration.core.preview;

import com.pentaho.profiling.api.AggregateProfileService;
import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.ProfileCreationException;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.ProfilingService;
import com.pentaho.profiling.api.StreamingProfile;
import com.pentaho.profiling.api.StreamingProfileService;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.configuration.ProfileConfiguration;
import com.pentaho.profiling.api.configuration.core.StreamingProfileMetadata;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
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

import static com.pentaho.plugin.util.DataSourceFieldValueCreator.createDataSourceFields;

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
    createDataSourceFields( dataSourceFieldValues, rowMetaInterface, objects );
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
