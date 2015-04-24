/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2015 Pentaho Corporation (Pentaho). All rights reserved.
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
import com.pentaho.profiling.api.Profile;
import com.pentaho.profiling.api.ProfileState;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusMessage;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.StreamingProfile;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.action.ProfileActionExceptionWrapper;
import com.pentaho.profiling.api.mapper.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

/**
 * Created by bryan on 4/23/15.
 */
public class MapperProfileImpl implements Profile {
  private static final Logger LOGGER = LoggerFactory.getLogger( MapperProfileImpl.class );
  private final Mapper mapper;
  private final StreamingProfile streamingProfile;
  private final ProfileStatusManager profileStatusManager;

  public MapperProfileImpl( Mapper mapper, StreamingProfile streamingProfile,
                            ProfileStatusManager profileStatusManager ) {
    this.mapper = mapper;
    this.streamingProfile = streamingProfile;
    streamingProfile.setHasStatusMessages( mapper );
    this.profileStatusManager = profileStatusManager;
  }

  @Override public String getId() {
    return streamingProfile.getId();
  }

  @Override public String getName() {
    return streamingProfile.getName();
  }

  @Override public void start( ExecutorService executorService ) {
    streamingProfile.start( executorService );
    executorService.submit( new Runnable() {
      @Override public void run() {
        try {
          mapper.run();
          profileStatusManager.write( new ProfileStatusWriteOperation<Void>() {
            @Override public Void write( MutableProfileStatus profileStatus ) {
              profileStatus.setProfileState( ProfileState.FINISHED_SUCCESSFULLY );
              profileStatus.setStatusMessages( new ArrayList<ProfileStatusMessage>() );
              return null;
            }
          } );
        } catch ( final ProfileActionException e ) {
          profileStatusManager.write( new ProfileStatusWriteOperation<Void>() {
            @Override public Void write( MutableProfileStatus profileStatus ) {
              profileStatus.setOperationError( new ProfileActionExceptionWrapper( e ) );
              return null;
            }
          } );
        } catch ( Exception e ) {
          LOGGER.error( e.getMessage(), e );
        }
      }
    } );
  }

  @Override public void stop() {
    streamingProfile.stop();
    mapper.stop();
  }

  @Override public boolean isRunning() {
    return mapper.isRunning();
  }
}
