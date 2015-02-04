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
import static org.mockito.Mockito.verify;

/**
 * Created by mhall on 28/01/15.
 */
public class CategoricalMetricContributorTest {

  @Test public void testProcessFieldCategorical() throws ProfileActionException {
    DataSourceMetricManager dataSourceMetricManager = new DataSourceMetricManager( new HashMap<String, Object>() );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( "A" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PHYSICAL_NAME, "a.b" );

    CategoricalMetricContributor categoricalMetricContributor = new CategoricalMetricContributor();
    categoricalMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( "B" );
    categoricalMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( "B" );
    categoricalMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
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
    dataSourceField.setPhysicalName( "a.b" );
    dataSourceFieldManager.addDataSourceField( dataSourceField );
    DataSourceMetricManager
      dataSourceMetricManager =
      dataSourceField.getMetricManagerForType( String.class.getCanonicalName(), true );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PHYSICAL_NAME, "a.b" );

    CategoricalMetricContributor categoricalMetricContributor = new CategoricalMetricContributor();
    for ( int i = 0; i < 102; i++ ) {
      dataSourceFieldValue.setFieldValue( "" + i );
      categoricalMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
    }
    Map<String, Object> categoricalMap = dataSourceMetricManager.getValueNoDefault( Statistic.FREQUENCY_DISTRIBUTION );
    assertNotNull( categoricalMap );
    assertFalse( (Boolean) categoricalMap.get( MetricContributorUtils.CATEGORICAL ) );
    Map<String, Integer> categories = (Map<String, Integer>) categoricalMap.get( MetricContributorUtils.CATEGORIES );
    assertNull( categories );
  }

  @Test public void testGetProfileFieldProperties() {
    assertNotNull( new CategoricalMetricContributor().getProfileFieldProperties() );
  }

  @Test
  public void testGetTypes() {
    assertNotNull( new CategoricalMetricContributor().getTypes() );
  }

  @Test
  public void testClear() {
    DataSourceMetricManager dataSourceMetricManager = mock( DataSourceMetricManager.class );
    new CategoricalMetricContributor().clear( dataSourceMetricManager );
    verify( dataSourceMetricManager ).clear( CategoricalMetricContributor.CLEAR_PATHS );
  }

  @Test
  public void testMergeNoFirstOrSecond() throws MetricMergeException {
    DataSourceMetricManager into = new DataSourceMetricManager( new HashMap<String, Object>() );
    DataSourceMetricManager from = new DataSourceMetricManager( new HashMap<String, Object>() );
    CategoricalMetricContributor categoricalMetricContributor = new CategoricalMetricContributor();
    categoricalMetricContributor.merge( into, from );
    assertNull( into.getValueNoDefault( Statistic.FREQUENCY_DISTRIBUTION ) );
  }

  @Test
  public void testMergeNoFirst() throws MetricMergeException {
    Map<String, Object> categoricalMap = mock( Map.class );
    DataSourceMetricManager into = new DataSourceMetricManager( new HashMap<String, Object>() );
    DataSourceMetricManager from = new DataSourceMetricManager( new HashMap<String, Object>() );
    from.setValue( categoricalMap, Statistic.FREQUENCY_DISTRIBUTION );
    CategoricalMetricContributor categoricalMetricContributor = new CategoricalMetricContributor();
    categoricalMetricContributor.merge( into, from );
    assertEquals( categoricalMap, into.getValueNoDefault( Statistic.FREQUENCY_DISTRIBUTION ) );
  }

  @Test
  public void testMergeNoSecond() throws MetricMergeException {
    Map<String, Object> categoricalMap = mock( Map.class );
    DataSourceMetricManager into = new DataSourceMetricManager( new HashMap<String, Object>() );
    DataSourceMetricManager from = new DataSourceMetricManager( new HashMap<String, Object>() );
    into.setValue( categoricalMap, Statistic.FREQUENCY_DISTRIBUTION );
    CategoricalMetricContributor categoricalMetricContributor = new CategoricalMetricContributor();
    categoricalMetricContributor.merge( into, from );
    assertEquals( categoricalMap, into.getValueNoDefault( Statistic.FREQUENCY_DISTRIBUTION ) );
  }

  @Test
  public void testMergeFirstNotCategorical() throws MetricMergeException {
    Map<String, Object> firstCategoricalMap = new HashMap<String, Object>();
    Map<String, Object> secondCategoricalMap = new HashMap<String, Object>();
    HashMap<String, Integer> categories = new HashMap<String, Integer>();

    firstCategoricalMap.put( MetricContributorUtils.CATEGORICAL, false );
    secondCategoricalMap.put( MetricContributorUtils.CATEGORICAL, true );
    secondCategoricalMap.put( MetricContributorUtils.CATEGORIES, categories );
    DataSourceMetricManager into = new DataSourceMetricManager( new HashMap<String, Object>() );
    DataSourceMetricManager from = new DataSourceMetricManager( new HashMap<String, Object>() );
    into.setValue( firstCategoricalMap, Statistic.FREQUENCY_DISTRIBUTION );
    from.setValue( secondCategoricalMap, Statistic.FREQUENCY_DISTRIBUTION );
    CategoricalMetricContributor categoricalMetricContributor = new CategoricalMetricContributor();
    categoricalMetricContributor.merge( into, from );
    assertEquals( firstCategoricalMap, into.getValueNoDefault( Statistic.FREQUENCY_DISTRIBUTION ) );
  }

  @Test
  public void testMergeSecondNotCategorical() throws MetricMergeException {
    Map<String, Object> firstCategoricalMap = new HashMap<String, Object>();
    Map<String, Object> secondCategoricalMap = new HashMap<String, Object>();
    HashMap<String, Integer> categories = new HashMap<String, Integer>();

    secondCategoricalMap.put( MetricContributorUtils.CATEGORICAL, false );
    firstCategoricalMap.put( MetricContributorUtils.CATEGORICAL, true );
    firstCategoricalMap.put( MetricContributorUtils.CATEGORIES, categories );

    DataSourceMetricManager into = new DataSourceMetricManager( new HashMap<String, Object>() );
    DataSourceMetricManager from = new DataSourceMetricManager( new HashMap<String, Object>() );
    into.setValue( firstCategoricalMap, Statistic.FREQUENCY_DISTRIBUTION );
    from.setValue( secondCategoricalMap, Statistic.FREQUENCY_DISTRIBUTION );
    CategoricalMetricContributor categoricalMetricContributor = new CategoricalMetricContributor();
    categoricalMetricContributor.merge( into, from );
    assertEquals( secondCategoricalMap, into.getValueNoDefault( Statistic.FREQUENCY_DISTRIBUTION ) );
  }

  @Test
  public void testMergeBothCategoricalAndTotalBelow100() throws MetricMergeException {
    Map<String, Object> firstCategoricalMap = new HashMap<String, Object>();
    Map<String, Object> secondCategoricalMap = new HashMap<String, Object>();
    HashMap<String, Integer> firstCategories = new HashMap<String, Integer>();
    HashMap<String, Integer> secondCategories = new HashMap<String, Integer>();

    for ( int i = 0; i < 99; i++ ) {
      firstCategories.put( "" + i, i );
      secondCategories.put( "" + i, 2 * i );
    }
    secondCategories.put( "99", 99 * 2 );

    firstCategoricalMap.put( MetricContributorUtils.CATEGORICAL, true );
    firstCategoricalMap.put( MetricContributorUtils.CATEGORIES, firstCategories );
    secondCategoricalMap.put( MetricContributorUtils.CATEGORICAL, true );
    secondCategoricalMap.put( MetricContributorUtils.CATEGORIES, secondCategories );

    DataSourceMetricManager into = new DataSourceMetricManager( new HashMap<String, Object>() );
    DataSourceMetricManager from = new DataSourceMetricManager( new HashMap<String, Object>() );
    into.setValue( firstCategoricalMap, Statistic.FREQUENCY_DISTRIBUTION );
    from.setValue( secondCategoricalMap, Statistic.FREQUENCY_DISTRIBUTION );
    CategoricalMetricContributor categoricalMetricContributor = new CategoricalMetricContributor();
    categoricalMetricContributor.merge( into, from );
    assertEquals( firstCategoricalMap, into.getValueNoDefault( Statistic.FREQUENCY_DISTRIBUTION ) );

    for ( int i = 0; i < 99; i++ ) {
      assertEquals( Integer.valueOf( 3 * i ), firstCategories.get( "" + i ) );
    }
    assertEquals( Integer.valueOf( 198 ), firstCategories.get( "99" ) );
  }


  @Test
  public void testMergeBothCategoricalAndTotalAbove100() throws MetricMergeException {
    Map<String, Object> firstCategoricalMap = new HashMap<String, Object>();
    Map<String, Object> secondCategoricalMap = new HashMap<String, Object>();
    HashMap<String, Integer> firstCategories = new HashMap<String, Integer>();
    HashMap<String, Integer> secondCategories = new HashMap<String, Integer>();

    for ( int i = 0; i < 75; i++ ) {
      firstCategories.put( "" + i, i );
      secondCategories.put( "" + ( 2 * i ), i );
    }

    firstCategoricalMap.put( MetricContributorUtils.CATEGORICAL, true );
    firstCategoricalMap.put( MetricContributorUtils.CATEGORIES, firstCategories );
    secondCategoricalMap.put( MetricContributorUtils.CATEGORICAL, true );
    secondCategoricalMap.put( MetricContributorUtils.CATEGORIES, secondCategories );

    DataSourceMetricManager into = new DataSourceMetricManager( new HashMap<String, Object>() );
    DataSourceMetricManager from = new DataSourceMetricManager( new HashMap<String, Object>() );
    into.setValue( firstCategoricalMap, Statistic.FREQUENCY_DISTRIBUTION );
    from.setValue( secondCategoricalMap, Statistic.FREQUENCY_DISTRIBUTION );
    CategoricalMetricContributor categoricalMetricContributor = new CategoricalMetricContributor();
    categoricalMetricContributor.merge( into, from );
    assertEquals( firstCategoricalMap, into.getValueNoDefault( Statistic.FREQUENCY_DISTRIBUTION ) );
    assertFalse( (Boolean) firstCategoricalMap.get( MetricContributorUtils.CATEGORICAL ) );
    assertNull( firstCategoricalMap.get( MetricContributorUtils.CATEGORIES ) );
  }
}
