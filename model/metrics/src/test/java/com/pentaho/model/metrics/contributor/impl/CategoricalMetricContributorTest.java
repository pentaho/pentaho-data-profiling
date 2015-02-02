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
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Created by mhall on 28/01/15.
 */
public class CategoricalMetricContributorTest {

  @Test public void testProcessFieldCategorical() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceField dataSourceField = new DataSourceField( new HashMap<String, Object>() );
    dataSourceField.setPath( "a.b" );
    dataSourceFieldManager.addDataSourceField( dataSourceField );
    DataSourceMetricManager
        dataSourceMetricManager =
        dataSourceField.getMetricManagerForType( String.class.getCanonicalName(), true );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( "A" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a.b" );

    CategoricalMetricContributor categoricalMetricContributor = new CategoricalMetricContributor();
    categoricalMetricContributor.processField( dataSourceFieldManager, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( "B" );
    categoricalMetricContributor.processField( dataSourceFieldManager, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( "B" );
    categoricalMetricContributor.processField( dataSourceFieldManager, dataSourceFieldValue );
    Map<String, Object> categoricalMap = dataSourceMetricManager.getValueNoDefault( Statistic.FREQUENCY_DISTRIBUTION );
    assertNotNull( categoricalMap );
    assertTrue( (Boolean) categoricalMap.get( MetricContributorUtils.CATEGORICAL ) );
    Map<String, Integer> categories = (Map<String, Integer>) categoricalMap.get( MetricContributorUtils.CATEGORIES );
    assertNotNull( categories );
    assertEquals( Integer.valueOf( 1 ), categories.get( "A" ) );
    assertEquals( Integer.valueOf( 2 ), categories.get( "B" ) );
  }

  @Test public void testProcessFieldNotCategorical() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceField dataSourceField = new DataSourceField( new HashMap<String, Object>() );
    dataSourceField.setPath( "a.b" );
    dataSourceFieldManager.addDataSourceField( dataSourceField );
    DataSourceMetricManager
        dataSourceMetricManager =
        dataSourceField.getMetricManagerForType( String.class.getCanonicalName(), true );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a.b" );

    CategoricalMetricContributor categoricalMetricContributor = new CategoricalMetricContributor();
    for ( int i = 0; i < 102; i++ ) {
      dataSourceFieldValue.setFieldValue( "" + i );
      categoricalMetricContributor.processField( dataSourceFieldManager, dataSourceFieldValue );
    }
    Map<String, Object> categoricalMap = dataSourceMetricManager.getValueNoDefault( Statistic.FREQUENCY_DISTRIBUTION );
    assertNotNull( categoricalMap );
    assertFalse( (Boolean) categoricalMap.get( MetricContributorUtils.CATEGORICAL ) );
    Map<String, Integer> categories = (Map<String, Integer>) categoricalMap.get( MetricContributorUtils.CATEGORIES );
    assertNull( categories );
  }

  @Test public void testProcessFieldNotLeaf() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock( DataSourceFieldManager.class );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( "one" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, false );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a.b" );
    new CategoricalMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test public void testProcessNotString() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock( DataSourceFieldManager.class );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( new Object() );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );
    new CategoricalMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test public void testProcessNull() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock( DataSourceFieldManager.class );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( null );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );
    new CategoricalMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test( expected = ProfileActionException.class ) public void testNullDataSourceField() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( "fred" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );

    new CategoricalMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
  }

  @Test public void testGetProfileFieldProperties() {
    assertNotNull( new CategoricalMetricContributor().getProfileFieldProperties() );
  }

  @Test public void testGetClearMap() {
    assertNotNull( new CategoricalMetricContributor().getClearMap() );
  }
}
