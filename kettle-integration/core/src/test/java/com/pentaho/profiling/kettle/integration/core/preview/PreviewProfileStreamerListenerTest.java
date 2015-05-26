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
import com.pentaho.profiling.api.metrics.LoggingEventUtil;
import com.pentaho.profiling.api.metrics.TestAppender;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 4/2/15.
 */
public class PreviewProfileStreamerListenerTest {
  private TestAppender originalTestAppender;
  private Map<String, LoggingEvent> loggingEventMap;
  private ProfilingService profilingService;
  private StreamingProfileService streamingProfileService;
  private String aggregateProfileId;
  private AggregateProfileService aggregateProfileService;

  @Before
  public void setup() {
    originalTestAppender = TestAppender.getInstance();
    loggingEventMap = new HashMap<String, LoggingEvent>();
    TestAppender.setInstance( new TestAppender( LoggingEventUtil.getMessageRecordingList( loggingEventMap ) ) );
    profilingService = mock( ProfilingService.class );
    streamingProfileService = mock( StreamingProfileService.class );
    aggregateProfileId = "aggregate-id";
    aggregateProfileService = mock( AggregateProfileService.class );
  }

  @After
  public void teardown() {
    TestAppender.setInstance( originalTestAppender );
  }

  @Test
  public void testListener()
    throws ProfileCreationException, KettleStepException, KettleValueException, ProfileActionException {
    String profileStatusManager1Id = "profileStatusManager1";
    String profileStatusManager2Id = "profileStatusManager2";
    String testFieldName = "test-name";
    String testData = "test-data";
    final ProfileStatusManager profileStatusManager1 = mock( ProfileStatusManager.class );
    final ProfileStatusManager profileStatusManager2 = mock( ProfileStatusManager.class );
    final MutableProfileStatus mutableProfileStatus1 = mock( MutableProfileStatus.class );
    final MutableProfileStatus mutableProfileStatus2 = mock( MutableProfileStatus.class );
    StreamingProfile streamingProfile1 = mock( StreamingProfile.class );
    when( streamingProfile1.perform( any( ProfileStatusWriteOperation.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        return profileStatusManager1.write( (ProfileStatusWriteOperation<Object>) invocation.getArguments()[0] );
      }
    } );
    StreamingProfile streamingProfile2 = mock( StreamingProfile.class );
    when( streamingProfile2.perform( any( ProfileStatusWriteOperation.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        return profileStatusManager2.write( (ProfileStatusWriteOperation<Object>) invocation.getArguments()[0] );
      }
    } );
    RowMetaInterface rowMetaInterface = mock( RowMetaInterface.class );
    ValueMetaInterface valueMetaInterface = mock( ValueMetaInterface.class );
    when( valueMetaInterface.getName() ).thenReturn( testFieldName );
    when( valueMetaInterface.getNativeDataType( any( Object.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        return invocation.getArguments()[ 0 ];
      }
    } ).thenThrow( new KettleValueException() );
    when( profileStatusManager1.write( isA( ProfileStatusWriteOperation.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        return ( (ProfileStatusWriteOperation) invocation.getArguments()[ 0 ] ).write( mutableProfileStatus1 );
      }
    } );
    when( profileStatusManager2.write( isA( ProfileStatusWriteOperation.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        return ( (ProfileStatusWriteOperation) invocation.getArguments()[ 0 ] ).write( mutableProfileStatus2 );
      }
    } );
    when( profilingService.create( any( ProfileConfiguration.class ) ) ).thenReturn( profileStatusManager1 )
      .thenReturn( profileStatusManager2 ).thenThrow( new ProfileCreationException( null, null ) );
    when( profileStatusManager1.getId() ).thenReturn( profileStatusManager1Id );
    when( profileStatusManager2.getId() ).thenReturn( profileStatusManager2Id );
    when( rowMetaInterface.getValueMetaList() ).thenReturn( Arrays.asList( valueMetaInterface ) );
    when( streamingProfileService.getStreamingProfile( profileStatusManager1Id ) ).thenReturn( streamingProfile1 );
    when( streamingProfileService.getStreamingProfile( profileStatusManager2Id ) ).thenReturn( streamingProfile2 );
    doThrow( new ProfileActionException( null, null ) ).when( streamingProfile2 ).processRecord( anyList() );
    PreviewProfileStreamerListener previewProfileStreamerListener =
      new PreviewProfileStreamerListener( profilingService, streamingProfileService, aggregateProfileId,
        aggregateProfileService );
    verify( aggregateProfileService ).addChild( aggregateProfileId, profileStatusManager1Id );
    String mutableProfileStatus1Name = "1 - ";
    verify( mutableProfileStatus1 ).setName( mutableProfileStatus1Name );
    when( mutableProfileStatus1.getName() ).thenReturn( mutableProfileStatus1Name );
    previewProfileStreamerListener.rowWrittenEvent( rowMetaInterface, new Object[] { testData } );
    ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass( List.class );
    verify( streamingProfile1 ).processRecord( argumentCaptor.capture() );
    List<DataSourceFieldValue> dataSourceFieldValues = argumentCaptor.getValue();
    assertEquals( 1, dataSourceFieldValues.size() );
    DataSourceFieldValue dataSourceFieldValue = dataSourceFieldValues.get( 0 );
    assertEquals( testFieldName, dataSourceFieldValue.getLogicalName() );
    assertEquals( testFieldName, dataSourceFieldValue.getPhysicalName() );
    assertEquals( testData, dataSourceFieldValue.getFieldValue() );
    previewProfileStreamerListener.breakPointHit( null, null, null, null );
    verify( mutableProfileStatus1 ).setName( "1 - 1" );
    String mutableProfileStatus2Name = "2 - ";
    verify( mutableProfileStatus2 ).setName( mutableProfileStatus2Name );
    previewProfileStreamerListener.rowWrittenEvent( rowMetaInterface, new Object[] { testData } );
    assertNotNull(
      loggingEventMap.get( PreviewProfileStreamerListener.UNABLE_TO_ADD_FIELD + valueMetaInterface.getName() ) );
    previewProfileStreamerListener.breakPointHit( null, null, null, null );
    assertNotNull( loggingEventMap.get( PreviewProfileStreamerListener.UNABLE_TO_CREATE_STREAMING_PROFILE ) );
    assertNotNull( loggingEventMap.get(
      PreviewProfileStreamerListener.UNABLE_TO_PROCESS_RECORD + new ArrayList<DataSourceFieldValue>().toString() ) );
  }

  @Test
  public void testRowReadAndErrorEvents() throws KettleStepException, ProfileCreationException {
    //Testing fields we had to implement but have no code, thanks cobertura
    ProfileStatusManager profileStatusManager1 = mock( ProfileStatusManager.class );
    String value = "test-id";
    when( profileStatusManager1.getId() ).thenReturn( value );
    when( profilingService.create( any( ProfileConfiguration.class ) ) ).thenReturn( profileStatusManager1 );
    when( streamingProfileService.getStreamingProfile( value ) ).thenReturn( mock( StreamingProfile.class ) );
    PreviewProfileStreamerListener previewProfileStreamerListener =
      new PreviewProfileStreamerListener( profilingService, streamingProfileService, aggregateProfileId,
        aggregateProfileService );
    previewProfileStreamerListener.rowReadEvent( null, null );
    previewProfileStreamerListener.errorRowWrittenEvent( null, null );
  }
}
