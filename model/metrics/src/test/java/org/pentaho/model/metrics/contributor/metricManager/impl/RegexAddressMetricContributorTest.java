/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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

package org.pentaho.model.metrics.contributor.metricManager.impl;

import org.pentaho.model.metrics.contributor.metricManager.MetricContributorBeanTester;
import org.pentaho.model.metrics.contributor.metricManager.impl.metrics.RegexHolder;
import org.pentaho.profiling.api.MutableProfileFieldValueType;
import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.metrics.MetricMergeException;
import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by mhall on 28/01/15.
 */
public class RegexAddressMetricContributorTest extends MetricContributorBeanTester {

  public RegexAddressMetricContributorTest() {
    super( RegexAddressMetricContributor.class );
  }

  @Test public void testProcessField() throws ProfileActionException {
    RegexAddressMetricContributor regexAddressMetricContributor = new RegexAddressMetricContributor();
    MutableProfileFieldValueType mutableProfileFieldValueType =
      MetricContributorTestUtils
        .createMockMutableProfileFieldValueType( regexAddressMetricContributor.metricName() );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( "A@B.net" );

    regexAddressMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( "fred" );
    regexAddressMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    RegexHolder regexHolder =
      (RegexHolder) mutableProfileFieldValueType.getValueTypeMetrics( regexAddressMetricContributor.metricName() );
    assertEquals( Long.valueOf( 1L ), regexHolder.getCount() );
  }

  @Test
  public void testMerge() throws MetricMergeException {
    RegexAddressMetricContributor regexAddressMetricContributor = new RegexAddressMetricContributor();
    MutableProfileFieldValueType into =
      MetricContributorTestUtils
        .createMockMutableProfileFieldValueType( regexAddressMetricContributor.metricName() );
    MutableProfileFieldValueType from =
      MetricContributorTestUtils
        .createMockMutableProfileFieldValueType( regexAddressMetricContributor.metricName() );
    into.setValueTypeMetrics( regexAddressMetricContributor.metricName(), new RegexHolder( 5L ) );
    from.setValueTypeMetrics( regexAddressMetricContributor.metricName(), new RegexHolder( 15L ) );
    regexAddressMetricContributor.merge( into, from );
    assertEquals( Long.valueOf( 20 ),
      ( (RegexHolder) into.getValueTypeMetrics( regexAddressMetricContributor.metricName() ) ).getCount() );
  }

  @Test
  public void testGetProfileFieldProperties() {
    assertNotNull( new RegexAddressMetricContributor().profileFieldProperties() );
  }

  @Test
  public void testGetTypes() {
    assertNotNull( new RegexAddressMetricContributor().supportedTypes() );
  }
}
