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

import org.pentaho.profiling.api.MutableProfileFieldValueType;
import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.metrics.MetricMergeException;
import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by mhall on 27/01/15.
 */
public class StringLengthMetricContributorTest {
  private StringLengthMetricContributor stringLengthMetricContributor;
  private NumericMetricContributor numericMetricContributor;

  @Before
  public void setup() {
    numericMetricContributor = mock( NumericMetricContributor.class );
    stringLengthMetricContributor = new StringLengthMetricContributor( numericMetricContributor );
  }

  @Test public void testUpdateTypeInitial() throws ProfileActionException {
    MutableProfileFieldValueType mutableProfileFieldValueType =
      MetricContributorTestUtils
        .createMockMutableProfileFieldValueType( NumericMetricContributor.SIMPLE_NAME );
    String test = "test-string";
    int value = test.length();
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( test );
    stringLengthMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    verify( numericMetricContributor ).processValue( refEq( mutableProfileFieldValueType ), eq( value ) );
    stringLengthMetricContributor.setDerived( mutableProfileFieldValueType );
    verify( numericMetricContributor ).setDerived( mutableProfileFieldValueType );
  }

  @Test
  public void testGetTypes() {
    assertNotNull( stringLengthMetricContributor.supportedTypes() );
    assertTrue( Collections
      .disjoint( stringLengthMetricContributor.supportedTypes(), numericMetricContributor.supportedTypes() ) );
  }

  @Test
  public void testGetProfileFieldProperties() {
    assertNotNull( stringLengthMetricContributor.profileFieldProperties() );
  }

  @Test
  public void testMerge() throws MetricMergeException {
    MutableProfileFieldValueType into = MetricContributorTestUtils.createMockMutableProfileFieldValueType(
      NumericMetricContributor.SIMPLE_NAME );
    MutableProfileFieldValueType from = MetricContributorTestUtils.createMockMutableProfileFieldValueType(
      NumericMetricContributor.SIMPLE_NAME );
    stringLengthMetricContributor.merge( into, from );
    verify( numericMetricContributor ).merge( into, from );
  }
}
