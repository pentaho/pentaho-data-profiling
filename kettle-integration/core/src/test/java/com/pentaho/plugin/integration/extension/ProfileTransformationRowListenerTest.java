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

package com.pentaho.plugin.integration.extension;

import com.pentaho.plugin.util.DataSourceFieldValueCreator;
import com.pentaho.profiling.api.StreamingProfile;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.row.RowMetaInterface;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Created by bryan on 5/14/15.
 */
public class ProfileTransformationRowListenerTest {
  private LogChannelInterface logChannelInterface;
  private StreamingProfile streamingProfile;
  private List<DataSourceFieldValue> dataSourceFieldValues;
  private DataSourceFieldValueCreator dataSourceFieldValueCreator;
  private ProfileTransformationRowListener profileTransformationRowListener;

  @Before
  public void setup() {
    logChannelInterface = mock( LogChannelInterface.class );
    streamingProfile = mock( StreamingProfile.class );
    dataSourceFieldValues = mock( List.class );
    dataSourceFieldValueCreator = mock( DataSourceFieldValueCreator.class );
    profileTransformationRowListener =
      new ProfileTransformationRowListener( logChannelInterface, streamingProfile, dataSourceFieldValueCreator,
        dataSourceFieldValues );
  }

  @Test
  public void testTwoArgConstructor() {
    new ProfileTransformationRowListener( logChannelInterface, streamingProfile );
    verifyNoMoreInteractions( logChannelInterface, streamingProfile );
  }

  @Test
  public void testRowReadEvent() throws KettleStepException {
    profileTransformationRowListener.rowReadEvent( null, null );
    verifyNoMoreInteractions( logChannelInterface, streamingProfile );
  }

  @Test
  public void testErrorRowWrittenEvent() throws KettleStepException {
    profileTransformationRowListener.errorRowWrittenEvent( null, null );
    verifyNoMoreInteractions( logChannelInterface, streamingProfile );
  }

  @Test
  public void testRowWrittenEvent() throws KettleStepException, ProfileActionException {
    RowMetaInterface rowMetaInterface = mock( RowMetaInterface.class );
    Object[] row = new Object[] {};
    profileTransformationRowListener.rowWrittenEvent( rowMetaInterface, row );
    verify( dataSourceFieldValues ).clear();
    verify( dataSourceFieldValueCreator ).createDataSourceFields( dataSourceFieldValues, rowMetaInterface, row );
    verify( streamingProfile ).processRecord( dataSourceFieldValues );
    verifyNoMoreInteractions( logChannelInterface, streamingProfile, dataSourceFieldValueCreator,
      dataSourceFieldValueCreator );
  }

  @Test
  public void testRowWrittenEventException() throws KettleStepException, ProfileActionException {
    RowMetaInterface rowMetaInterface = mock( RowMetaInterface.class );
    Object[] row = new Object[] {};
    doThrow( new ProfileActionException( null, null ) ).when( streamingProfile ).processRecord( dataSourceFieldValues );
    profileTransformationRowListener.rowWrittenEvent( rowMetaInterface, row );
    verify( dataSourceFieldValues ).clear();
    verify( dataSourceFieldValueCreator ).createDataSourceFields( dataSourceFieldValues, rowMetaInterface, row );
    verify( streamingProfile ).processRecord( dataSourceFieldValues );
    ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass( String.class );
    verify( logChannelInterface ).logError( stringArgumentCaptor.capture(), any( ProfileActionException.class ) );
    assertTrue(
      stringArgumentCaptor.getValue().startsWith( ProfileTransformationRowListener.UNABLE_TO_PROCESS_RECORD ) );
    verifyNoMoreInteractions( logChannelInterface, streamingProfile, dataSourceFieldValueCreator,
      dataSourceFieldValueCreator );
  }
}
