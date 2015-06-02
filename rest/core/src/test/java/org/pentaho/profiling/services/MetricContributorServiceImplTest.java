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

package org.pentaho.profiling.services;

import org.pentaho.profiling.api.metrics.MetricContributorService;
import org.pentaho.profiling.api.metrics.MetricContributors;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by bryan on 3/18/15.
 */
public class MetricContributorServiceImplTest {
  private MetricContributorService delegate;
  private MetricContributorServiceImpl metricContributorService;

  @Before
  public void setup() {
    delegate = mock( MetricContributorService.class );
    metricContributorService = new MetricContributorServiceImpl( delegate );
  }

  @Test
  public void testGetDefault() throws IOException {
    /*HttpServletResponse httpServletResponse = mock( HttpServletResponse.class );
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    when( httpServletResponse.getOutputStream() ).thenReturn( new ServletOutputStream() {
      @Override public void write( int b ) throws IOException {
        byteArrayOutputStream.write( b );
      }

      @Override public void flush() throws IOException {
        super.flush();
        byteArrayOutputStream.flush();
      }

      @Override public void close() throws IOException {
        super.close();
        byteArrayOutputStream.close();
      }
    } );
    CardinalityMetricContributor cardinalityMetricContributor = new CardinalityMetricContributor();
    cardinalityMetricContributor.setNormalPrecision( 150 );
    cardinalityMetricContributor.setSparsePrecision( 250 );
    MetricContributors metricContributors = new MetricContributors( new ArrayList<MetricContributor>(), new ArrayList
      <MetricManagerContributor>( Arrays.asList( cardinalityMetricContributor ) ) );
    when( delegate.getDefaultMetricContributors() ).thenReturn( metricContributors );
    metricContributorService.getDefaultMetricContributorsWs( httpServletResponse );
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enableDefaultTyping( ObjectMapper.DefaultTyping.NON_FINAL );
    assertEquals( metricContributors,
      objectMapper.readValue( byteArrayOutputStream.toByteArray(), MetricContributors.class ) );*/
  }

  @Test
  public void testSetDefault() throws IOException {
    MetricContributors metricContributors = mock( MetricContributors.class );
    metricContributorService
      .setDefaultMetricContributors( MetricContributorService.DEFAULT_CONFIGURATION, metricContributors );
    verify( delegate )
      .setDefaultMetricContributors( MetricContributorService.DEFAULT_CONFIGURATION, metricContributors );
  }
}
