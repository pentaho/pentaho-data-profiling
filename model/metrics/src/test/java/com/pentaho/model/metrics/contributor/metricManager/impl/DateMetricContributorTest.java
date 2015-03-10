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
import com.pentaho.profiling.api.metrics.NVL;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import com.pentaho.model.metrics.contributor.metricManager.NVLOperations;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.stats.Statistic;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by mhall on 27/01/15.
 */
public class DateMetricContributorTest {
  private DateMetricContributor dateMetricContributor;
  private NVL nvl;

  @Before
  public void setup() {
    nvl = mock( NVL.class );
    dateMetricContributor = new DateMetricContributor( nvl );
  }

  @Test
  public void testProcessField() throws ProfileActionException, ParseException {
    DataSourceMetricManager dataSourceMetricManager = new DataSourceMetricManager( new HashMap<String, Object>() );

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );

    Date date1 = simpleDateFormat.parse( "3/4/1005" );
    Date date2 = simpleDateFormat.parse( "4/5/1006" );
    Date date3 = simpleDateFormat.parse( "6/7/1008" );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( date2 );
    dataSourceFieldValue.setPhysicalName( "a" );

    DateMetricContributor dateMetricContributor = new DateMetricContributor();
    dateMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
    assertEquals( date2,
      dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MIN ) );
    assertEquals( date2,
      dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MAX ) );
    dataSourceFieldValue.setFieldValue( date1 );
    dateMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
    assertEquals( date1,
      dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MIN ) );
    assertEquals( date2,
      dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MAX ) );
    dataSourceFieldValue.setFieldValue( date3 );
    dateMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
    assertEquals( date1,
      dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MIN ) );
    assertEquals( date3,
      dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MAX ) );
  }

  @Test
  public void testMerge() throws MetricMergeException {
    DataSourceMetricManager into = mock( DataSourceMetricManager.class );
    DataSourceMetricManager from = mock( DataSourceMetricManager.class );
    dateMetricContributor.merge( into, from );
    verify( nvl ).performAndSet( NVLOperations.DATE_MIN, into, from, NumericMetricContributor.MIN_PATH );
    verify( nvl ).performAndSet( NVLOperations.DATE_MAX, into, from, NumericMetricContributor.MAX_PATH );
  }

  @Test
  public void testClear() {
    DataSourceMetricManager dataSourceMetricManager = mock( DataSourceMetricManager.class );
    dateMetricContributor.clear( dataSourceMetricManager );
    verify( dataSourceMetricManager ).clear( DateMetricContributor.CLEAR_LIST );
  }

  @Test
  public void testGetTypes() {
    assertNotNull( dateMetricContributor.supportedTypes() );
  }

  @Test
  public void testGetProfileFieldProperties() {
    assertNotNull( dateMetricContributor.profileFieldProperties() );
  }
}
