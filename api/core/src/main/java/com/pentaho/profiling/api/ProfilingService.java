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
}
