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

import org.pentaho.profiling.api.MutableProfileStatus;
import org.pentaho.profiling.api.Profile;
import org.pentaho.profiling.api.ProfileFactory;
import org.pentaho.profiling.api.ProfileStatusManager;
import org.pentaho.profiling.api.ProfileStatusWriteOperation;
import org.pentaho.profiling.api.configuration.DataSourceMetadata;
import org.pentaho.profiling.api.configuration.ProfileConfiguration;
import org.pentaho.profiling.api.configuration.core.AggregateProfileMetadata;
import org.pentaho.profiling.api.metrics.MetricContributorsFactory;

/**
 * Created by bryan on 3/5/15.
 */
public class AggregateProfileFactory implements ProfileFactory {
  private final ProfilingServiceImpl profilingService;
  private final AggregateProfileServiceImpl aggregateProfileService;
  private final MetricContributorsFactory metricContributorsFactory;

  public AggregateProfileFactory( ProfilingServiceImpl profilingService,
                                  AggregateProfileServiceImpl aggregateProfileService,
                                  MetricContributorsFactory metricContributorsFactory ) {
    this.profilingService = profilingService;
    this.aggregateProfileService = aggregateProfileService;
    this.metricContributorsFactory = metricContributorsFactory;
  }

  @Override public boolean accepts( DataSourceMetadata dataSourceMetadata ) {
    return AggregateProfileMetadata.class.isInstance( dataSourceMetadata );
  }

  @Override
  public Profile create( ProfileConfiguration profileConfiguration, ProfileStatusManager profileStatusManager ) {
    AggregateProfileMetadata aggregateProfileMetadata =
      (AggregateProfileMetadata) profileConfiguration.getDataSourceMetadata();
    final String name = aggregateProfileMetadata.getName();
    if ( name != null ) {
      profileStatusManager.write( new ProfileStatusWriteOperation<Void>() {
        @Override public Void write( MutableProfileStatus profileStatus ) {
          profileStatus.setName( name );
          return null;
        }
      } );
    }
    AggregateProfileImpl aggregateProfile =
      new AggregateProfileImpl( profileStatusManager, profilingService, metricContributorsFactory,
        profileConfiguration.getMetricContributors() );
    aggregateProfileService.registerAggregateProfile( aggregateProfile );
    return aggregateProfile;
  }
}
