/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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

package org.pentaho.profiling.model;

import org.pentaho.profiling.api.MutableProfileStatus;
import org.pentaho.profiling.api.ProfileState;
import org.pentaho.profiling.api.ProfileStatusManager;
import org.pentaho.profiling.api.ProfileStatusMessage;
import org.pentaho.profiling.api.ProfileStatusWriteOperation;
import org.pentaho.profiling.api.StreamingProfile;
import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.action.ProfileActionExceptionWrapper;
import org.pentaho.profiling.api.mapper.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 4/29/15.
 */
public class MapperProfileImplTest {
  private Mapper mapper;
  private StreamingProfile streamingProfile;
  private ProfileStatusManager profileStatusManager;
  private MutableProfileStatus mutableProfileStatus;
  private MapperProfileImpl mapperProfile;
  private ExecutorService executorService;

  @Before
  public void setup() {
    mapper = mock( Mapper.class );
    streamingProfile = mock( StreamingProfile.class );
    profileStatusManager = mock( ProfileStatusManager.class );
    mutableProfileStatus = mock( MutableProfileStatus.class );
    when( profileStatusManager.write( any( ProfileStatusWriteOperation.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        return ( (ProfileStatusWriteOperation) invocation.getArguments()[ 0 ] ).write( mutableProfileStatus );
      }
    } );
    executorService = mock( ExecutorService.class );
    when( executorService.submit( any( Runnable.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        ( (Runnable) invocation.getArguments()[ 0 ] ).run();
        return null;
      }
    } );
    mapperProfile = new MapperProfileImpl( mapper, streamingProfile, profileStatusManager );
  }

  @Test
  public void testGetId() {
    String id = "id";
    when( streamingProfile.getId() ).thenReturn( id );
    assertEquals( id, mapperProfile.getId() );
  }

  @Test
  public void testGetName() {
    String name = "name";
    when( streamingProfile.getName() ).thenReturn( name );
    assertEquals( name, mapperProfile.getName() );
  }

  @Test
  public void testStop() {
    mapperProfile.stop();
    verify( streamingProfile ).stop();
    verify( mapper ).stop();
  }

  @Test
  public void testIsRunning() {
    when( mapper.isRunning() ).thenReturn( false ).thenReturn( true );
    assertFalse( mapperProfile.isRunning() );
    assertTrue( mapperProfile.isRunning() );
  }

  @Test
  public void testStart() throws ProfileActionException {
    mapperProfile.start( executorService );
    verify( mapper ).run();
    verify( mutableProfileStatus ).setProfileState( ProfileState.FINISHED_SUCCESSFULLY );
    verify( mutableProfileStatus ).setStatusMessages( eq( new ArrayList<ProfileStatusMessage>() ) );
  }

  @Test
  public void testStartProfileActionException() throws ProfileActionException {
    ProfileActionException profileActionException = mock( ProfileActionException.class );
    ProfileStatusMessage message = mock( ProfileStatusMessage.class );
    when( profileActionException.getProfileStatusMessage() ).thenReturn( message );
    doThrow( profileActionException ).when( mapper ).run();
    mapperProfile.start( executorService );
    verify( mapper ).run();
    verify( mutableProfileStatus ).setProfileState( ProfileState.FINISHED_ERRORS );
    ArgumentCaptor<ProfileActionExceptionWrapper> captor =
      ArgumentCaptor.forClass( ProfileActionExceptionWrapper.class );
    verify( mutableProfileStatus ).setOperationError( captor.capture() );
    assertEquals( message, captor.getValue().getMessage() );
  }

  @Test
  public void testStartRuntimeException() throws ProfileActionException {
    doThrow( new RuntimeException() ).when( mapper ).run();
    mapperProfile.start( executorService );
    verify( mapper ).run();
    verify( mutableProfileStatus ).setProfileState( ProfileState.FINISHED_ERRORS );
  }
}
