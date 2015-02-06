package com.pentaho.model.metrics.contributor.metricManager.impl;

import com.pentaho.profiling.api.metrics.MetricContributorUtils;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.stats.Statistic;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by mhall on 26/01/15.
 */
public class NumericMetricContributorTest {

  @Test public void testUpdateTypeInitial() throws ProfileActionException {
    DataSourceMetricManager manager = new DataSourceMetricManager( new HashMap<String, Object>() );
    manager.setValue( 1L, MetricContributorUtils.COUNT );
    double value = 1.235;
    new NumericMetricContributor().process( manager, new DataSourceFieldValue( value ) );
    assertEquals( value, manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MIN ) );
    assertEquals( value, manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MAX ) );
    assertEquals( value, manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.SUM ) );
    assertEquals( value * value,
      manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.SUM_OF_SQUARES ) );
    assertEquals( value, manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MEAN ) );
  }

  @Test public void testUpdateTypeWithMultiple() throws ProfileActionException {
    DataSourceMetricManager manager = new DataSourceMetricManager( new HashMap<String, Object>() );
    manager.setValue( 3L, MetricContributorUtils.COUNT );
    double value1 = 1.235;
    double value2 = 1.335;
    double value3 = 1.435;
    new NumericMetricContributor().process( manager, new DataSourceFieldValue( value1 ) );
    new NumericMetricContributor().process( manager, new DataSourceFieldValue( value2 ) );
    new NumericMetricContributor().process( manager, new DataSourceFieldValue( value3 ) );
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

  @Test
  public void testMerge() throws ProfileActionException, MetricMergeException {
    DataSourceMetricManager manager = new DataSourceMetricManager( new HashMap<String, Object>() );
    DataSourceMetricManager manager2 = new DataSourceMetricManager( new HashMap<String, Object>() );
    manager.setValue( 2L, MetricContributorUtils.COUNT );
    manager2.setValue( 2L, MetricContributorUtils.COUNT );
    double value1 = 1.235;
    double value2 = 1.335;
    double value3 = 1.435;
    double value4 = 1.535;
    NumericMetricContributor numericMetricContributor = new NumericMetricContributor();
    numericMetricContributor.process( manager, new DataSourceFieldValue( value1 ) );
    numericMetricContributor.process( manager, new DataSourceFieldValue( value2 ) );
    manager.setValue( 4L, MetricContributorUtils.COUNT );
    numericMetricContributor.process( manager2, new DataSourceFieldValue( value3 ) );
    numericMetricContributor.process( manager2, new DataSourceFieldValue( value4 ) );
    numericMetricContributor.merge( manager, manager2 );
    assertEquals( value1, manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MIN ) );
    assertEquals( value4, manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MAX ) );
    double sum = value1 + value2 + value3 + value4;
    assertEquals( sum, (Double) manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.SUM ), .01 );
    double sumOfSquares = value1 * value1 + value2 * value2 + value3 * value3 + value4 * value4;
    assertEquals( sumOfSquares,
      (Double) manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.SUM_OF_SQUARES ), .01 );
    assertEquals( sum / 4, (Double) manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MEAN ),
      .01 );
    double variance = ( sumOfSquares - ( sum * sum ) / 4 ) / 3;
    assertEquals( variance, (Double) manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.VARIANCE ),
      .01 );
    assertEquals( Math.sqrt( variance ),
      (Double) manager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.STANDARD_DEVIATION ), .01 );
  }

  @Test
  public void testGetProfileFieldProperties() {
    assertNotNull( new NumericMetricContributor().getProfileFieldProperties() );
  }

  @Test
  public void testGetTypes() {
    assertNotNull( new NumericMetricContributor().getTypes() );
  }

  @Test
  public void testClear() {
    NumericMetricContributor numericMetricContributor = new NumericMetricContributor();
    DataSourceMetricManager dataSourceMetricManager = mock( DataSourceMetricManager.class );
    numericMetricContributor.clear( dataSourceMetricManager );
    verify( dataSourceMetricManager ).clear( NumericMetricContributor.CLEAR_PATHS );
  }

  /*

  @Test public void testProcessFieldNotLeaf() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock( DataSourceFieldManager.class );
    DataSourceFieldValue fieldValue = new DataSourceFieldValue( 2.3d );
    fieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, false );
    fieldValue.setFieldMetatdata( DataSourceField.PHYSICAL_NAME, "a" );
    new NumericMetricContributor().processFields( dataSourceFieldManager, fieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test public void testProcessNotNumber() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock( DataSourceFieldManager.class );
    DataSourceFieldValue fieldValue = new DataSourceFieldValue( new Object() );
    fieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    fieldValue.setFieldMetatdata( DataSourceField.PHYSICAL_NAME, "a" );
    new NumericMetricContributor().processFields( dataSourceFieldManager, fieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test public void testProcessNull() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock( DataSourceFieldManager.class );
    DataSourceFieldValue fieldValue = new DataSourceFieldValue( null );
    fieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    fieldValue.setFieldMetatdata( DataSourceField.PHYSICAL_NAME, "a" );
    new NumericMetricContributor().processFields( dataSourceFieldManager, fieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test( expected = ProfileActionException.class )
  public void testNullProfilingField() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceFieldValue fieldValue = new DataSourceFieldValue( 2.3d );
    fieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    fieldValue.setFieldMetatdata( DataSourceField.PHYSICAL_NAME, "a" );
    new NumericMetricContributor().processFields( dataSourceFieldManager, fieldValue );
  }

  @Test( expected = ProfileActionException.class ) public void testNoDoubleMongoProfilingField()
      throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceField dataSourceField = new DataSourceField( new HashMap<String, Object>() );
    dataSourceField.setPhysicalName( "a" );
    dataSourceFieldManager.addDataSourceField( dataSourceField );
    DataSourceFieldValue fieldValue = new DataSourceFieldValue( 2.3d );
    fieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    fieldValue.setFieldMetatdata( DataSourceField.PHYSICAL_NAME, "a" );
    new NumericMetricContributor().processFields( dataSourceFieldManager, fieldValue );
  }

  @Test public void testProcessField() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceField dataSourceField = new DataSourceField( new HashMap<String, Object>() );
    dataSourceField.setPhysicalName( "a" );
    dataSourceField.getMetricManagerForType( Double.class.getCanonicalName(), true )
        .setValue( 1L, MetricContributorUtils.COUNT );
    dataSourceFieldManager.addDataSourceField( dataSourceField );
    DataSourceFieldValue fieldValue = new DataSourceFieldValue( 2.3d );
    fieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    fieldValue.setFieldMetatdata( DataSourceField.PHYSICAL_NAME, "a" );
    new NumericMetricContributor().processFields( dataSourceFieldManager, fieldValue );
  }

  @Test public void testGetProfileFieldProperties() {
    assertNotNull( new NumericMetricContributor().getProfileFieldProperties() );
  }

  @Test public void testGetClearMap() {
    assertNotNull( new NumericMetricContributor().getClearMap() );
  }*/
}
