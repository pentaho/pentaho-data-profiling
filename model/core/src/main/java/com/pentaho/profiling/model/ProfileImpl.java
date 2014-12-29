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

import com.pentaho.profiling.api.Profile;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfilingService;
import com.pentaho.profiling.api.action.ProfileActionExecutor;
import com.pentaho.profiling.api.operations.ProfileOperation;
import com.pentaho.profiling.api.operations.ProfileOperationProvider;
import com.pentaho.profiling.model.operations.DiscardOperation;
import com.pentaho.profiling.model.operations.RetryOperation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bryan on 10/3/14.
 */
public class ProfileImpl implements Profile {
  public static final String KEY_PATH = "profiling-model/com.pentaho.profiling.model.operations.messages";
  private final String id;
  private final ProfileActionExecutor profileActionExecutor;
  private final ProfileStatusManager profileStatusManager;
  private final ProfileOperation discardOperation;
  private final ProfileOperation retryOperation;
  private List<ProfileOperation> profileOperations;
  private Map<String, ProfileOperation> profileOperationMap;
  private ProfileOperation currentProfileOperation;

  public ProfileImpl( String id, ProfileActionExecutor profileActionExecutor, ProfileStatusManager profileStatusManager,
                      ProfilingService profilingService ) {
    this.id = id;
    this.profileActionExecutor = profileActionExecutor;
    this.profileStatusManager = profileStatusManager;
    discardOperation = new DiscardOperation( profilingService, profileStatusManager );
    retryOperation = new RetryOperation();
  }

  public void setProfileOperationProvider( ProfileOperationProvider profileOperationProvider ) {
    this.profileOperations = Collections
      .unmodifiableList( new ArrayList<ProfileOperation>( profileOperationProvider.getProfileOperations() ) );
    profileOperationMap = new HashMap<String, ProfileOperation>();
    for ( ProfileOperation profileOperation : profileOperations ) {
      profileOperationMap.put( profileOperation.getId(), profileOperation );
    }
    profileOperationMap.put( DiscardOperation.PROFILE_OPERATION_DISCARD_KEY, discardOperation );
    if ( profileOperationProvider.getInitialOperation() != null ) {
      startOperation( profileOperationProvider.getInitialOperation().getId() );
    }
  }


  @Override public String getId() {
    return id;
  }

  @Override public synchronized void stopCurrentOperation() {
    if ( currentProfileOperation != null ) {
      currentProfileOperation.stop();
    }
  }

  @Override public List<ProfileOperation> getProfileOperations() {
    return profileOperations;
  }

  @Override public synchronized void startOperation( String operationId ) {
    if ( currentProfileOperation == null || !currentProfileOperation.isRunning() ) {
      // Special case handling necessary for retry as we don't want to wipe out last operation
      if ( RetryOperation.PROFILE_OPERATION_RETRY_KEY.equals( operationId ) ) {
        if ( currentProfileOperation != null ) {
          currentProfileOperation.start( profileActionExecutor );
        }
      } else {
        ProfileOperation profileOperation = profileOperationMap.get( operationId );
        currentProfileOperation = profileOperation;
        profileOperation.start( profileActionExecutor );
      }
    }
  }

  @Override public ProfileOperation getProfileDiscardOperation() {
    return discardOperation;
  }

  @Override public ProfileOperation getRetryOperation() {
    return retryOperation;
  }
}
