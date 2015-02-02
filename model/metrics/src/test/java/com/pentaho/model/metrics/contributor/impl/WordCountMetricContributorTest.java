package com.pentaho.model.metrics.contributor.impl;

import com.pentaho.metrics.api.MetricContributorUtils;
import com.pentaho.metrics.api.field.DataSourceField;
import com.pentaho.metrics.api.field.DataSourceFieldManager;
import com.pentaho.metrics.api.field.DataSourceFieldValue;
import com.pentaho.metrics.api.field.DataSourceMetricManager;
import com.pentaho.profiling.api.ProfilingField;
import com.pentaho.profiling.api.action.ProfileActionException;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Created by mhall on 27/01/15.
 */
public class WordCountMetricContributorTest {

  @Test public void testUpdateTypeInitial() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceField dataSourceField = new DataSourceField();
    DataSourceMetricManager
        metricManager =
        dataSourceField.getMetricManagerForType( String.class.getCanonicalName(), true );
    dataSourceField.setPath( "a" );
    dataSourceFieldManager.addDataSourceField( dataSourceField );
    metricManager.setValue( 1L, MetricContributorUtils.COUNT );
    String test = "a test string";
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( test );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.PATH, "a" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    new WordCountMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );

    assertEquals( 3L, metricManager.getValueNoDefault( WordCountMetricContributor.WORD_COUNT_KEY_MIN ) );
    assertEquals( 3L, metricManager.getValueNoDefault( WordCountMetricContributor.WORD_COUNT_KEY_MAX ) );
    assertEquals( 3L, metricManager.getValueNoDefault( WordCountMetricContributor.WORD_COUNT_KEY_SUM ) );
    assertEquals( 3d, metricManager.getValueNoDefault( WordCountMetricContributor.WORD_COUNT_KEY_MEAN ) );
  }

  @Test public void testUpdateTypeWithMultiple() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceField dataSourceField = new DataSourceField();
    DataSourceMetricManager
        metricManager =
        dataSourceField.getMetricManagerForType( String.class.getCanonicalName(), true );
    dataSourceField.setPath( "a" );
    dataSourceFieldManager.addDataSourceField( dataSourceField );
    metricManager.setValue( 3L, MetricContributorUtils.COUNT );
    String test1 = "a test string";
    String test2 = "second";
    String test3 = "another test string with stuff";

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( test1 );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.PATH, "a" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );

    new WordCountMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( test2 );
    new WordCountMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( test3 );
    new WordCountMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );

    assertEquals( 1L, metricManager.getValueNoDefault( WordCountMetricContributor.WORD_COUNT_KEY_MIN ) );
    assertEquals( 5L, metricManager.getValueNoDefault( WordCountMetricContributor.WORD_COUNT_KEY_MAX ) );
    assertEquals( 9L, metricManager.getValueNoDefault( WordCountMetricContributor.WORD_COUNT_KEY_SUM ) );
    assertEquals( 3d, metricManager.getValueNoDefault( WordCountMetricContributor.WORD_COUNT_KEY_MEAN ) );
  }

  @Test public void testProcessFieldNotLeaf() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock( DataSourceFieldManager.class );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( "one" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, false );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );

    new WordCountMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test public void testProcessNull() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock( DataSourceFieldManager.class );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( null );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );
    new WordCountMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test public void testProcessNotString() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock( DataSourceFieldManager.class );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( new Object() );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );
    new WordCountMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test( expected = ProfileActionException.class ) public void testNullDataSourceField() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( "fred" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );

    new WordCountMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
  }

  @Test public void testGetProfileFieldProperties() {
    assertNotNull( new WordCountMetricContributor().getProfileFieldProperties() );
  }

  @Test public void testGetClearMap() {
    assertNotNull( new WordCountMetricContributor().getClearMap() );
  }
}
