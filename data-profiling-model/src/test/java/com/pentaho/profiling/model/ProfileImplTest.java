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

import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfilingService;
import com.pentaho.profiling.api.action.ProfileActionExecutor;
import com.pentaho.profiling.api.operations.ProfileOperation;
import com.pentaho.profiling.api.operations.ProfileOperationProvider;
import com.pentaho.profiling.model.operations.DiscardOperation;
import com.pentaho.profiling.model.operations.RetryOperation;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 10/8/14.
 */
public class ProfileImplTest {
  private String id;
  private String initialOperationId;
  private ProfileActionExecutor profileActionExecutor;
  private ProfileStatusManager profileStatusManager;
  private ProfilingService profilingService;
  private ProfileOperationProvider profileOperationProvider;
  private ProfileOperation initialOperation;
  private ProfileImpl profile;

  @Before
  public void setup() {
    id = "Test-Id";
    initialOperationId = "Test-Initial-Operation";
    profileActionExecutor = mock( ProfileActionExecutor.class );
    profileStatusManager = mock( ProfileStatusManager.class );
    profilingService = mock( ProfilingService.class );
    initialOperation = mock( ProfileOperation.class );
    when( initialOperation.getId() ).thenReturn( initialOperationId );
    profileOperationProvider = mockProvider( Arrays.asList( initialOperation ), initialOperation );
    profile = new ProfileImpl( id, profileActionExecutor, profileStatusManager, profilingService );
  }

  private ProfileOperationProvider mockProvider( List<ProfileOperation> operations, ProfileOperation initialOperation ) {
    ProfileOperationProvider profileOperationProvider = mock( ProfileOperationProvider.class );
    when( profileOperationProvider.getProfileOperations() ).thenReturn( operations );
    when( profileOperationProvider.getInitialOperation() ).thenReturn( initialOperation );
    return profileOperationProvider;
  }

  @Test
  public void testGetId() {
    assertEquals( id, profile.getId() );
  }

  @Test
  public void testSetProfileOperationProvider() {
    profile.setProfileOperationProvider( profileOperationProvider );
    verify( initialOperation ).start( profileActionExecutor );
  }

  @Test
  public void testGetOperations() {
    profile.setProfileOperationProvider( profileOperationProvider );
    assertEquals( 1, profile.getProfileOperations().size() );
    assertEquals( initialOperation, profile.getProfileOperations().get( 0 ) );
  }

  @Test
  public void testStopCurrentOperation() {
    profile.setProfileOperationProvider( profileOperationProvider );
    verify( initialOperation, times( 1 ) ).start( profileActionExecutor );
    profile.stopCurrentOperation();
    verify( initialOperation ).stop();
    // Shouldn't NPE
    profile.stopCurrentOperation();
  }

  @Test
  public void testStartOperationRetry() {
    profile.setProfileOperationProvider( profileOperationProvider );
    profile.stopCurrentOperation();
    profile.startOperation( RetryOperation.PROFILE_OPERATION_RETRY_KEY );
    verify( initialOperation, times( 2 ) ).start( profileActionExecutor );
  }

  @Test
  public void testStartOperationNoPrev() {
    profile.setProfileOperationProvider( mockProvider( Arrays.asList( initialOperation ), null ) );
    // Shouldn't npe
    profile.stopCurrentOperation();
  }

  @Test
  public void testStartOperation() {
    profile.setProfileOperationProvider( mockProvider( Arrays.asList( initialOperation ), null ) );
    verify( initialOperation, never() ).start( any( ProfileActionExecutor.class ) );
    profile.startOperation( initialOperationId );
    verify( initialOperation ).start( profileActionExecutor );
  }

  @Test
  public void testStartOperationRunning() {
    profile.setProfileOperationProvider( profileOperationProvider );
    when( initialOperation.isRunning() ).thenReturn( true );
    // Should noop
    profile.startOperation( "other-id" );
    verify( initialOperation, never() ).stop();
  }

  @Test
  public void testGetRetryOperation() {
    assertTrue( profile.getRetryOperation() instanceof RetryOperation );
  }

  @Test
  public void testGetDiscardOperation() {
    assertTrue( profile.getProfileDiscardOperation() instanceof DiscardOperation );
  }
}
