package com.pentaho.model.metrics.contributor.metricManager.impl;

import com.pentaho.profiling.api.metrics.MetricContributorUtils;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.stats.Statistic;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by mhall on 27/01/15.
 */
public class PercentileMetricContributorTest {

  @Test public void testProcessField() throws ProfileActionException {
    DataSourceMetricManager dataSourceMetricManager = new DataSourceMetricManager();

    dataSourceMetricManager.setValue( 1L, MetricContributorUtils.COUNT );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    dataSourceFieldValue.setFieldValue( 2.25d );

    PercentileMetricContributor percentileMetricContributor = new PercentileMetricContributor();
    percentileMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( 2.5d );
    percentileMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( 2.75d );
    percentileMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( 3.75d );
    percentileMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
    dataSourceMetricManager.setValue( 5L, MetricContributorUtils.COUNT );
    dataSourceFieldValue.setFieldValue( 4.75d );
    percentileMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );

    assertEquals( Double.valueOf( 2.625 ),
      dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_50" ) );
    assertEquals( Double.valueOf( 2.4375 ),
      dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_25" ) );
    assertEquals( Double.valueOf( 3 ),
      dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_75" ) );
  }

  @Test public void testMerge() throws ProfileActionException, MetricMergeException {
    PercentileMetricContributor percentileMetricContributor = new PercentileMetricContributor();
    DataSourceMetricManager dataSourceMetricManager = new DataSourceMetricManager();
    DataSourceMetricManager dataSourceMetricManager2 = new DataSourceMetricManager();

    dataSourceMetricManager.setValue( 2L, MetricContributorUtils.COUNT );
    dataSourceMetricManager2.setValue( 2L, MetricContributorUtils.COUNT );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( 2.25d );
    percentileMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
    dataSourceFieldValue = new DataSourceFieldValue( 2.5d );
    percentileMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
    dataSourceFieldValue = new DataSourceFieldValue( 2.75d );
    percentileMetricContributor.process( dataSourceMetricManager2, dataSourceFieldValue );
    dataSourceFieldValue = new DataSourceFieldValue( 3.75d );
    percentileMetricContributor.process( dataSourceMetricManager2, dataSourceFieldValue );
    dataSourceMetricManager.setValue( 5L, MetricContributorUtils.COUNT );
    dataSourceFieldValue = new DataSourceFieldValue( 4.75d );
    percentileMetricContributor.process( dataSourceMetricManager2, dataSourceFieldValue );
    percentileMetricContributor.merge( dataSourceMetricManager, dataSourceMetricManager2 );

    assertEquals( Double.valueOf( 2.625 ),
      dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_50" ) );
    assertEquals( Double.valueOf( 2.4375 ),
      dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_25" ) );
    assertEquals( Double.valueOf( 3 ),
      dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_75" ) );
  }

  @Test
  public void testGetTypes() {
    assertNotNull( new PercentileMetricContributor().getTypes() );
  }

  @Test
  public void testClear() {
    DataSourceMetricManager dataSourceMetricManager = mock( DataSourceMetricManager.class );
    new PercentileMetricContributor().clear( dataSourceMetricManager );
    verify( dataSourceMetricManager ).clear( PercentileMetricContributor.CLEAR_PATH );
  }

  @Test
  public void testGetProfileFieldProperties() {
    assertNotNull( new PercentileMetricContributor().getProfileFieldProperties() );
  }

  /*@Test public void processFieldNotLeaf() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock( DataSourceFieldManager.class );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.PATH, "a" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, false );
    dataSourceFieldValue.setFieldValue( 2.25d );

    new PercentileMetricContributor().processFields( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test public void testProcessNotNumber() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock( DataSourceFieldManager.class );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.PATH, "a" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldValue( "fred" );

    new PercentileMetricContributor().processFields( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test public void testProcessNull() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock( DataSourceFieldManager.class );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.PATH, "a" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );

    new PercentileMetricContributor().processFields( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test( expected = ProfileActionException.class ) public void testNullDataSourceField() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceField dataSourceField = new DataSourceField( new HashMap<String, Object>() );
    dataSourceField.setPhysicalName( "a" );
    dataSourceFieldManager.addDataSourceField( dataSourceField );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.PATH, "a" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldValue( 2.3d );

    new PercentileMetricContributor().processFields( dataSourceFieldManager, dataSourceFieldValue );
  }

  @Test
  public void testGetProfileFieldProperties() {
    assertNotNull(new NumericMetricContributor().getProfileFieldProperties());
  }

  @Test
  public void testGetClearMap() {
    assertNotNull(new NumericMetricContributor().getClearMap());
  }*/
}
