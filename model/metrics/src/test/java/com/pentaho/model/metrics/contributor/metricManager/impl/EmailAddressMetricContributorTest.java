package com.pentaho.model.metrics.contributor.metricManager.impl;

import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import com.pentaho.profiling.api.action.ProfileActionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by mhall on 28/01/15.
 */
public class EmailAddressMetricContributorTest {

  @Test public void testProcessField() throws ProfileActionException {
    DataSourceMetricManager dataSourceMetricManager = new DataSourceMetricManager();
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( "A@B.net" );

    EmailAddressMetricContributor emailAddressMetricContributor = new EmailAddressMetricContributor();
    emailAddressMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( "fred" );
    emailAddressMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
    assertEquals( Long.valueOf( 1L ),
      dataSourceMetricManager.getValueNoDefault( EmailAddressMetricContributor.EMAIL_ADDRESS_KEY ) );
  }

  @Test
  public void testMerge() throws MetricMergeException {
    DataSourceMetricManager into = new DataSourceMetricManager();
    DataSourceMetricManager from = new DataSourceMetricManager();
    into.setValue( 5L, EmailAddressMetricContributor.EMAIL_ADDRESS_KEY );
    from.setValue( 15L, EmailAddressMetricContributor.EMAIL_ADDRESS_KEY );
    new EmailAddressMetricContributor().merge( into, from );
    assertEquals( 20L, into.getValueNoDefault( EmailAddressMetricContributor.EMAIL_ADDRESS_KEY ) );
  }

  @Test
  public void testGetProfileFieldProperties() {
    assertNotNull( new EmailAddressMetricContributor().getProfileFieldProperties() );
  }

  @Test
  public void testGetTypes() {
    assertNotNull( new EmailAddressMetricContributor().getTypes() );
  }

  @Test
  public void testClear() {
    DataSourceMetricManager dataSourceMetricManager = mock( DataSourceMetricManager.class );
    new EmailAddressMetricContributor().clear( dataSourceMetricManager );
    verify( dataSourceMetricManager ).clear( EmailAddressMetricContributor.CLEAR_LIST );
  }
}
