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

import org.pentaho.profiling.api.AggregateProfile;
import org.pentaho.profiling.api.ProfileStatus;
import org.pentaho.profiling.api.ProfileStatusManager;
import org.pentaho.profiling.api.ProfileStatusReadOperation;
import org.pentaho.profiling.api.StreamingProfile;
import org.pentaho.profiling.api.configuration.DataSourceMetadata;
import org.pentaho.profiling.api.configuration.ProfileConfiguration;
import org.pentaho.profiling.api.configuration.core.AggregateProfileMetadata;
import org.pentaho.profiling.api.configuration.core.StreamingProfileMetadata;
import org.pentaho.profiling.api.metrics.MetricContributorService;
import org.pentaho.profiling.api.metrics.MetricContributors;
import org.pentaho.profiling.api.metrics.MetricContributorsFactory;
import org.pentaho.profiling.api.sample.SampleProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

/**
 * Created by bryan on 4/8/15.
 */
public class SampleProviderImpl implements SampleProvider {
  private final Map<Class<?>, List<Object>> samples;

  public SampleProviderImpl( MetricContributorsFactory metricContributorsFactory,
                             MetricContributorService metricContributorService, ExecutorService executorService ) {
    ProfilingServiceImpl profilingService = new ProfilingServiceImpl( executorService, metricContributorService );
    samples = new HashMap<Class<?>, List<Object>>();
    MetricContributors defaultMetricContributors =
      metricContributorService.getDefaultMetricContributors( MetricContributorService.DEFAULT_CONFIGURATION );
    AggregateProfileMetadata aggregateProfileMetadata = new AggregateProfileMetadata();
    ProfileStatusManagerImpl sampleAggregate =
      new ProfileStatusManagerImpl( UUID.randomUUID().toString(), "sampleAggregate",
        new ProfileConfiguration( aggregateProfileMetadata, null, defaultMetricContributors ), profilingService );
    StreamingProfileMetadata streamingProfileMetadata = new StreamingProfileMetadata();
    ProfileStatusManagerImpl sampleStreaming =
      new ProfileStatusManagerImpl( UUID.randomUUID().toString(), "sampleStreaming",
        new ProfileConfiguration( streamingProfileMetadata, MetricContributorService.DEFAULT_CONFIGURATION,
          null ), profilingService );
    samples.put( ProfileStatusManager.class, Arrays.<Object>asList( sampleAggregate, sampleStreaming ) );
    ProfileStatusReadOperation<ProfileStatus> profileStatusReadOperation =
      new ProfileStatusReadOperation<ProfileStatus>() {
        @Override public ProfileStatus read( ProfileStatus profileStatus ) {
          return profileStatus;
        }
      };
    samples.put( ProfileStatus.class,
      Arrays.<Object>asList( sampleAggregate.read( profileStatusReadOperation ),
        sampleStreaming.read( profileStatusReadOperation ) ) );
    samples
      .put( DataSourceMetadata.class, Arrays.<Object>asList( aggregateProfileMetadata, streamingProfileMetadata ) );
    StreamingProfileImpl streamingProfile = new StreamingProfileImpl( sampleStreaming, metricContributorsFactory,
      metricContributorService.getDefaultMetricContributors( MetricContributorService.DEFAULT_CONFIGURATION ) );
    samples.put( StreamingProfile.class, Arrays.<Object>asList(
      streamingProfile ) );
    profilingService.registerProfile( streamingProfile, sampleStreaming );
    AggregateProfileImpl aggregateProfile =
      new AggregateProfileImpl( sampleAggregate, profilingService, metricContributorsFactory,
        metricContributorService.getDefaultMetricContributors( MetricContributorService.DEFAULT_CONFIGURATION ) );
    aggregateProfile.start( executorService );
    aggregateProfile.addChildProfile( sampleStreaming.getId() );
    samples.put( AggregateProfile.class, Arrays.<Object>asList(
      aggregateProfile ) );
  }

  @Override public <T> List<T> provide( Class<T> type ) {
    return (List<T>) samples.get( type );
  }
}
