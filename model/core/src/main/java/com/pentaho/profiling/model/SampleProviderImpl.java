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

import com.pentaho.profiling.api.AggregateProfile;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusReadOperation;
import com.pentaho.profiling.api.StreamingProfile;
import com.pentaho.profiling.api.configuration.DataSourceMetadata;
import com.pentaho.profiling.api.configuration.ProfileConfiguration;
import com.pentaho.profiling.api.configuration.core.AggregateProfileMetadata;
import com.pentaho.profiling.api.configuration.core.StreamingProfileMetadata;
import com.pentaho.profiling.api.metrics.MetricContributorService;
import com.pentaho.profiling.api.metrics.MetricContributors;
import com.pentaho.profiling.api.metrics.MetricContributorsFactory;
import com.pentaho.profiling.api.sample.SampleProvider;

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
    aggregateProfile.addChildProfile( sampleStreaming.getId() );
    samples.put( AggregateProfile.class, Arrays.<Object>asList(
      aggregateProfile ) );
  }

  @Override public <T> List<T> provide( Class<T> type ) {
    return (List<T>) samples.get( type );
  }
}
