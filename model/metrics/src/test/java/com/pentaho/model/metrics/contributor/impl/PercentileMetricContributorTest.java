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
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Created by mhall on 27/01/15.
 */
public class PercentileMetricContributorTest {

  @Test public void testProcessField() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceField dataSourceField = new DataSourceField( new HashMap<String, Object>() );
    dataSourceField.setPath( "a" );
    DataSourceMetricManager
        dataSourceMetricManager =
        dataSourceField.getMetricManagerForType( Double.class.getCanonicalName(), true );
    dataSourceFieldManager.addDataSourceField( dataSourceField );

    dataSourceMetricManager.setValue( 1L, MetricContributorUtils.COUNT );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.PATH, "a" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldValue( 2.25d );

    PercentileMetricContributor percentileMetricContributor = new PercentileMetricContributor();
    percentileMetricContributor.processField( dataSourceFieldManager, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( 2.5d );
    percentileMetricContributor.processField( dataSourceFieldManager, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( 2.75d );
    percentileMetricContributor.processField( dataSourceFieldManager, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( 3.75d );
    percentileMetricContributor.processField( dataSourceFieldManager, dataSourceFieldValue );
    dataSourceMetricManager.setValue( 5L, MetricContributorUtils.COUNT );
    dataSourceFieldValue.setFieldValue( 4.75d );
    percentileMetricContributor.processField( dataSourceFieldManager, dataSourceFieldValue );

    assertEquals( Double.valueOf( 2.625 ),
        dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_50" ) );
    assertEquals( Double.valueOf( 2.4375 ),
        dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_25" ) );
    assertEquals( Double.valueOf( 3 ),
        dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_75" ) );
  }

  @Test public void processFieldNotLeaf() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock( DataSourceFieldManager.class );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.PATH, "a" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, false );
    dataSourceFieldValue.setFieldValue( 2.25d );

    new PercentileMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test public void testProcessNotNumber() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock( DataSourceFieldManager.class );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.PATH, "a" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldValue( "fred" );

    new PercentileMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test public void testProcessNull() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock( DataSourceFieldManager.class );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.PATH, "a" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );

    new PercentileMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test( expected = ProfileActionException.class ) public void testNullDataSourceField() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceField dataSourceField = new DataSourceField( new HashMap<String, Object>() );
    dataSourceField.setPath( "a" );
    dataSourceFieldManager.addDataSourceField( dataSourceField );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.PATH, "a" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldValue( 2.3d );

    new PercentileMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
  }

  @Test
  public void testGetProfileFieldProperties() {
    assertNotNull(new NumericMetricContributor().getProfileFieldProperties());
  }

  @Test
  public void testGetClearMap() {
    assertNotNull(new NumericMetricContributor().getClearMap());
  }
}
