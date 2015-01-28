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
import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Created by mhall on 26/01/15.
 */
public class NumericMetricContributorTest {

  @Test public void testUpdateTypeInitial() {
    DataSourceMetricManager manager = new DataSourceMetricManager( new HashMap<String, Object>() );
    manager.setValue( 1L, MetricContributorUtils.COUNT );
    double value = 1.235;
    NumericMetricContributor.updateType( manager, value );
    assertEquals( value, manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MIN ) );
    assertEquals( value, manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MAX ) );
    assertEquals( value, manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.SUM ) );
    assertEquals( value * value,
        manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.SUM_OF_SQUARES ) );
    assertEquals( value, manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MEAN ) );
  }

  @Test public void testUpdateTypeWithMultiple() {
    DataSourceMetricManager manager = new DataSourceMetricManager( new HashMap<String, Object>() );
    manager.setValue( 3L, MetricContributorUtils.COUNT );
    double value1 = 1.235;
    double value2 = 1.335;
    double value3 = 1.435;
    NumericMetricContributor.updateType( manager, value1 );
    NumericMetricContributor.updateType( manager, value2 );
    NumericMetricContributor.updateType( manager, value3 );
    assertEquals( value1, manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MIN ) );
    assertEquals( value3, manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MAX ) );
    double sum = value1 + value2 + value3;
    assertEquals( sum, manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.SUM ) );
    double sumOfSquares = value1 * value1 + value2 * value2 + value3 * value3;
    assertEquals( sumOfSquares,
        manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.SUM_OF_SQUARES ) );
    assertEquals( sum / 3, manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MEAN ) );
    double variance = ( sumOfSquares - ( sum * sum ) / 3 ) / 2;
    assertEquals( variance, manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.VARIANCE ) );
    assertEquals( Math.sqrt( variance ),
        manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.STANDARD_DEVIATION ) );
  }

  @Test public void testProcessFieldNotLeaf() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock( DataSourceFieldManager.class );
    DataSourceFieldValue fieldValue = new DataSourceFieldValue( 2.3d );
    fieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, false );
    fieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );
    new NumericMetricContributor().processField( dataSourceFieldManager, fieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test public void testProcessNotNumber() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock( DataSourceFieldManager.class );
    DataSourceFieldValue fieldValue = new DataSourceFieldValue( new Object() );
    fieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    fieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );
    new NumericMetricContributor().processField( dataSourceFieldManager, fieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test public void testProcessNull() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock( DataSourceFieldManager.class );
    DataSourceFieldValue fieldValue = new DataSourceFieldValue( null );
    fieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    fieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );
    new NumericMetricContributor().processField( dataSourceFieldManager, fieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test( expected = ProfileActionException.class )
  public void testNullProfilingField() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceFieldValue fieldValue = new DataSourceFieldValue( 2.3d );
    fieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    fieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );
    new NumericMetricContributor().processField( dataSourceFieldManager, fieldValue );
  }

  @Test( expected = ProfileActionException.class ) public void testNoDoubleMongoProfilingField()
      throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceField dataSourceField = new DataSourceField( new HashMap<String, Object>() );
    dataSourceField.setPath( "a" );
    dataSourceFieldManager.addDataSourceField( dataSourceField );
    DataSourceFieldValue fieldValue = new DataSourceFieldValue( 2.3d );
    fieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    fieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );
    new NumericMetricContributor().processField( dataSourceFieldManager, fieldValue );
  }

  @Test public void testProcessField() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceField dataSourceField = new DataSourceField( new HashMap<String, Object>() );
    dataSourceField.setPath( "a" );
    dataSourceField.getMetricManagerForType( Double.class.getCanonicalName(), true )
        .setValue( 1L, MetricContributorUtils.COUNT );
    dataSourceFieldManager.addDataSourceField( dataSourceField );
    DataSourceFieldValue fieldValue = new DataSourceFieldValue( 2.3d );
    fieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    fieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );
    new NumericMetricContributor().processField( dataSourceFieldManager, fieldValue );
  }

  @Test public void testGetProfileFieldProperties() {
    assertNotNull( new NumericMetricContributor().getProfileFieldProperties() );
  }

  @Test public void testGetClearMap() {
    assertNotNull( new NumericMetricContributor().getClearMap() );
  }
}
