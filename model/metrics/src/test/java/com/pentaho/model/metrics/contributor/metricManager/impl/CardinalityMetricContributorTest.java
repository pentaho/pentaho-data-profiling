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

import com.clearspring.analytics.stream.cardinality.CardinalityMergeException;
import com.pentaho.model.metrics.contributor.metricManager.MetricContributorBeanTester;
import com.pentaho.model.metrics.contributor.metricManager.impl.metrics.HyperLogLogPlusHolder;
import com.pentaho.profiling.api.MutableProfileFieldValueType;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CardinalityMetricContributorTest extends MetricContributorBeanTester {

  public CardinalityMetricContributorTest() {
    super( CardinalityMetricContributor.class );
  }

  @Test public void testProcess() throws ProfileActionException {
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( "one" );
    dataSourceFieldValue.setPhysicalName( "a" );

    CardinalityMetricContributor cardinalityMetricContributor = new CardinalityMetricContributor();
    MutableProfileFieldValueType mutableProfileFieldValueType =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CardinalityMetricContributor.SIMPLE_NAME );
    cardinalityMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    cardinalityMetricContributor.setDerived( mutableProfileFieldValueType );
    HyperLogLogPlusHolder hyperLogLogPlusHolder =
      (HyperLogLogPlusHolder) mutableProfileFieldValueType
        .getValueTypeMetrics( CardinalityMetricContributor.SIMPLE_NAME );
    assertEquals( 1L, hyperLogLogPlusHolder.getCardinality() );

    for ( int i = 0; i < 10; i++ ) {
      dataSourceFieldValue.setFieldValue( "two" );
      cardinalityMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    }
    cardinalityMetricContributor.setDerived( mutableProfileFieldValueType );
    assertEquals( 2L, hyperLogLogPlusHolder.getCardinality() );
    dataSourceFieldValue.setFieldValue( "three" );
    cardinalityMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    cardinalityMetricContributor.setDerived( mutableProfileFieldValueType );
    assertEquals( 3L, hyperLogLogPlusHolder.getCardinality() );

  }

  @Test( expected = ProfileActionException.class )
  public void testProcessException() throws ProfileActionException {
    MutableProfileFieldValueType mutableProfileFieldValueType =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CardinalityMetricContributor.SIMPLE_NAME );
    CardinalityMetricContributor cardinalityMetricContributor = new CardinalityMetricContributor();
    MetricManagerContributor mock = mock( MetricManagerContributor.class );
    when( mock.toString() ).thenThrow( new RuntimeException() );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( mock );
    cardinalityMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
  }

  @Test
  public void testGetSupportedTypes() {
    assertTrue( new CardinalityMetricContributor().supportedTypes().contains( String.class.getCanonicalName() ) );
  }

  @Test
  public void testMergeNoFirstOrSecond() throws MetricMergeException {
    CardinalityMetricContributor cardinalityMetricContributor = new CardinalityMetricContributor();
    MutableProfileFieldValueType mutableProfileFieldValueType =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CardinalityMetricContributor.SIMPLE_NAME );
    MutableProfileFieldValueType mutableProfileFieldValueType2 =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CardinalityMetricContributor.SIMPLE_NAME );
    cardinalityMetricContributor.merge( mutableProfileFieldValueType, mutableProfileFieldValueType2 );
    assertNull( mutableProfileFieldValueType.getValueTypeMetrics( CardinalityMetricContributor.SIMPLE_NAME ) );
  }

  @Test
  public void testMergeNoFirst() throws MetricMergeException, ProfileActionException {
    CardinalityMetricContributor cardinalityMetricContributor = new CardinalityMetricContributor();
    MutableProfileFieldValueType into =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CardinalityMetricContributor.SIMPLE_NAME );
    MutableProfileFieldValueType from =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CardinalityMetricContributor.SIMPLE_NAME );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    for ( int i = 0; i < 5; i++ ) {
      dataSourceFieldValue.setFieldValue( "" + i );
      cardinalityMetricContributor.process( from, dataSourceFieldValue );
    }
    cardinalityMetricContributor.merge( into, from );
    cardinalityMetricContributor.setDerived( into );
    assertEquals( 5L, ( (HyperLogLogPlusHolder) into.getValueTypeMetrics( CardinalityMetricContributor.SIMPLE_NAME ) )
      .getCardinality() );
  }

  @Test
  public void testMergeNoSecond() throws MetricMergeException, ProfileActionException {
    CardinalityMetricContributor cardinalityMetricContributor = new CardinalityMetricContributor();
    MutableProfileFieldValueType into =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CardinalityMetricContributor.SIMPLE_NAME );
    MutableProfileFieldValueType from =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CardinalityMetricContributor.SIMPLE_NAME );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    for ( int i = 0; i < 5; i++ ) {
      dataSourceFieldValue.setFieldValue( "" + i );
      cardinalityMetricContributor.process( into, dataSourceFieldValue );
    }
    cardinalityMetricContributor.merge( into, from );
    cardinalityMetricContributor.setDerived( into );
    assertEquals( 5L, ( (HyperLogLogPlusHolder) into.getValueTypeMetrics( CardinalityMetricContributor.SIMPLE_NAME ) )
      .getCardinality() );
  }

  @Test
  public void testMergeBoth() throws MetricMergeException, CardinalityMergeException, ProfileActionException {
    CardinalityMetricContributor cardinalityMetricContributor = new CardinalityMetricContributor();
    MutableProfileFieldValueType into =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CardinalityMetricContributor.SIMPLE_NAME );
    MutableProfileFieldValueType from =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CardinalityMetricContributor.SIMPLE_NAME );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    for ( int i = 0; i < 5; i++ ) {
      dataSourceFieldValue.setFieldValue( "" + i );
      cardinalityMetricContributor.process( into, dataSourceFieldValue );
      cardinalityMetricContributor.process( from, dataSourceFieldValue );
    }
    dataSourceFieldValue.setFieldValue( "6" );
    cardinalityMetricContributor.process( from, dataSourceFieldValue );
    cardinalityMetricContributor.merge( into, from );
    cardinalityMetricContributor.setDerived( into );
    assertEquals( 6L, ( (HyperLogLogPlusHolder) into.getValueTypeMetrics( CardinalityMetricContributor.SIMPLE_NAME ) )
      .getCardinality() );
  }

  @Test( expected = MetricMergeException.class )
  public void testMetricMergeException() throws CardinalityMergeException, MetricMergeException {
    CardinalityMetricContributor cardinalityMetricContributor = new CardinalityMetricContributor();
    MutableProfileFieldValueType into =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CardinalityMetricContributor.SIMPLE_NAME );
    MutableProfileFieldValueType from =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CardinalityMetricContributor.SIMPLE_NAME );
    HyperLogLogPlusHolder originalEstimator = mock( HyperLogLogPlusHolder.class );
    HyperLogLogPlusHolder secondEstimator = mock( HyperLogLogPlusHolder.class );
    when( originalEstimator.merge( secondEstimator ) ).thenThrow( new CardinalityMergeException( "fake" ) {
    } );
    into.setValueTypeMetrics( CardinalityMetricContributor.SIMPLE_NAME, originalEstimator );
    from.setValueTypeMetrics( CardinalityMetricContributor.SIMPLE_NAME, secondEstimator );
    cardinalityMetricContributor.merge( into, from );
  }

  @Test
  public void testGetProfileFieldProperties() {
    assertNotNull( new CardinalityMetricContributor().profileFieldProperties().size() );
  }
}
