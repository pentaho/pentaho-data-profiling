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

package com.pentaho.profiling.model;

import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.Profile;
import com.pentaho.profiling.api.ProfileFactory;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.configuration.DataSourceMetadata;
import com.pentaho.profiling.api.configuration.ProfileConfiguration;
import com.pentaho.profiling.api.configuration.core.StreamingProfileMetadata;
import com.pentaho.profiling.api.metrics.MetricContributorsFactory;

/**
 * Created by bryan on 3/23/15.
 */
public class StreamingProfileFactory implements ProfileFactory {
  private final StreamingProfileServiceImpl streamingProfileService;
  private final MetricContributorsFactory metricContributorsFactory;

  public StreamingProfileFactory( StreamingProfileServiceImpl streamingProfileService,
                                  MetricContributorsFactory metricContributorsFactory ) {
    this.streamingProfileService = streamingProfileService;
    this.metricContributorsFactory = metricContributorsFactory;
  }

  @Override public boolean accepts( DataSourceMetadata dataSourceMetadata ) {
    return StreamingProfileMetadata.class.isInstance( dataSourceMetadata );
  }

  @Override
  public Profile create( ProfileConfiguration profileConfiguration, ProfileStatusManager profileStatusManager ) {
    StreamingProfileMetadata dataSourceMetadata =
      (StreamingProfileMetadata) profileConfiguration.getDataSourceMetadata();
    final String name = dataSourceMetadata.getName();
    if ( name != null ) {
      profileStatusManager.write( new ProfileStatusWriteOperation<Void>() {
        @Override public Void write( MutableProfileStatus profileStatus ) {
          profileStatus.setName( name );
          return null;
        }
      } );
    }
    StreamingProfileImpl streamingProfile =
      new StreamingProfileImpl( profileStatusManager, metricContributorsFactory,
        profileConfiguration.getMetricContributors() );
    streamingProfileService.registerStreamingProfile( streamingProfile );
    return streamingProfile;
  }
}
