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
