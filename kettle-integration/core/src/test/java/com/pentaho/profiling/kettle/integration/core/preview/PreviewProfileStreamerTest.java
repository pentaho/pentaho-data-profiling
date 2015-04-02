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

import com.pentaho.profiling.api.AggregateProfile;
import com.pentaho.profiling.api.AggregateProfileService;
import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.ProfileCreateRequest;
import com.pentaho.profiling.api.ProfileCreationException;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.ProfilingService;
import com.pentaho.profiling.api.StreamingProfileService;
import com.pentaho.profiling.api.metrics.LoggingEventUtil;
import com.pentaho.profiling.api.metrics.TestAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.debug.StepDebugMeta;
import org.pentaho.di.trans.debug.TransDebugMeta;
import org.pentaho.di.trans.debug.TransDebugMetaWrapper;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.spoon.trans.TransGraph;
import org.pentaho.di.ui.spoon.trans.TransPreviewDialog;
import org.pentaho.osgi.i18n.LocalizationService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 3/31/15.
 */
public class PreviewProfileStreamerTest {
  private TestAppender originalTestAppender;
  private Map<String, LoggingEvent> loggingEvents;
  private LocalizationService localizationService;
  private ProfilingService profilingService;
  private AggregateProfileService aggregateProfileService;
  private StreamingProfileService streamingProfileService;
  private PreviewProfileStreamer previewProfileStreamer;

  @Before
  public void setup() {
    originalTestAppender = TestAppender.getInstance();
    loggingEvents = new HashMap<String, LoggingEvent>();
    TestAppender.setInstance( new TestAppender( LoggingEventUtil.getMessageRecordingList( loggingEvents ) ) );
    localizationService = mock( LocalizationService.class );
    profilingService = mock( ProfilingService.class );
    aggregateProfileService = mock( AggregateProfileService.class );
    streamingProfileService = mock( StreamingProfileService.class );
    previewProfileStreamer = new PreviewProfileStreamer( localizationService, profilingService, aggregateProfileService,
      streamingProfileService );
  }

  @After
  public void teardown() {
    TestAppender.setInstance( originalTestAppender );
  }

  @Test
  public void testRespondsTo() {
    Map<Class<?>, Set<String>> respondsTo = previewProfileStreamer.respondsTo();
    assertEquals( 3, respondsTo.size() );
    assertEquals( new HashSet<String>( Arrays.asList( TransGraph.PREVIEW_TRANS ) ),
      respondsTo.get( TransDebugMetaWrapper.class ) );
    assertEquals( new HashSet<String>( Arrays.asList( TransPreviewDialog.TRANS_PREVIEW_DIALOG ) ),
      respondsTo.get( TransPreviewDialog.class ) );
    assertEquals( new HashSet<String>( Arrays.asList( TransPreviewDialog.TRANS_PREVIEW_DIALOG_SET_DATA ) ),
      respondsTo.get( TransPreviewDialog.TransPreviewDialogSetDataWrapper.class ) );
  }

  @Test
  public void testPreviewTrans() throws ProfileCreationException {
    String profileId = "test-profile-id";
    String aggregateId = "test-aggregate-id";
    String transName = "trans-name";
    String stepName = "step-name";
    TransDebugMetaWrapper transDebugMetaWrapper = mock( TransDebugMetaWrapper.class );
    Trans trans = mock( Trans.class );
    TransDebugMeta transDebugMeta = mock( TransDebugMeta.class );
    StepMeta stepMeta = mock( StepMeta.class );
    StepInterface stepInterface = mock( StepInterface.class );
    StepDebugMeta stepDebugMeta = mock( StepDebugMeta.class );
    ProfileStatusManager profileStatusManager = mock( ProfileStatusManager.class );
    final MutableProfileStatus mutableProfileStatus = mock( MutableProfileStatus.class );
    AggregateProfile aggregateProfile = mock( AggregateProfile.class );
    Map<StepMeta, StepDebugMeta> stepDebugMetaMap = new HashMap<StepMeta, StepDebugMeta>();
    when( trans.getName() ).thenReturn( transName );
    when( stepMeta.getName() ).thenReturn( stepName );
    when( transDebugMetaWrapper.getTrans() ).thenReturn( trans );
    when( transDebugMetaWrapper.getTransDebugMeta() ).thenReturn( transDebugMeta );
    when( transDebugMeta.getStepDebugMetaMap() ).thenReturn( stepDebugMetaMap );
    when( profilingService.create( any( ProfileCreateRequest.class ) ) ).thenReturn( profileStatusManager );
    when( profileStatusManager.getId() ).thenReturn( profileId );
    when( aggregateProfileService.getAggregateProfile( profileId ) ).thenReturn( aggregateProfile );
    when( aggregateProfile.getId() ).thenReturn( aggregateId );
    when( trans.findBaseSteps( stepName ) ).thenReturn( Arrays.asList( stepInterface ) );
    when( profileStatusManager.write( any( ProfileStatusWriteOperation.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        return ( (ProfileStatusWriteOperation) invocation.getArguments()[ 0 ] ).write( mutableProfileStatus );
      }
    } );
    stepDebugMetaMap.put( stepMeta, stepDebugMeta );
    previewProfileStreamer.uiEvent( transDebugMetaWrapper, TransGraph.PREVIEW_TRANS );
    verify( mutableProfileStatus ).setName( transName + "." + stepName );
    verify( transDebugMeta ).addBreakPointListers( isA( PreviewProfileStreamerListener.class ) );
    verify( stepInterface ).addRowListener( isA( PreviewProfileStreamerListener.class ) );
    assertEquals( 0, loggingEvents.size() );
  }

  @Test
  public void testProfileCreateExceptionLogged() throws ProfileCreationException {
    TransDebugMetaWrapper transDebugMetaWrapper = mock( TransDebugMetaWrapper.class );
    Trans trans = mock( Trans.class );
    TransDebugMeta transDebugMeta = mock( TransDebugMeta.class );
    StepMeta stepMeta = mock( StepMeta.class );
    StepDebugMeta stepDebugMeta = mock( StepDebugMeta.class );
    Map<StepMeta, StepDebugMeta> stepDebugMetaMap = new HashMap<StepMeta, StepDebugMeta>();
    when( transDebugMetaWrapper.getTrans() ).thenReturn( trans );
    when( transDebugMetaWrapper.getTransDebugMeta() ).thenReturn( transDebugMeta );
    when( transDebugMeta.getStepDebugMetaMap() ).thenReturn( stepDebugMetaMap );
    when( profilingService.create( any( ProfileCreateRequest.class ) ) )
      .thenThrow( new ProfileCreationException( null, null ) );
    stepDebugMetaMap.put( stepMeta, stepDebugMeta );
    previewProfileStreamer.uiEvent( transDebugMetaWrapper, TransGraph.PREVIEW_TRANS );
    assertNotNull( loggingEvents.get( PreviewProfileStreamer.UNABLE_TO_CREATE_PROFILE ) );
  }
}
