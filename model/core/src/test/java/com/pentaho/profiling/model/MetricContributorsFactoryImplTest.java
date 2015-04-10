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

import com.pentaho.profiling.api.metrics.MetricContributor;
import com.pentaho.profiling.api.metrics.MetricContributorService;
import com.pentaho.profiling.api.metrics.MetricContributors;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.univariate.MetricManagerBasedMetricContributor;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 4/2/15.
 */
public class MetricContributorsFactoryImplTest {
  private MetricContributorService metricContributorService;
  private MetricContributorsFactoryImpl metricContributorsFactory;

  @Before
  public void setup() {
    metricContributorService = mock( MetricContributorService.class );
    metricContributorsFactory = new MetricContributorsFactoryImpl( metricContributorService );
  }

  @Test
  public void testNullContributors() {
    MetricContributors metricContributors = mock( MetricContributors.class );
    MetricContributor metricContributor = mock( MetricContributor.class );
    MetricManagerContributor metricManagerContributor = mock( MetricManagerContributor.class );
    when( metricContributorService.getDefaultMetricContributors( MetricContributorService.DEFAULT_CONFIGURATION ) )
      .thenReturn( metricContributors );
    when( metricContributors.getMetricContributors() ).thenReturn( Arrays.asList( metricContributor ) );
    when( metricContributors.getMetricManagerContributors() ).thenReturn( Arrays.asList( metricManagerContributor ) );
    List<MetricContributor> metricContributorList = metricContributorsFactory.construct( null );
    assertEquals( 2, metricContributorList.size() );
    assertEquals( metricContributor, metricContributorList.get( 0 ) );
    assertTrue( metricContributorList.get( 1 ) instanceof MetricManagerBasedMetricContributor );
  }

  @Test
  public void testEmptyContributors() {
    MetricContributors metricContributors = mock( MetricContributors.class );
    when( metricContributors.getMetricContributors() ).thenReturn( null );
    when( metricContributors.getMetricManagerContributors() ).thenReturn( null ).thenReturn( new ArrayList
      <MetricManagerContributor>() );
    //Null list of mmcs
    List<MetricContributor> metricContributorList = metricContributorsFactory.construct( metricContributors );
    assertEquals( 0, metricContributorList.size() );
    //Empty list of mmcs
    metricContributorList = metricContributorsFactory.construct( metricContributors );
    assertEquals( 0, metricContributorList.size() );
  }
}
