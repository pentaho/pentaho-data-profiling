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

package com.pentaho.profiling.api.action;

import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusMessage;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.operations.ProfileOperation;
import com.pentaho.profiling.api.util.ObjectHolder;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 8/11/14.
 */
public class ExceptionProfileActionResultTest {
  @Test
  public void testExceptionProfileActionResultStoresException() {
    ProfileActionException exception = new ProfileActionException( new ProfileStatusMessage(), null );
    ExceptionProfileActionResult exceptionProfileActionResult = new ExceptionProfileActionResult( exception );
    assertEquals( exception, exceptionProfileActionResult.getProfileException() );
  }

  @Test
  public void testExceptionProfileActionResultAppliesError() {
    ProfileStatusManager profileStatusManager = mock( ProfileStatusManager.class );
    final List<ProfileOperation> recoverOperations = new ArrayList<ProfileOperation>();
    ProfileOperation mockRecover = mock( ProfileOperation.class );
    recoverOperations.add( mockRecover );
    ProfileActionException profileActionException = new ProfileActionException( new ProfileStatusMessage(
      "test-path", "test-key", new ArrayList<String>() ), null, recoverOperations );
    final ExceptionProfileActionResult exceptionProfileActionResult = new ExceptionProfileActionResult(
      profileActionException );
    final ObjectHolder<ProfileActionExceptionWrapper> objectHolder = new ObjectHolder<ProfileActionExceptionWrapper>();
    when( profileStatusManager.write( any( ProfileStatusWriteOperation.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        ArgumentCaptor<ProfileActionExceptionWrapper> exceptionWrapperArgumentCaptor =
          ArgumentCaptor.forClass( ProfileActionExceptionWrapper.class );
        MutableProfileStatus mutableProfileStatus = mock( MutableProfileStatus.class );
        ( (ProfileStatusWriteOperation) invocation.getArguments()[ 0 ] ).write( mutableProfileStatus );
        verify( mutableProfileStatus ).setOperationError( exceptionWrapperArgumentCaptor.capture() );
        objectHolder.setObject( exceptionWrapperArgumentCaptor.getValue() );
        return null;
      }
    } );
    exceptionProfileActionResult.apply( profileStatusManager );
    assertEquals( "test-path", objectHolder.getObject().getMessage().getMessagePath() );
    assertEquals( "test-key", objectHolder.getObject().getMessage().getMessageKey() );
    assertEquals( recoverOperations, objectHolder.getObject().getRecoveryOperations() );
  }
}
