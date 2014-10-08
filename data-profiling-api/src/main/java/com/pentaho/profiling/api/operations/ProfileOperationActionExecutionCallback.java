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
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.action.ProfileAction;
import com.pentaho.profiling.api.action.ProfileActionExecutionCallback;
import com.pentaho.profiling.api.action.ProfileActionExecutor;
import com.pentaho.profiling.api.action.ProfileActionResult;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by bryan on 10/6/14.
 */
public class ProfileOperationActionExecutionCallback implements ProfileActionExecutionCallback {
  private final ProfileOperationImpl profileOperation;
  private final ProfileActionExecutor profileActionExecutor;
  private final ProfileStatusManager profileStatusManager;
  private final AtomicBoolean running;

  public ProfileOperationActionExecutionCallback( ProfileOperationImpl profileOperation,
                                                  ProfileActionExecutor profileActionExecutor,
                                                  ProfileStatusManager profileStatusManager, AtomicBoolean running ) {
    this.profileOperation = profileOperation;
    this.profileActionExecutor = profileActionExecutor;
    this.profileStatusManager = profileStatusManager;
    this.running = running;
  }

  @Override public void call( ProfileActionResult profileActionResult ) {
    if ( running.get() ) {
      profileActionResult.apply( profileStatusManager );
      final ProfileAction next = profileOperation.getNext();
      if ( profileActionResult.getProfileException() == null && next != null && running.get() ) {
        profileStatusManager.write( new ProfileStatusWriteOperation<Void>() {
          @Override public Void write( MutableProfileStatus profileStatus ) {
            profileStatus.setOperationError( null );
            profileStatus.setCurrentOperationMessage( next.getCurrentOperationMessage() );
            return null;
          }
        } );
        profileActionExecutor.submit( next, this );
      } else {
        profileOperation.stop();
      }
    }
  }
}
