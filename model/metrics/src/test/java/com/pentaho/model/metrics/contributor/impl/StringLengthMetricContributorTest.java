package com.pentaho.model.metrics.contributor.impl;

import com.pentaho.metrics.api.MetricContributorUtils;
import com.pentaho.metrics.api.field.DataSourceField;
import com.pentaho.metrics.api.field.DataSourceFieldManager;
import com.pentaho.metrics.api.field.DataSourceFieldValue;
import com.pentaho.metrics.api.field.DataSourceMetricManager;
import com.pentaho.profiling.api.ProfilingField;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.stats.Statistic;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Created by mhall on 27/01/15.
 */
public class StringLengthMetricContributorTest {

  @Test public void testUpdateTypeInitial() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceField dataSourceField = new DataSourceField();
    DataSourceMetricManager
        metricManager =
        dataSourceField.getMetricManagerForType( String.class.getCanonicalName(), true );
    dataSourceField.setPath( "a" );
    dataSourceFieldManager.addDataSourceField( dataSourceField );
    metricManager.setValue( 1L, MetricContributorUtils.COUNT );
    String test = "test-string";
    double value = test.length();
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( test );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.PATH, "a" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    new StringLengthMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );

    assertEquals( value, metricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MIN ) );
    assertEquals( value, metricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MAX ) );
    assertEquals( value, metricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.SUM ) );
    assertEquals( value * value,
        metricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.SUM_OF_SQUARES ) );
    assertEquals( value, metricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MEAN ) );
  }

  @Test
  public void testUpdateTypwWithMultiple() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceField dataSourceField = new DataSourceField();
    DataSourceMetricManager
        metricManager =
        dataSourceField.getMetricManagerForType( String.class.getCanonicalName(), true );
    dataSourceField.setPath( "a" );
    dataSourceFieldManager.addDataSourceField( dataSourceField );
    metricManager.setValue( 3L, MetricContributorUtils.COUNT );

    String test1 = "test-string";
    String test2 = "test-string-two";
    String test3 = "test-string-three";
    double value1 = test1.length();
    double value2 = test2.length();
    double value3 = test3.length();

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( test1 );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.PATH, "a" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );

    new StringLengthMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( test2 );
    new StringLengthMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( test3 );
    new StringLengthMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );

    assertEquals( value1, metricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MIN ) );
    assertEquals( value3, metricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MAX ) );
    double sum = value1 + value2 + value3;
    assertEquals( sum, metricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.SUM ) );
    double sumOfSq = value1 * value1 + value2 * value2 + value3 * value3;
    assertEquals( sumOfSq,
        metricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.SUM_OF_SQUARES ) );
    double variance = ( sumOfSq - ( sum * sum ) / 3 ) / 2;
    assertEquals( variance,
        metricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.VARIANCE ) );
    assertEquals( Math.sqrt(variance),
        metricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.STANDARD_DEVIATION ) );
  }

  @Test
  public void testProcessFieldNotLeaf() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock(DataSourceFieldManager.class);

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( "one" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, false );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );

    new StringLengthMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test
  public void testProcessNull() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock(DataSourceFieldManager.class);

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( null );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );
    new StringLengthMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test
  public void testProcessNotString() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock(DataSourceFieldManager.class);

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( new Object() );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );
    new StringLengthMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test(expected = ProfileActionException.class)
  public void testNullDataSourceField() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( "fred" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );

    new StringLengthMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
  }

  @Test
  public void testGetProfileFieldProperties() {
    assertNotNull(new StringLengthMetricContributor().getProfileFieldProperties());
  }

  @Test
  public void testGetClearMap() {
    assertNotNull(new StringLengthMetricContributor().getClearMap());
  }
}
