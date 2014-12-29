/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
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

package com.pentaho.profiling.api.operations;

import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusMessage;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.action.ProfileAction;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.action.ProfileActionExecutor;
import com.pentaho.profiling.api.action.ProfileActionResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 10/7/14.
 */
public class ProfileOperationActionExecutionCallbackTest {
  private ProfileOperationImpl profileOperation;
  private ProfileActionExecutor profileActionExecutor;
  private ProfileStatusManager profileStatusManager;
  private AtomicBoolean running;
  private ProfileActionResult profileActionResult;
  private ProfileOperationActionExecutionCallback profileOperationActionExecutionCallback;

  @Before
  public void setup() {
    profileOperation = mock( ProfileOperationImpl.class );
    profileActionExecutor = mock( ProfileActionExecutor.class );
    profileStatusManager = mock( ProfileStatusManager.class );
    running = new AtomicBoolean( true );
    profileActionResult = mock( ProfileActionResult.class );
    profileOperationActionExecutionCallback =
      new ProfileOperationActionExecutionCallback( profileOperation, profileActionExecutor, profileStatusManager,
        running );
  }

  @Test
  public void testNotRunning() {
    running.set( false );
    profileOperationActionExecutionCallback.call( profileActionResult );
    verifyNoMoreInteractions( profileActionResult );
  }

  @Test
  public void testProfileException() {
    ProfileActionException profileActionException = mock( ProfileActionException.class );
    when( profileActionResult.getProfileException() ).thenReturn( profileActionException );
    profileOperationActionExecutionCallback.call( profileActionResult );
    verify( profileActionResult ).apply( profileStatusManager );
    verifyNoMoreInteractions( profileStatusManager );
    verify( profileOperation ).stop();
  }

  @Test
  public void testNullNext() {
    profileOperationActionExecutionCallback.call( profileActionResult );
    verify( profileActionResult ).apply( profileStatusManager );
    verifyNoMoreInteractions( profileStatusManager );
    verify( profileOperation ).stop();
  }

  @Test
  public void testStopDuringApply() {
    ProfileAction next = mock( ProfileAction.class );
    when( profileOperation.getNext() ).thenReturn( next );
    doAnswer( new Answer<Void>() {
      @Override public Void answer( InvocationOnMock invocation ) throws Throwable {
        running.set( false );
        return null;
      }
    } ).when( profileActionResult ).apply( profileStatusManager );
    profileOperationActionExecutionCallback.call( profileActionResult );
    verify( profileActionResult ).apply( profileStatusManager );
    verifyNoMoreInteractions( profileStatusManager );
    verify( profileOperation ).stop();
  }

  @Test
  public void testWithNext() {
    ProfileAction next = mock( ProfileAction.class );
    ProfileStatusMessage message = mock( ProfileStatusMessage.class );
    when( next.getCurrentOperationMessage() ).thenReturn( message );
    final MutableProfileStatus mutableProfileStatus = mock( MutableProfileStatus.class );
    when( profileOperation.getNext() ).thenReturn( next );
    when( profileStatusManager.write( any( ProfileStatusWriteOperation.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        return ( (ProfileStatusWriteOperation) invocation.getArguments()[ 0 ] ).write( mutableProfileStatus );
      }
    } );
    profileOperationActionExecutionCallback.call( profileActionResult );
    verify( profileActionResult ).apply( profileStatusManager );
    verify( mutableProfileStatus ).setOperationError( null );
    verify( mutableProfileStatus ).setCurrentOperationMessage( message );
  }
}
