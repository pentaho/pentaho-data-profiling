package com.pentaho.model.metrics.contributor.impl;

import com.pentaho.metrics.api.field.DataSourceField;
import com.pentaho.metrics.api.field.DataSourceFieldManager;
import com.pentaho.metrics.api.field.DataSourceFieldValue;
import com.pentaho.metrics.api.field.DataSourceMetricManager;
import com.pentaho.profiling.api.ProfilingField;
import com.pentaho.profiling.api.action.ProfileActionException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Created by mhall on 28/01/15.
 */
public class EmailAddressMetricContributorTest {

  @Test public void testProcessField() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceField dataSourceField = new DataSourceField( new HashMap<String, Object>() );
    dataSourceField.setPath( "a" );
    dataSourceFieldManager.addDataSourceField( dataSourceField );
    DataSourceMetricManager
        dataSourceMetricManager =
        dataSourceField.getMetricManagerForType( String.class.getCanonicalName(), true );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( "A@B.net" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.PATH, "a" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );

    EmailAddressMetricContributor emailAddressMetricContributor = new EmailAddressMetricContributor();
    emailAddressMetricContributor.processField( dataSourceFieldManager, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( "fred" );
    emailAddressMetricContributor.processField( dataSourceFieldManager, dataSourceFieldValue );
    assertEquals( Long.valueOf( 1L ),
        dataSourceMetricManager.getValueNoDefault( EmailAddressMetricContributor.EMAIL_ADDRESS_KEY ) );
  }

  @Test public void testProcessFieldNotLeaf() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock( DataSourceFieldManager.class );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( "A@B.net" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, false );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );
    new EmailAddressMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test public void testProcessNotString() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock( DataSourceFieldManager.class );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( new Object() );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );
    new EmailAddressMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test public void testProcessNull() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = mock( DataSourceFieldManager.class );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( null );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );
    new EmailAddressMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
    verifyNoMoreInteractions( dataSourceFieldManager );
  }

  @Test( expected = ProfileActionException.class ) public void testNullDataSourceField() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager( new ArrayList<ProfilingField>() );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( "A@B.net" );
    dataSourceFieldValue.setFieldMetatdata( DataSourceFieldValue.LEAF, true );
    dataSourceFieldValue.setFieldMetatdata( DataSourceField.PATH, "a" );

    new EmailAddressMetricContributor().processField( dataSourceFieldManager, dataSourceFieldValue );
  }

  @Test public void testGetProfileFieldProperties() {
    assertNotNull( new EmailAddressMetricContributor().getProfileFieldProperties() );
  }

  @Test public void testGetClearMap() {
    assertNotNull( new EmailAddressMetricContributor().getClearMap() );
  }
}
