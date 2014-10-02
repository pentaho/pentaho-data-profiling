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

package com.pentaho.profiling.model;

import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.action.ProfileAction;
import com.pentaho.profiling.api.action.ProfileActionExceptionWrapper;
import com.pentaho.profiling.api.action.ProfileActionResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 8/14/14.
 */
public class ProfileActionExecutorImplTest {
  private ExecutorService executorService;
  private ProfileStatusManager profileStatusManager;
  private MutableProfileStatus profileStatus;
  private ProfileAction profileAction;
  private ProfileActionResult profileActionResult;

  @Before
  public void setup() {
    executorService = mock( ExecutorService.class );
    when( executorService.submit( any( Runnable.class ) ) ).thenAnswer( new Answer<Future<?>>() {
      @Override public Future<?> answer( InvocationOnMock invocation ) throws Throwable {
        ( (Runnable) invocation.getArguments()[ 0 ] ).run();
        return null;
      }
    } );
    profileStatusManager = mock( ProfileStatusManager.class );
    profileStatus = mock( MutableProfileStatus.class );
    when( profileStatusManager.write( any( ProfileStatusWriteOperation.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        return ( (ProfileStatusWriteOperation) invocation.getArguments()[0]).write( profileStatus );
      }
    } );
    profileAction = mock( ProfileAction.class );
    profileActionResult = mock( ProfileActionResult.class );
    when( profileAction.execute() ).thenReturn( profileActionResult );
  }

  @Test
  public void testExecuteClearsOperationError() {
    String id = "test-id";
    when( profileStatus.getId() ).thenReturn( id );
    ProfileActionExceptionWrapper profileActionExceptionWrapper = mock( ProfileActionExceptionWrapper.class );
    when( profileStatus.getOperationError() ).thenReturn( profileActionExceptionWrapper );
    ProfileActionExecutorImpl profileActionExecutor = new ProfileActionExecutorImpl();
    profileActionExecutor.setExecutorService( executorService );
    profileActionExecutor.submit( profileAction, profileStatusManager );
    verify( profileActionResult ).apply( profileStatusManager );
    verify( profileStatus ).setOperationError( null );
  }

  @Test
  public void testExecuteNoThen() {
    ProfileActionExecutorImpl profileActionExecutor = new ProfileActionExecutorImpl();
    profileActionExecutor.setExecutorService( executorService );
    profileActionExecutor.submit( profileAction, profileStatusManager );
    verify( profileActionResult ).apply( profileStatusManager );
  }

  @Test
  public void testExecuteNullResult() {
    ProfileActionExecutorImpl profileActionExecutor = new ProfileActionExecutorImpl();
    profileActionExecutor.setExecutorService( executorService );
    ProfileAction profileAction = mock( ProfileAction.class );
    profileActionExecutor.submit( profileAction, profileStatusManager );
    verify( profileStatus, times( 0 ) ).setFields( anyList() );
  }

  @Test
  public void testExecuteThen() {
    ProfileAction then = mock( ProfileAction.class );
    when( profileAction.then() ).thenReturn( then );
    ProfileActionResult thenResult = mock( ProfileActionResult.class );
    when( then.execute() ).thenReturn( thenResult );
    ProfileActionExecutorImpl profileActionExecutor = new ProfileActionExecutorImpl();
    profileActionExecutor.setExecutorService( executorService );
    profileActionExecutor.submit( profileAction, profileStatusManager );
    verify( profileActionResult ).apply( profileStatusManager );
    verify( thenResult ).apply( profileStatusManager );
  }
}
