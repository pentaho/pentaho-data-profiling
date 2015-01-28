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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class CardinalityMetricContributorTest {

  @Test public void testProcessField() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceField dataSourceField = new DataSourceField( new HashMap<String, Object>() );
    dataSourceField.setPath( "a" );
    dataSourceFieldManager.addDataSourceField( dataSourceField );
    DataSourceMetricManager
        dataSourceMetricManager =
        dataSourceField.getMetricManagerForType( String.class.getCanonicalName(), true );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( "one" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );

    CardinalityMetricContributor cardinalityMetricContributor = new CardinalityMetricContributor();
    cardinalityMetricContributor.processField( dataSourceFieldManager, dataSourceFieldValue );

    assertEquals( Long.valueOf( 1L ),
        dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.CARDINALITY ) );

    for ( int i = 0; i < 10; i++ ) {
      dataSourceFieldValue.setFieldValue( "two" );
      cardinalityMetricContributor.processField( dataSourceFieldManager, dataSourceFieldValue );
    }
    assertEquals( Long.valueOf( 2L ),
        dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.CARDINALITY ) );
    dataSourceFieldValue.setFieldValue( "three" );
    cardinalityMetricContributor.processField( dataSourceFieldManager, dataSourceFieldValue );
    assertEquals( Long.valueOf( 3L ),
        dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.CARDINALITY ) );

  }

  @Test
  public void testProcessFieldNotLeaf() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock(DataSourceFieldManager.class);

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( "one" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, false );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );
    new CardinalityMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test
  public void testProcessNull() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock(DataSourceFieldManager.class);

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( null );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );
    new CardinalityMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test(expected = ProfileActionException.class)
  public void testNullDataSourceField() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( 10L );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );

    new CardinalityMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
  }

  @Test
  public void testGetProfileFieldProperties() {
    assertNotNull(new CardinalityMetricContributor().getProfileFieldProperties());
  }

  @Test
  public void testGetClearMap() {
    assertNotNull(new CardinalityMetricContributor().getClearMap());
  }
}
