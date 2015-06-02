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

package org.pentaho.profiling.api;

import java.util.List;

/**
 * Service for interacting with aggregate profiles
 */
public interface AggregateProfileService {
  String AGGREGATE_PROFILES = "aggregateProfiles";
  /**
   * Returns a list of all aggregate profiles
   *
   * @return a list of all aggregate profiles
   */
  public List<AggregateProfile> getAggregateProfiles();

  /**
   * Will return the highest level aggregate profile that contains the profile with the given id
   *
   * @param profileId the profileId
   * @return the highest level aggregate profile that contains the profile with the given id
   */
  public AggregateProfile getAggregateProfile( String profileId );

  /**
   * Adds the profile with id of childProfileId as a child of the profile with the id profileId
   *
   * @param profileId      the parent profile id
   * @param childProfileId the child profile id
   */
  public void addChild( String profileId, String childProfileId );
}
