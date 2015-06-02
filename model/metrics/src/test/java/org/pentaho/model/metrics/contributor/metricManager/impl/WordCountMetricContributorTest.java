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

import org.pentaho.model.metrics.contributor.metricManager.impl.metrics.WordCountHolder;
import org.pentaho.profiling.api.MutableProfileFieldValueType;
import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.metrics.MetricMergeException;
import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Created by mhall on 27/01/15.
 */
public class WordCountMetricContributorTest {

  @Test public void testUpdateTypeInitial() throws ProfileActionException {
    MutableProfileFieldValueType mutableProfileFieldValueType =
      MetricContributorTestUtils
        .createMockMutableProfileFieldValueType( WordCountMetricContributor.SIMPLE_NAME );
    String test = "a test string";
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( test );
    WordCountMetricContributor wordCountMetricContributor = new WordCountMetricContributor();
    wordCountMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    when( mutableProfileFieldValueType.getCount() ).thenReturn( 1L );
    wordCountMetricContributor.setDerived( mutableProfileFieldValueType );

    WordCountHolder wordCountHolder =
      (WordCountHolder) mutableProfileFieldValueType.getValueTypeMetrics( WordCountMetricContributor.SIMPLE_NAME );
    assertEquals( Long.valueOf( 3 ), wordCountHolder.getMin() );
    assertEquals( Long.valueOf( 3 ), wordCountHolder.getMax() );
    assertEquals( Long.valueOf( 3 ), wordCountHolder.getSum() );
    assertEquals( Double.valueOf( 3 ), wordCountHolder.getMean() );
  }

  @Test public void testUpdateTypeWithMultiple() throws ProfileActionException {
    MutableProfileFieldValueType mutableProfileFieldValueType =
      MetricContributorTestUtils
        .createMockMutableProfileFieldValueType( WordCountMetricContributor.SIMPLE_NAME );
    String test1 = "a test string";
    String test2 = "second";
    String test3 = "another test string with stuff";

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( test1 );

    WordCountMetricContributor wordCountMetricContributor = new WordCountMetricContributor();
    wordCountMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( test2 );
    wordCountMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( test3 );
    wordCountMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    when( mutableProfileFieldValueType.getCount() ).thenReturn( 3L );
    wordCountMetricContributor.setDerived( mutableProfileFieldValueType );
    WordCountHolder wordCountHolder =
      (WordCountHolder) mutableProfileFieldValueType.getValueTypeMetrics( WordCountMetricContributor.SIMPLE_NAME );
    assertEquals( Long.valueOf( 1 ), wordCountHolder.getMin() );
    assertEquals( Long.valueOf( 5 ), wordCountHolder.getMax() );
    assertEquals( Long.valueOf( 9 ), wordCountHolder.getSum() );
    assertEquals( Double.valueOf( 3 ), wordCountHolder.getMean() );
  }

  @Test public void testMerge() throws ProfileActionException, MetricMergeException {
    MutableProfileFieldValueType into =
      MetricContributorTestUtils
        .createMockMutableProfileFieldValueType( WordCountMetricContributor.SIMPLE_NAME );
    MutableProfileFieldValueType from =
      MetricContributorTestUtils
        .createMockMutableProfileFieldValueType( WordCountMetricContributor.SIMPLE_NAME );
    String test1 = "a test string";
    String test2 = "second";
    String test3 = "another test string with stuff";

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( test1 );

    WordCountMetricContributor wordCountMetricContributor = new WordCountMetricContributor();
    wordCountMetricContributor.process( into, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( test2 );
    wordCountMetricContributor.process( into, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( test3 );
    wordCountMetricContributor.process( from, dataSourceFieldValue );
    wordCountMetricContributor.merge( into, from );
    when( into.getCount() ).thenReturn( 3L );
    wordCountMetricContributor.setDerived( into );

    WordCountHolder wordCountHolder =
      (WordCountHolder) into.getValueTypeMetrics( WordCountMetricContributor.SIMPLE_NAME );
    assertEquals( Long.valueOf( 1 ), wordCountHolder.getMin() );
    assertEquals( Long.valueOf( 5 ), wordCountHolder.getMax() );
    assertEquals( Long.valueOf( 9 ), wordCountHolder.getSum() );
    assertEquals( Double.valueOf( 3 ), wordCountHolder.getMean() );
  }

  @Test
  public void testGetTypes() {
    assertNotNull( new WordCountMetricContributor().supportedTypes() );
  }

  @Test
  public void testGetProfileFieldProperties() {
    assertNotNull( new WordCountMetricContributor().profileFieldProperties() );
  }
}
