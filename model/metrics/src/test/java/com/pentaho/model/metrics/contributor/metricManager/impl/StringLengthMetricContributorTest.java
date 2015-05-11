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

import com.pentaho.profiling.api.MutableProfileFieldValueType;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
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
