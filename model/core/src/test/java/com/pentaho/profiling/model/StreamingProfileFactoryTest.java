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

import org.pentaho.profiling.api.Profile;
import org.pentaho.profiling.api.ProfileStatusManager;
import org.pentaho.profiling.api.StreamingProfile;
import org.pentaho.profiling.api.configuration.ProfileConfiguration;
import org.pentaho.profiling.api.configuration.core.AggregateProfileMetadata;
import org.pentaho.profiling.api.configuration.core.StreamingProfileMetadata;
import org.pentaho.profiling.api.metrics.MetricContributors;
import org.pentaho.profiling.api.metrics.MetricContributorsFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by bryan on 4/2/15.
 */
public class StreamingProfileFactoryTest {
  private StreamingProfileServiceImpl streamingProfileService;
  private MetricContributorsFactory metricContributorsFactory;
  private StreamingProfileFactory streamingProfileFactory;

  @Before
  public void setup() {
    streamingProfileService = mock( StreamingProfileServiceImpl.class );
    metricContributorsFactory = mock( MetricContributorsFactory.class );
    streamingProfileFactory = new StreamingProfileFactory( streamingProfileService, metricContributorsFactory );
  }

  @Test
  public void testAccepts() {
    assertTrue( streamingProfileFactory.accepts( new StreamingProfileMetadata() ) );
    assertFalse( streamingProfileFactory.accepts( new AggregateProfileMetadata() ) );
  }

  @Test
  public void testCreate() {
    ProfileStatusManager profileStatusManager = mock( ProfileStatusManager.class );
    MetricContributors metricContributors = mock( MetricContributors.class );
    Profile profile = streamingProfileFactory
      .create( new ProfileConfiguration( new StreamingProfileMetadata(), null, null ), profileStatusManager );
    assertTrue( profile instanceof StreamingProfile );
    verify( streamingProfileService ).registerStreamingProfile( (StreamingProfile) profile );
  }
}
