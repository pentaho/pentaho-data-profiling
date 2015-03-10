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
import com.pentaho.profiling.api.Profile;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.datasource.DataSourceReference;
import com.pentaho.profiling.api.metrics.MetricContributor;
import com.pentaho.profiling.api.metrics.MetricContributors;
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
      .accepts( new DataSourceReference( "test-id", AggregateProfileFactory.AGGREGATE_PROFILE ) ) );
    assertFalse( aggregateProfileFactory.accepts( new DataSourceReference( "test-id", "test-type" ) ) );
  }

  @Test
  public void testCreate() {
    DataSourceReference dataSourceReference =
      new DataSourceReference( "test-id", AggregateProfileFactory.AGGREGATE_PROFILE );
    ProfileStatusManager profileStatusManager = mock( ProfileStatusManager.class );
    Profile profile = aggregateProfileFactory
      .create( dataSourceReference, profileStatusManager, new MetricContributors( metricContributors, null ) );
    assertTrue( profile instanceof AggregateProfile );
    verify( aggregateProfileService ).registerAggregateProfile( (AggregateProfile) profile );
  }
}
