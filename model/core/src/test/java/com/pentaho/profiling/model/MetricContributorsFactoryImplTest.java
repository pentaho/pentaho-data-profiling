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
