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
