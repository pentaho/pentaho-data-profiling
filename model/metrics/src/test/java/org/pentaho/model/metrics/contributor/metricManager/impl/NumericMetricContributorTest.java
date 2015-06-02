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

package org.pentaho.model.metrics.contributor.metricManager.impl;

import org.pentaho.model.metrics.contributor.metricManager.impl.metrics.NumericHolder;
import org.pentaho.profiling.api.MutableProfileFieldValueType;
import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.metrics.MetricMergeException;
import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Created by mhall on 26/01/15.
 */
public class NumericMetricContributorTest {

  @Test public void testUpdateTypeInitial() throws ProfileActionException {
    MutableProfileFieldValueType mutableProfileFieldValueType =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( NumericMetricContributor.SIMPLE_NAME );
    Double value = 1.235;
    NumericMetricContributor numericMetricContributor = new NumericMetricContributor();
    numericMetricContributor.process( mutableProfileFieldValueType, new DataSourceFieldValue( value ) );
    when( mutableProfileFieldValueType.getCount() ).thenReturn( 1L );
    numericMetricContributor.setDerived( mutableProfileFieldValueType );
    NumericHolder numericHolder =
      (NumericHolder) mutableProfileFieldValueType.getValueTypeMetrics( NumericMetricContributor.SIMPLE_NAME );
    assertEquals( value, numericHolder.getMin() );
    assertEquals( value, numericHolder.getMax() );
    assertEquals( value, numericHolder.getSum() );
    assertEquals( Double.valueOf( value * value ), numericHolder.getSumOfSquares() );
    assertEquals( value, numericHolder.getMean() );
  }

  @Test public void testUpdateTypeWithMultiple() throws ProfileActionException {
    MutableProfileFieldValueType mutableProfileFieldValueType =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( NumericMetricContributor.SIMPLE_NAME );
    Double value1 = 1.235;
    Double value2 = 1.335;
    Double value3 = 1.435;
    NumericMetricContributor numericMetricContributor = new NumericMetricContributor();
    numericMetricContributor.process( mutableProfileFieldValueType, new DataSourceFieldValue( value1 ) );
    numericMetricContributor.process( mutableProfileFieldValueType, new DataSourceFieldValue( value2 ) );
    numericMetricContributor.process( mutableProfileFieldValueType, new DataSourceFieldValue( value3 ) );
    when( mutableProfileFieldValueType.getCount() ).thenReturn( 3L );
    numericMetricContributor.setDerived( mutableProfileFieldValueType );
    NumericHolder numericHolder =
      (NumericHolder) mutableProfileFieldValueType.getValueTypeMetrics( NumericMetricContributor.SIMPLE_NAME );
    assertEquals( value1, numericHolder.getMin() );
    assertEquals( value3, numericHolder.getMax() );
    Double sum = value1 + value2 + value3;
    assertEquals( sum, numericHolder.getSum() );
    Double sumOfSquares = value1 * value1 + value2 * value2 + value3 * value3;
    assertEquals( sumOfSquares, numericHolder.getSumOfSquares() );
    assertEquals( Double.valueOf( sum / 3 ), numericHolder.getMean() );
    Double variance = ( sumOfSquares - ( sum * sum ) / 3 ) / 2;
    assertEquals( variance, numericHolder.getVariance() );
    assertEquals( Double.valueOf( Math.sqrt( variance ) ), numericHolder.getStdDev() );
  }

  @Test
  public void testMerge() throws ProfileActionException, MetricMergeException {
    MutableProfileFieldValueType into =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( NumericMetricContributor.SIMPLE_NAME );
    MutableProfileFieldValueType from =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( NumericMetricContributor.SIMPLE_NAME );
    double value1 = 1.235;
    double value2 = 1.335;
    double value3 = 1.435;
    double value4 = 1.535;
    NumericMetricContributor numericMetricContributor = new NumericMetricContributor();
    numericMetricContributor.process( into, new DataSourceFieldValue( value1 ) );
    numericMetricContributor.process( into, new DataSourceFieldValue( value2 ) );
    numericMetricContributor.process( from, new DataSourceFieldValue( value3 ) );
    numericMetricContributor.process( from, new DataSourceFieldValue( value4 ) );
    numericMetricContributor.merge( into, from );
    when( into.getCount() ).thenReturn( 4L );
    numericMetricContributor.setDerived( into );
    NumericHolder numericHolder = (NumericHolder) into.getValueTypeMetrics( NumericMetricContributor.SIMPLE_NAME );
    assertEquals( Double.valueOf( value1 ), numericHolder.getMin() );
    assertEquals( Double.valueOf( value4 ), numericHolder.getMax() );
    double sum = value1 + value2 + value3 + value4;
    assertEquals( sum, numericHolder.getSum(), .01 );
    double sumOfSquares = value1 * value1 + value2 * value2 + value3 * value3 + value4 * value4;
    assertEquals( sumOfSquares, numericHolder.getSumOfSquares(), .01 );
    assertEquals( sum / 4, numericHolder.getMean(), .01 );
    double variance = ( sumOfSquares - ( sum * sum ) / 4 ) / 3;
    assertEquals( variance, numericHolder.getVariance(), .01 );
    assertEquals( Math.sqrt( variance ), numericHolder.getStdDev(), .01 );
  }

  @Test
  public void testGetProfileFieldProperties() {
    assertNotNull( new NumericMetricContributor().profileFieldProperties() );
  }

  @Test
  public void testGetTypes() {
    assertNotNull( new NumericMetricContributor().supportedTypes() );
  }
}
