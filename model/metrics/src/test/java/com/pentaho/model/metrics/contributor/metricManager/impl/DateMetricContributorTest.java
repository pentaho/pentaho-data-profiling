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
    assertNotNull( dateMetricContributor.getTypes() );
  }

  @Test
  public void testGetProfileFieldProperties() {
    assertNotNull( dateMetricContributor.getProfileFieldProperties() );
  }
}
