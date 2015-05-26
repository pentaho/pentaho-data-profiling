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

import com.pentaho.profiling.api.AggregateProfile;
import com.pentaho.profiling.api.Profile;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.configuration.ProfileConfiguration;
import com.pentaho.profiling.api.configuration.core.AggregateProfileMetadata;
import com.pentaho.profiling.api.configuration.core.StreamingProfileMetadata;
import com.pentaho.profiling.api.metrics.MetricContributor;
import com.pentaho.profiling.api.metrics.MetricContributorsFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by bryan on 3/6/15.
 */
public class AggregateProfileFactoryTest {
  private ProfilingServiceImpl profilingService;
  private AggregateProfileServiceImpl aggregateProfileService;
  private List<MetricContributor> metricContributors;
  private AggregateProfileFactory aggregateProfileFactory;
  private MetricContributorsFactory metricContributorsFactory;

  @Before
  public void setup() {
    profilingService = mock( ProfilingServiceImpl.class );
    aggregateProfileService = mock( AggregateProfileServiceImpl.class );
    metricContributors = mock( List.class );
    metricContributorsFactory = mock( MetricContributorsFactory.class );
    aggregateProfileFactory =
      new AggregateProfileFactory( profilingService, aggregateProfileService, metricContributorsFactory );
  }

  @Test
  public void testAccepts() {
    assertTrue( aggregateProfileFactory
      .accepts( new AggregateProfileMetadata() ) );
    assertFalse( aggregateProfileFactory.accepts( new StreamingProfileMetadata() ) );
  }

  @Test
  public void testCreate() {
    ProfileStatusManager profileStatusManager = mock( ProfileStatusManager.class );
    Profile profile = aggregateProfileFactory
      .create( new ProfileConfiguration( new AggregateProfileMetadata(), null, null ), profileStatusManager );
    assertTrue( profile instanceof AggregateProfile );
    verify( aggregateProfileService ).registerAggregateProfile( (AggregateProfile) profile );
  }
}
