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

package org.pentaho.profiling.model;

import org.pentaho.profiling.api.StreamingProfile;
import org.pentaho.profiling.api.StreamingProfileService;
import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bryan on 3/23/15.
 */
public class StreamingProfileServiceImpl implements StreamingProfileService {
  private final Map<String, StreamingProfile> profileMap = new ConcurrentHashMap<String, StreamingProfile>();

  public void registerStreamingProfile( StreamingProfile streamingProfile ) {
    profileMap.put( streamingProfile.getId(), streamingProfile );
  }

  @Override public StreamingProfile getStreamingProfile( String profileId ) {
    return profileMap.get( profileId );
  }

  @Override public void processRecord( String profileId, List<DataSourceFieldValue> dataSourceFieldValues )
    throws ProfileActionException {
    profileMap.get( profileId ).processRecord( dataSourceFieldValues );
  }
}
