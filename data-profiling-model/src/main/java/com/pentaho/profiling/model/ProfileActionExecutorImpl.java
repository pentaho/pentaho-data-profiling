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

import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.action.ProfileAction;
import com.pentaho.profiling.api.action.ProfileActionExecutor;
import com.pentaho.profiling.api.action.ProfileActionResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by bryan on 8/1/14.
 */
public class ProfileActionExecutorImpl implements ProfileActionExecutor {
  private ExecutorService executorService = Executors.newCachedThreadPool();
  private ProfileNotificationProvider profileNotificationProvider;

  protected void setExecutorService( ExecutorService executorService ) {
    this.executorService = executorService;
  }

  public void setProfileNotificationProvider( ProfileNotificationProvider profileNotificationProvider ) {
    this.profileNotificationProvider = profileNotificationProvider;
  }

  @Override
  public void submit( final ProfileAction action, final ProfileStatus status ) {
    executorService.submit( new Runnable() {
      @Override
      public void run() {
        ProfileActionResult result = action.execute();
        if ( result != null ) {
          result.apply( status );
          profileNotificationProvider.notify( status.getId() );
        }
        ProfileAction then = action.then();
        if ( then != null ) {
          submit( then, status );
        }
      }
    } );
  }
}
