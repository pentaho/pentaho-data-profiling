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

import com.pentaho.profiling.api.metrics.MetricContributorUtils;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceField;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldManager;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import com.pentaho.profiling.api.ProfilingField;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.stats.Statistic;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceField dataSourceField = new DataSourceField();
    DataSourceMetricManager metricManager = new DataSourceMetricManager();
    dataSourceField.setPhysicalName( "a" );
    dataSourceFieldManager.addDataSourceField( dataSourceField );
    metricManager.setValue( 1L, MetricContributorUtils.COUNT );
    String test = "test-string";
    double value = test.length();
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( test );
    new StringLengthMetricContributor().process( metricManager, dataSourceFieldValue );

    assertEquals( value, metricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MIN ) );
    assertEquals( value, metricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MAX ) );
    assertEquals( value, metricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.SUM ) );
    assertEquals( value * value,
      metricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.SUM_OF_SQUARES ) );
    assertEquals( value, metricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MEAN ) );
  }

  @Test
  public void testGetTypes() {
    assertNotNull( stringLengthMetricContributor.getTypes() );
    assertTrue( Collections.disjoint( stringLengthMetricContributor.getTypes(), numericMetricContributor.getTypes() ) );
  }

  @Test
  public void testGetProfileFieldProperties() {
    assertNotNull( stringLengthMetricContributor.getProfileFieldProperties() );
  }

  @Test
  public void testProcess() throws ProfileActionException {
    DataSourceMetricManager dataSourceMetricManager = mock( DataSourceMetricManager.class );
    String fieldValue = "test-value";
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( fieldValue );
    stringLengthMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
    verify( numericMetricContributor ).processValue( dataSourceMetricManager, fieldValue.length() );
  }

  @Test
  public void testMerge() throws MetricMergeException {
    DataSourceMetricManager into = mock( DataSourceMetricManager.class );
    DataSourceMetricManager from = mock( DataSourceMetricManager.class );
    stringLengthMetricContributor.merge( into, from );
    verify( numericMetricContributor ).merge( into, from );
  }

  @Test
  public void testClear() {
    DataSourceMetricManager dataSourceMetricManager = mock( DataSourceMetricManager.class );
    stringLengthMetricContributor.clear( dataSourceMetricManager );
    verify( numericMetricContributor ).clear( dataSourceMetricManager );
  }
}
