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

import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.ProfileState;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusMessage;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.StreamingProfile;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.action.ProfileActionExceptionWrapper;
import com.pentaho.profiling.api.mapper.Mapper;
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
