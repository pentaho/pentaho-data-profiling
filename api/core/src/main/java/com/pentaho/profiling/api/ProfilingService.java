/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
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

package com.pentaho.profiling.api;

import com.pentaho.profiling.api.configuration.DataSourceMetadata;
import com.pentaho.profiling.api.configuration.ProfileConfiguration;

import java.util.List;

/**
 * Service for creation and manipulation of profiles
 */
public interface ProfilingService {
  String PROFILES = "profiles";

  /**
   * Get the profile factory for a given data source
   *
   * @param dataSourceMetadata the data source metadata
   * @return a ProfileFactory that accepts the reference (or null if it doesn't exist)
   */
  ProfileFactory getProfileFactory( DataSourceMetadata dataSourceMetadata );

  /**
   * Return a boolean indicating whether a ProfileFactory exists for the given data source
   *
   * @param dataSourceMetadata the data source metadata
   * @return true iff there is a ProfileFactory that accepts the reference
   */
  boolean accepts( DataSourceMetadata dataSourceMetadata );

  /**
   * Creates a profile from the ProfileConfiguration and returns its initial status
   *
   * @param profileConfiguration the profile configuration
   * @return a ProfileStatusManager for the created profile
   * @throws ProfileCreationException if there is an error during profile creation
   */
  ProfileStatusManager create( ProfileConfiguration profileConfiguration ) throws ProfileCreationException;

  /**
   * Returns a list of the currently active profiles
   *
   * @return a list of the currently active profiles
   */
  List<ProfileStatusReader> getActiveProfiles();

  /**
   * Returns the profile for a given profileId
   *
   * @param profileId the profileId
   * @return the Profile
   */
  Profile getProfile( String profileId );

  /**
   * Returns the a ProfileStatusReader for the given profileId
   *
   * @param profileId the profileId
   * @return the ProfileStatusReader
   */
  ProfileStatusReader getProfileUpdate( String profileId );

  /**
   * Stops the profile with the given id
   *
   * @param profileId the profileId to stop
   */
  void stop( String profileId );

  /**
   * Stops all the running profiles
   */
  void stopAll();

  /**
   * Returns a boolean indicating whether a profile is running
   *
   * @param profileId the profileId to check
   * @return a boolean indicating whether a profile is running
   */
  boolean isRunning( String profileId );

  /**
   * Discards the profile with the given id
   *
   * @param profileId the profileId to discard
   */
  void discardProfile( String profileId );

  /**
   * Discards all profiles
   */
  void discardProfiles();
}
