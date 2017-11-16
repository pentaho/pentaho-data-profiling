/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.profiling.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.pentaho.profiling.api.MutableProfileStatus;
import org.pentaho.profiling.api.Profile;
import org.pentaho.profiling.api.ProfileState;
import org.pentaho.profiling.api.ProfileStatusManager;
import org.pentaho.profiling.api.ProfileStatusMessage;
import org.pentaho.profiling.api.ProfileStatusWriteOperation;
import org.pentaho.profiling.api.StreamingProfile;
import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.action.ProfileActionExceptionWrapper;
import org.pentaho.profiling.api.mapper.Mapper;
import org.pentaho.profiling.api.performance.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
        Collector collector = new Collector( Thread.currentThread() );
        collector.start();
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
              profileStatus.setProfileState( ProfileState.FINISHED_ERRORS );
              profileStatus.setOperationError( new ProfileActionExceptionWrapper( e ) );
              return null;
            }
          } );
        } catch ( Exception e ) {
          profileStatusManager.write( new ProfileStatusWriteOperation<Void>() {
            @Override public Void write( MutableProfileStatus profileStatus ) {
              profileStatus.setProfileState( ProfileState.FINISHED_ERRORS );
              return null;
            }
          } );
          LOGGER.error( e.getMessage(), e );
        } finally {
          try {
            collector.stop();
            new ObjectMapper()
              .writeValue( new File( "/tmp/" + profileStatusManager.getId() ), collector.getRootNode() );
          } catch ( Throwable e ) {
            LOGGER.warn( e.getMessage(), e );
          }
        }
      }
    } );
  }

  @Override public void commit() {
    mapper.commit();
  }

  @Override public void stop() {
    streamingProfile.stop();
    mapper.stop();
  }

  @Override public boolean isRunning() {
    return mapper.isRunning();
  }
}
