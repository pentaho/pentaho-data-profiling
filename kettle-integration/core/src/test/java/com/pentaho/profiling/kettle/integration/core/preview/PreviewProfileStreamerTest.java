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
import com.pentaho.profiling.api.ProfilingService;
import com.pentaho.profiling.api.StreamingProfileService;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.debug.StepDebugMeta;
import org.pentaho.di.trans.debug.TransDebugMeta;
import org.pentaho.di.trans.debug.TransDebugMetaWrapper;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 3/31/15.
 */
public class PreviewProfileStreamerTest {
  private LocalizationService localizationService;
  private ProfilingService profilingService;
  private AggregateProfileService aggregateProfileService;
  private StreamingProfileService streamingProfileService;
  private PreviewProfileStreamer previewProfileStreamer;

  @Before
  public void setup() {
    localizationService = mock( LocalizationService.class );
    profilingService = mock( ProfilingService.class );
    aggregateProfileService = mock( AggregateProfileService.class );
    streamingProfileService = mock( StreamingProfileService.class );
    previewProfileStreamer = new PreviewProfileStreamer( localizationService, profilingService, aggregateProfileService,
      streamingProfileService );
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
  public void testPreviewTrans() {
    TransDebugMetaWrapper transDebugMetaWrapper = mock( TransDebugMetaWrapper.class );
    Trans trans = mock( Trans.class );
    TransDebugMeta transDebugMeta = mock( TransDebugMeta.class );
    when( transDebugMetaWrapper.getTrans() ).thenReturn( trans );
    when( transDebugMetaWrapper.getTransDebugMeta() ).thenReturn( transDebugMeta );
    Map<StepMeta, StepDebugMeta> stepDebugMetaMap = new HashMap<StepMeta, StepDebugMeta>();

  }
}
