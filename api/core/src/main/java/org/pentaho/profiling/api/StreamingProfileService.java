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

package org.pentaho.profiling.api;

import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;

import java.util.List;

/**
 * Service for interacting with StreamingProfile
 */
public interface StreamingProfileService {
  /**
   * Returns the StreamingProfile for a given profileId or null if it doesn't exist or isn't a StreamingProfile
   *
   * @param profileId the profileId
   * @return the StreamingProfile for a given profileId or null if it doesn't exist or isn't a StreamingProfile
   */
  public StreamingProfile getStreamingProfile( String profileId );

  /**
   * Sends a list of DataSourceFieldValues (corresponding to a single record or row) into the streaming profile
   *
   * @param profileId             the profileId
   * @param dataSourceFieldValues the DataSourceFieldValue objects making up the record
   * @throws ProfileActionException if there is a problem processing the values
   */
  public void processRecord( String profileId, List<DataSourceFieldValue> dataSourceFieldValues )
    throws ProfileActionException;
}
