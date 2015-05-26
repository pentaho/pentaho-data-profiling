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

package com.pentaho.model.metrics.contributor.metricManager.impl;

import com.pentaho.model.metrics.contributor.metricManager.MetricContributorBeanTester;
import com.pentaho.model.metrics.contributor.metricManager.impl.metrics.CategoricalHolder;
import com.pentaho.profiling.api.MutableProfileFieldValueType;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by mhall on 28/01/15.
 */
public class CategoricalMetricContributorTest extends MetricContributorBeanTester {

  public CategoricalMetricContributorTest() {
    super( CategoricalMetricContributor.class );
  }

  @Test public void testProcessFieldCategorical() throws ProfileActionException {
    MutableProfileFieldValueType mutableProfileFieldValueType =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CategoricalMetricContributor.SIMPLE_NAME );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( "A" );
    CategoricalMetricContributor categoricalMetricContributor = new CategoricalMetricContributor();
    categoricalMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( "B" );
    categoricalMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( "B" );
    categoricalMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    CategoricalHolder categoricalHolder =
      (CategoricalHolder) mutableProfileFieldValueType.getValueTypeMetrics( CategoricalMetricContributor.SIMPLE_NAME );
    assertNotNull( categoricalHolder );
    assertTrue( categoricalHolder.getCategorical() );
    Map<String, Long> categories = categoricalHolder.getCategories();
    assertNotNull( categories );
    assertEquals( Long.valueOf( 1 ), categories.get( "A" ) );
    assertEquals( Long.valueOf( 2 ), categories.get( "B" ) );
  }

  @Test public void testProcessFieldNotCategorical() throws ProfileActionException {
    MutableProfileFieldValueType mutableProfileFieldValueType =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CategoricalMetricContributor.SIMPLE_NAME );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    CategoricalMetricContributor categoricalMetricContributor = new CategoricalMetricContributor();
    for ( int i = 0; i < 102; i++ ) {
      dataSourceFieldValue.setFieldValue( "" + i );
      categoricalMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    }
    CategoricalHolder categoricalHolder =
      (CategoricalHolder) mutableProfileFieldValueType.getValueTypeMetrics( CategoricalMetricContributor.SIMPLE_NAME );
    assertNotNull( categoricalHolder );
    assertFalse( categoricalHolder.getCategorical() );
    assertNull( categoricalHolder.getCategories() );
  }

  @Test public void testGetProfileFieldProperties() {
    assertNotNull( new CategoricalMetricContributor().profileFieldProperties() );
  }

  @Test
  public void testGetTypes() {
    assertNotNull( new CategoricalMetricContributor().supportedTypes() );
  }

  @Test
  public void testMergeNoFirstOrSecond() throws MetricMergeException {
    MutableProfileFieldValueType into =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CategoricalMetricContributor.SIMPLE_NAME );
    MutableProfileFieldValueType from =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CategoricalMetricContributor.SIMPLE_NAME );
    CategoricalMetricContributor categoricalMetricContributor = new CategoricalMetricContributor();
    categoricalMetricContributor.merge( into, from );
    assertNull( into.getValueTypeMetrics( CategoricalMetricContributor.SIMPLE_NAME ) );
  }

  @Test
  public void testMergeNoFirst() throws MetricMergeException, ProfileActionException {
    MutableProfileFieldValueType into =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CategoricalMetricContributor.SIMPLE_NAME );
    MutableProfileFieldValueType from =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CategoricalMetricContributor.SIMPLE_NAME );
    CategoricalMetricContributor categoricalMetricContributor = new CategoricalMetricContributor();
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    for ( int i = 0; i < 5; i++ ) {
      dataSourceFieldValue.setFieldValue( "" + i );
      categoricalMetricContributor.process( from, dataSourceFieldValue );
    }
    categoricalMetricContributor.merge( into, from );
    assertEquals( 5,
      ( (CategoricalHolder) into.getValueTypeMetrics( CategoricalMetricContributor.SIMPLE_NAME ) ).getCategories()
        .size() );
  }

  @Test
  public void testMergeNoSecond() throws MetricMergeException, ProfileActionException {
    MutableProfileFieldValueType into =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CategoricalMetricContributor.SIMPLE_NAME );
    MutableProfileFieldValueType from =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CategoricalMetricContributor.SIMPLE_NAME );
    CategoricalMetricContributor categoricalMetricContributor = new CategoricalMetricContributor();
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    for ( int i = 0; i < 5; i++ ) {
      dataSourceFieldValue.setFieldValue( "" + i );
      categoricalMetricContributor.process( into, dataSourceFieldValue );
    }
    categoricalMetricContributor.merge( into, from );
    assertEquals( 5,
      ( (CategoricalHolder) into.getValueTypeMetrics( CategoricalMetricContributor.SIMPLE_NAME ) ).getCategories()
        .size() );
  }

  @Test
  public void testMergeFirstNotCategorical() throws MetricMergeException {
    CategoricalHolder firstHolder = new CategoricalHolder( 100, null );
    CategoricalHolder secondHolder = new CategoricalHolder( 100, new HashMap<String, Long>() );

    MutableProfileFieldValueType into =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CategoricalMetricContributor.SIMPLE_NAME );
    MutableProfileFieldValueType from =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CategoricalMetricContributor.SIMPLE_NAME );

    into.setValueTypeMetrics( CategoricalMetricContributor.SIMPLE_NAME, firstHolder );
    from.setValueTypeMetrics( CategoricalMetricContributor.SIMPLE_NAME, secondHolder );
    CategoricalMetricContributor categoricalMetricContributor = new CategoricalMetricContributor();
    categoricalMetricContributor.merge( into, from );
    assertFalse(
      ( (CategoricalHolder) into.getValueTypeMetrics( CategoricalMetricContributor.SIMPLE_NAME ) ).getCategorical() );
  }

  @Test
  public void testMergeSecondNotCategorical() throws MetricMergeException {
    CategoricalHolder firstHolder = new CategoricalHolder( 100, new HashMap<String, Long>() );
    CategoricalHolder secondHolder = new CategoricalHolder( 100, null );

    MutableProfileFieldValueType into =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CategoricalMetricContributor.SIMPLE_NAME );
    MutableProfileFieldValueType from =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CategoricalMetricContributor.SIMPLE_NAME );

    into.setValueTypeMetrics( CategoricalMetricContributor.SIMPLE_NAME, firstHolder );
    from.setValueTypeMetrics( CategoricalMetricContributor.SIMPLE_NAME, secondHolder );
    CategoricalMetricContributor categoricalMetricContributor = new CategoricalMetricContributor();
    categoricalMetricContributor.merge( into, from );
    assertFalse(
      ( (CategoricalHolder) into.getValueTypeMetrics( CategoricalMetricContributor.SIMPLE_NAME ) ).getCategorical() );
  }

  @Test
  public void testMergeBothCategoricalAndTotalBelow100() throws MetricMergeException {
    HashMap<String, Long> firstCategories = new HashMap<String, Long>();
    HashMap<String, Long> secondCategories = new HashMap<String, Long>();

    for ( int i = 0; i < 99; i++ ) {
      firstCategories.put( "" + i, Long.valueOf( i ) );
      secondCategories.put( "" + i, Long.valueOf( 2 * i ) );
    }
    secondCategories.put( "99", Long.valueOf( 99 * 2 ) );

    CategoricalHolder firstHolder = new CategoricalHolder( 100, firstCategories );
    CategoricalHolder secondHolder = new CategoricalHolder( 100, secondCategories );

    MutableProfileFieldValueType into =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CategoricalMetricContributor.SIMPLE_NAME );
    MutableProfileFieldValueType from =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CategoricalMetricContributor.SIMPLE_NAME );

    into.setValueTypeMetrics( CategoricalMetricContributor.SIMPLE_NAME, firstHolder );
    from.setValueTypeMetrics( CategoricalMetricContributor.SIMPLE_NAME, secondHolder );

    CategoricalMetricContributor categoricalMetricContributor = new CategoricalMetricContributor();
    categoricalMetricContributor.merge( into, from );
    CategoricalHolder firstMetrics =
      (CategoricalHolder) into.getValueTypeMetrics( CategoricalMetricContributor.SIMPLE_NAME );
    Map<String, Long> categories = firstMetrics.getCategories();
    for ( int i = 0; i < 99; i++ ) {
      assertEquals( Long.valueOf( 3 * i ), categories.get( "" + i ) );
    }
    assertEquals( Long.valueOf( 198 ), categories.get( "99" ) );
  }


  @Test
  public void testMergeBothCategoricalAndTotalAbove100() throws MetricMergeException {
    HashMap<String, Long> firstCategories = new HashMap<String, Long>();
    HashMap<String, Long> secondCategories = new HashMap<String, Long>();

    for ( int i = 0; i < 75; i++ ) {
      firstCategories.put( "" + i, Long.valueOf( i ) );
      secondCategories.put( "" + ( 2 * i ), Long.valueOf( i ) );
    }


    CategoricalHolder firstHolder = new CategoricalHolder( 100, firstCategories );
    CategoricalHolder secondHolder = new CategoricalHolder( 100, secondCategories );

    MutableProfileFieldValueType into =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CategoricalMetricContributor.SIMPLE_NAME );
    MutableProfileFieldValueType from =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CategoricalMetricContributor.SIMPLE_NAME );

    into.setValueTypeMetrics( CategoricalMetricContributor.SIMPLE_NAME, firstHolder );
    from.setValueTypeMetrics( CategoricalMetricContributor.SIMPLE_NAME, secondHolder );

    CategoricalMetricContributor categoricalMetricContributor = new CategoricalMetricContributor();
    categoricalMetricContributor.merge( into, from );
    CategoricalHolder firstMetrics =
      (CategoricalHolder) into.getValueTypeMetrics( CategoricalMetricContributor.SIMPLE_NAME );
    assertFalse( firstMetrics.getCategorical() );
  }
}
