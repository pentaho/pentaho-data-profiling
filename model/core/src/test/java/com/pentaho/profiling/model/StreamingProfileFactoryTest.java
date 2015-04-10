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

import com.pentaho.profiling.api.Profile;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.StreamingProfile;
import com.pentaho.profiling.api.configuration.ProfileConfiguration;
import com.pentaho.profiling.api.configuration.core.AggregateProfileMetadata;
import com.pentaho.profiling.api.configuration.core.StreamingProfileMetadata;
import com.pentaho.profiling.api.metrics.MetricContributors;
import com.pentaho.profiling.api.metrics.MetricContributorsFactory;
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
