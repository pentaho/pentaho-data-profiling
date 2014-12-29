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
import com.pentaho.profiling.api.action.ProfileActionExecutionCallback;
import com.pentaho.profiling.api.action.ProfileActionExecutor;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 9/9/14.
 */
public class ProfileOperationTest {
  @Test
  public void testNoArgConstructor() {
    ProfileOperation profileOperation = new ProfileOperationImpl() {
      @Override protected ProfileAction getNext() {
        return null;
      }

      @Override protected void resetState() {

      }
    };
    assertNull( profileOperation.getNameKey() );
    assertNull( profileOperation.getNamePath() );
    assertNull( profileOperation.getId() );
  }

  @Test
  public void testIdNamePathNameKeyConstructor() {
    String id = "id";
    String namePath = "name-path";
    String nameKey = "name-key";
    ProfileStatusManager profileStatusManager = mock( ProfileStatusManager.class );
    ProfileOperation profileOperation = new ProfileOperationImpl( id, namePath, nameKey, profileStatusManager ) {
      @Override protected ProfileAction getNext() {
        return null;
      }

      @Override protected void resetState() {

      }
    };
    assertEquals( id, profileOperation.getId() );
    assertEquals( namePath, profileOperation.getNamePath() );
    assertEquals( nameKey, profileOperation.getNameKey() );
  }

  @Test
  public void testSetId() {
    String id = "id";
    ProfileOperationImpl profileOperation = new ProfileOperationImpl() {
      @Override protected ProfileAction getNext() {
        return null;
      }

      @Override protected void resetState() {

      }
    };
    profileOperation.setId( id );
    assertEquals( id, profileOperation.getId() );
  }

  @Test
  public void testSetNamePath() {
    String namePath = "name-path";
    ProfileOperationImpl profileOperation = new ProfileOperationImpl() {
      @Override protected ProfileAction getNext() {
        return null;
      }

      @Override protected void resetState() {

      }
    };
    profileOperation.setNamePath( namePath );
    assertEquals( namePath, profileOperation.getNamePath() );
  }

  @Test
  public void testSetNameKey() {
    String nameKey = "name-key";
    ProfileOperationImpl profileOperation = new ProfileOperationImpl() {
      @Override protected ProfileAction getNext() {
        return null;
      }

      @Override protected void resetState() {

      }
    };
    profileOperation.setNameKey( nameKey );
    assertEquals( nameKey, profileOperation.getNameKey() );
  }

  @Test
  public void testStartStop() {
    final ProfileAction profileAction = mock( ProfileAction.class );
    ProfileStatusMessage profileStatusMessage = mock( ProfileStatusMessage.class );
    when( profileAction.getCurrentOperationMessage() ).thenReturn( profileStatusMessage );
    final AtomicBoolean resetState = new AtomicBoolean( false );
    ProfileStatusManager profileStatusManager = mock( ProfileStatusManager.class );
    final MutableProfileStatus mutableProfileStatus = mock( MutableProfileStatus.class );
    ProfileOperationImpl profileOperation = new ProfileOperationImpl( null, null, null, profileStatusManager ) {
      @Override protected ProfileAction getNext() {
        return profileAction;
      }

      @Override protected void resetState() {
        resetState.set( true );
      }
    };
    ProfileActionExecutor profileActionExecutor = mock( ProfileActionExecutor.class );
    when( profileStatusManager.write( any( ProfileStatusWriteOperation.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        return ( (ProfileStatusWriteOperation) invocation.getArguments()[0] ).write( mutableProfileStatus );
      }
    } );
    profileOperation.start( profileActionExecutor );
    verify( mutableProfileStatus ).setOperationError( null );
    verify( mutableProfileStatus ).setCurrentOperationMessage( profileStatusMessage );
    verify( profileActionExecutor ).submit( eq( profileAction ), any( ProfileActionExecutionCallback.class ) );
    assertFalse( resetState.get() );
    profileOperation.stop();
    verify( mutableProfileStatus ).setCurrentOperationMessage( null );
    assertFalse( profileOperation.isRunning() );
    assertTrue( resetState.get() );
  }
}
