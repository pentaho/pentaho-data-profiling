package com.pentaho.model.metrics.contributor.metricManager.impl;

import com.clearspring.analytics.stream.cardinality.CardinalityMergeException;
import com.clearspring.analytics.stream.cardinality.ICardinality;
import com.pentaho.profiling.api.metrics.MetricContributorUtils;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceField;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.stats.Statistic;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CardinalityMetricContributorTest {

  @Test public void testProcess() throws ProfileActionException {
    DataSourceMetricManager dataSourceMetricManager = new DataSourceMetricManager( new HashMap<String, Object>() );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( "one" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PHYSICAL_NAME, "a" );

    CardinalityMetricContributor cardinalityMetricContributor = new CardinalityMetricContributor();
    cardinalityMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
    cardinalityMetricContributor.setDerived( dataSourceMetricManager );
    assertEquals( Long.valueOf( 1L ),
      dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.CARDINALITY ) );

    for ( int i = 0; i < 10; i++ ) {
      dataSourceFieldValue.setFieldValue( "two" );
      cardinalityMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
    }
    cardinalityMetricContributor.setDerived( dataSourceMetricManager );
    assertEquals( Long.valueOf( 2L ),
      dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.CARDINALITY ) );
    dataSourceFieldValue.setFieldValue( "three" );
    cardinalityMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
    cardinalityMetricContributor.setDerived( dataSourceMetricManager );
    assertEquals( Long.valueOf( 3L ),
      dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.CARDINALITY ) );

  }

  @Test( expected = ProfileActionException.class )
  public void testProcessException() throws ProfileActionException {
    DataSourceMetricManager dataSourceMetricManager = new DataSourceMetricManager( new HashMap<String, Object>() );
    CardinalityMetricContributor cardinalityMetricContributor = new CardinalityMetricContributor();
    MetricManagerContributor mock = mock( MetricManagerContributor.class );
    when( mock.toString() ).thenThrow( new RuntimeException() );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( mock );
    cardinalityMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
  }

  @Test
  public void testGetSupportedTypes() {
    assertTrue( new CardinalityMetricContributor().getTypes().contains( String.class.getCanonicalName() ) );
  }

  @Test
  public void testMergeNoFirstOrSecond() throws MetricMergeException {
    CardinalityMetricContributor cardinalityMetricContributor = new CardinalityMetricContributor();
    DataSourceMetricManager into = new DataSourceMetricManager( new HashMap<String, Object>() );
    DataSourceMetricManager from = new DataSourceMetricManager( new HashMap<String, Object>() );
    cardinalityMetricContributor.merge( into, from );
    assertNull( into.getValueNoDefault( CardinalityMetricContributor.CARDINALITY_PATH_ESTIMATOR ) );
    assertNull( into.getValueNoDefault( CardinalityMetricContributor.CARDINALITY_PATH ) );
  }

  @Test
  public void testMergeNoFirst() throws MetricMergeException {
    CardinalityMetricContributor cardinalityMetricContributor = new CardinalityMetricContributor();
    DataSourceMetricManager into = new DataSourceMetricManager( new HashMap<String, Object>() );
    DataSourceMetricManager from = new DataSourceMetricManager( new HashMap<String, Object>() );
    ICardinality iCardinality = mock( ICardinality.class );
    when( iCardinality.cardinality() ).thenReturn( 5L );
    from.setValue( iCardinality, CardinalityMetricContributor.CARDINALITY_PATH_ESTIMATOR );
    cardinalityMetricContributor.merge( into, from );
    assertEquals( iCardinality, into.getValueNoDefault( CardinalityMetricContributor.CARDINALITY_PATH_ESTIMATOR ) );
    assertEquals( 5L, into.getValueNoDefault( CardinalityMetricContributor.CARDINALITY_PATH ) );
  }

  @Test
  public void testMergeNoSecond() throws MetricMergeException {
    CardinalityMetricContributor cardinalityMetricContributor = new CardinalityMetricContributor();
    DataSourceMetricManager into = new DataSourceMetricManager( new HashMap<String, Object>() );
    DataSourceMetricManager from = new DataSourceMetricManager( new HashMap<String, Object>() );
    ICardinality iCardinality = mock( ICardinality.class );
    when( iCardinality.cardinality() ).thenReturn( 5L );
    into.setValue( iCardinality, CardinalityMetricContributor.CARDINALITY_PATH_ESTIMATOR );
    cardinalityMetricContributor.merge( into, from );
    assertEquals( iCardinality, into.getValueNoDefault( CardinalityMetricContributor.CARDINALITY_PATH_ESTIMATOR ) );
    assertEquals( 5L, into.getValueNoDefault( CardinalityMetricContributor.CARDINALITY_PATH ) );
  }

  @Test
  public void testMergeBoth() throws MetricMergeException, CardinalityMergeException {
    CardinalityMetricContributor cardinalityMetricContributor = new CardinalityMetricContributor();
    DataSourceMetricManager into = new DataSourceMetricManager( new HashMap<String, Object>() );
    DataSourceMetricManager from = new DataSourceMetricManager( new HashMap<String, Object>() );
    ICardinality originalEstimator = mock( ICardinality.class );
    ICardinality secondEstimator = mock( ICardinality.class );
    ICardinality mergedEstimator = mock( ICardinality.class );
    when( originalEstimator.merge( secondEstimator ) ).thenReturn( mergedEstimator );
    when( mergedEstimator.cardinality() ).thenReturn( 5L );
    into.setValue( originalEstimator, CardinalityMetricContributor.CARDINALITY_PATH_ESTIMATOR );
    from.setValue( secondEstimator, CardinalityMetricContributor.CARDINALITY_PATH_ESTIMATOR );
    cardinalityMetricContributor.merge( into, from );
    verify( originalEstimator ).merge( secondEstimator );
    assertEquals( mergedEstimator, into.getValueNoDefault( CardinalityMetricContributor.CARDINALITY_PATH_ESTIMATOR ) );
    assertEquals( 5L, into.getValueNoDefault( CardinalityMetricContributor.CARDINALITY_PATH ) );
  }

  @Test
  public void testClear() {
    DataSourceMetricManager into = mock( DataSourceMetricManager.class );
    new CardinalityMetricContributor().clear( into );
    verify( into ).clear( CardinalityMetricContributor.CLEAR_PATHS );
  }

  @Test( expected = MetricMergeException.class )
  public void testMetricMergeException() throws CardinalityMergeException, MetricMergeException {
    CardinalityMetricContributor cardinalityMetricContributor = new CardinalityMetricContributor();
    DataSourceMetricManager into = new DataSourceMetricManager( new HashMap<String, Object>() );
    DataSourceMetricManager from = new DataSourceMetricManager( new HashMap<String, Object>() );
    ICardinality originalEstimator = mock( ICardinality.class );
    ICardinality secondEstimator = mock( ICardinality.class );
    when( originalEstimator.merge( secondEstimator ) ).thenThrow( new CardinalityMergeException( "fake" ) {
    } );
    into.setValue( originalEstimator, CardinalityMetricContributor.CARDINALITY_PATH_ESTIMATOR );
    from.setValue( secondEstimator, CardinalityMetricContributor.CARDINALITY_PATH_ESTIMATOR );
    cardinalityMetricContributor.merge( into, from );
  }

  @Test
  public void testGetProfileFieldProperties() {
    assertNotNull( new CardinalityMetricContributor().getProfileFieldProperties().size() );
  }
}
