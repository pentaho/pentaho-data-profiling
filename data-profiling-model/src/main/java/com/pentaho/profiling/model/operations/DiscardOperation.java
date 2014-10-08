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
import com.pentaho.profiling.api.ProfileStatusMessage;
import com.pentaho.profiling.api.ProfilingService;
import com.pentaho.profiling.api.action.ProfileAction;
import com.pentaho.profiling.api.action.ProfileActionResult;
import com.pentaho.profiling.api.operations.ProfileOperationImpl;
import com.pentaho.profiling.model.ProfileImpl;

/**
 * Created by bryan on 10/3/14.
 */
public class DiscardOperation extends ProfileOperationImpl {
  public static final String PROFILE_OPERATION_DISCARD_KEY = "ProfileDiscard";
  private ProfileAction next;

  public DiscardOperation( final ProfilingService profilingService, final ProfileStatusManager profileStatusManager ) {
    super( PROFILE_OPERATION_DISCARD_KEY, ProfileImpl.KEY_PATH, PROFILE_OPERATION_DISCARD_KEY, profileStatusManager );
    next = new ProfileAction() {
      @Override public ProfileActionResult execute() {
        profilingService.discardProfile( profileStatusManager.getId() );
        return null;
      }

      @Override public ProfileStatusMessage getCurrentOperationMessage() {
        return null;
      }
    };
  }

  @Override protected ProfileAction getNext() {
    ProfileAction result = next;
    next = null;
    return result;
  }

  @Override protected void resetState() {

  }
}
