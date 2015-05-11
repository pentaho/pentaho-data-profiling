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

import com.pentaho.model.metrics.contributor.metricManager.MetricContributorBeanTester;
import com.pentaho.model.metrics.contributor.metricManager.impl.metrics.RegexHolder;
import com.pentaho.profiling.api.MutableProfileFieldValueType;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
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
