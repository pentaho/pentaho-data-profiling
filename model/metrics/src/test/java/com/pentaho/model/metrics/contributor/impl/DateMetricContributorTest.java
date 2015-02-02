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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Created by mhall on 27/01/15.
 */
public class DateMetricContributorTest {

  @Test
  public void testProcessField() throws ProfileActionException, ParseException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceField dataSourceField = new DataSourceField( new HashMap<String, Object>() );
    dataSourceField.setPath( "a" );
    dataSourceFieldManager.addDataSourceField( dataSourceField );
    DataSourceMetricManager
        dataSourceMetricManager =
        dataSourceField.getMetricManagerForType( Date.class.getCanonicalName(), true );

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );

    Date date1 = simpleDateFormat.parse( "3/4/1005" );
    Date date2 = simpleDateFormat.parse( "4/5/1006" );
    Date date3 = simpleDateFormat.parse( "6/7/1008" );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( date2 );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );

    DateMetricContributor dateMetricContributor = new DateMetricContributor();
    dateMetricContributor.processField( dataSourceFieldManager, dataSourceFieldValue );
    assertEquals(date2, dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MIN ));
    assertEquals(date2, dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MAX ));
    dataSourceFieldValue.setFieldValue( date1 );
    dateMetricContributor.processField( dataSourceFieldManager, dataSourceFieldValue );
    assertEquals(date1, dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MIN ));
    assertEquals(date2, dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MAX ));
    dataSourceFieldValue.setFieldValue( date3 );
    dateMetricContributor.processField( dataSourceFieldManager, dataSourceFieldValue );
    assertEquals(date1, dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MIN ));
    assertEquals(date3, dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.MAX ));
  }

  @Test
  public void testProcessFieldNotLeaf() throws ProfileActionException, ParseException {
    DataSourceFieldManager dataSourceFieldManager = mock(DataSourceFieldManager.class);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );

    Date date1 = simpleDateFormat.parse( "3/4/1005" );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( date1 );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, false );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );
    new DateMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test
  public void testProcessNull() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock(DataSourceFieldManager.class);

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( null );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );
    new DateMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test(expected = ProfileActionException.class)
  public void testNullDataSourceField() throws ProfileActionException, ParseException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
    Date date1 = simpleDateFormat.parse( "3/4/1005" );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( date1 );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );

    new DateMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
  }

  @Test
  public void testGetProfileFieldProperties() {
    assertNotNull(new DateMetricContributor().getProfileFieldProperties());
  }

  @Test
  public void testGetClearMap() {
    assertNotNull(new DateMetricContributor().getClearMap());
  }
}
