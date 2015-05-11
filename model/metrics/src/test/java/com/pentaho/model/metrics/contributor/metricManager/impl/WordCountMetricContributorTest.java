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

package com.pentaho.model.metrics.contributor.metricManager.impl;

import com.pentaho.model.metrics.contributor.metricManager.impl.metrics.WordCountHolder;
import com.pentaho.profiling.api.MutableProfileFieldValueType;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
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
