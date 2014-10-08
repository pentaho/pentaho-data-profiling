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

package com.pentaho.profiling.model.operations;

import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfilingService;
import com.pentaho.profiling.api.action.ProfileAction;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 10/8/14.
 */
public class DiscardOperationTest {
  private String id = "test-id";
  private ProfilingService profilingService;
  private ProfileStatusManager profileStatusManager;
  private DiscardOperation discardOperation;

  @Before
  public void setup() {
    profilingService = mock( ProfilingService.class );
    profileStatusManager = mock( ProfileStatusManager.class );
    when( profileStatusManager.getId() ).thenReturn( id );
    discardOperation = new DiscardOperation( profilingService, profileStatusManager );
  }

  @Test
  public void testGetNext() {
    ProfileAction next = discardOperation.getNext();
    assertNull( next.getCurrentOperationMessage() );
    next.execute();
    verify( profilingService ).discardProfile( id );
    assertNull( discardOperation.getNext() );
  }

  @Test
  public void testReset() {
    discardOperation.resetState();
    verifyNoMoreInteractions( profilingService );
    verifyNoMoreInteractions( profileStatusManager );
  }
}
