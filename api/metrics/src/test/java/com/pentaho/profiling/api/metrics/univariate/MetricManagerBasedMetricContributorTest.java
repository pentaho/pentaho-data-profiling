package com.pentaho.profiling.api.metrics.univariate;

import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.TestAppender;
import com.pentaho.profiling.api.metrics.field.DataSourceField;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldManager;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 2/5/15.
 */
public class MetricManagerBasedMetricContributorTest {
  private MetricManagerContributor metricManagerContributor;
  private MetricManagerBasedMetricContributor metricManagerBasedMetricContributor;
  private List<LoggingEvent> loggingEvents;

  @Before
  public void setup() {
    loggingEvents = new ArrayList<LoggingEvent>();
    TestAppender.setInstance( new TestAppender( loggingEvents ) );
    metricManagerContributor = mock( MetricManagerContributor.class );
    metricManagerBasedMetricContributor = new MetricManagerBasedMetricContributor( metricManagerContributor );
  }

  @Test
  public void testProcessNullDataSourceField() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager();
    metricManagerBasedMetricContributor.processFields( dataSourceFieldManager, Arrays.asList(
      new DataSourceFieldValue[] { new DataSourceFieldValue() } ) );
    assertTrue( loggingEvents.get( loggingEvents.size() - 1 ).getMessage().toString()
      .startsWith( "Got DataSourceFieldValue for nonexistent field" ) );
  }

  @Test
  public void testNullFieldValue() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager();
    DataSourceField dataSourceField = new DataSourceField();
    String physicalName = "test.p.name";
    dataSourceField.setPhysicalName( physicalName );
    dataSourceFieldManager.addDataSourceField( dataSourceField );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    dataSourceFieldValue.setPhysicalName( physicalName );
    when( metricManagerContributor.getTypes() ).thenReturn( new HashSet<String>() );
    metricManagerBasedMetricContributor.processFields( dataSourceFieldManager, Arrays.asList( dataSourceFieldValue ) );
    verify( metricManagerContributor ).getTypes();
    verifyNoMoreInteractions( metricManagerContributor );
  }

  @Test
  public void testFieldValueNoManager() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager();
    DataSourceField dataSourceField = new DataSourceField();
    String physicalName = "test.p.name";
    dataSourceField.setPhysicalName( physicalName );
    dataSourceFieldManager.addDataSourceField( dataSourceField );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    dataSourceFieldValue.setPhysicalName( physicalName );
    String fieldValue = "test-value";
    dataSourceFieldValue.setFieldValue( fieldValue );
    when( metricManagerContributor.getTypes() )
      .thenReturn( new HashSet<String>( Arrays.asList( String.class.getCanonicalName() ) ) );
    metricManagerBasedMetricContributor.processFields( dataSourceFieldManager, Arrays.asList( dataSourceFieldValue ) );
    verify( metricManagerContributor ).getTypes();
    verifyNoMoreInteractions( metricManagerContributor );
  }

  @Test
  public void testFieldValue() throws ProfileActionException {
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager();
    DataSourceField dataSourceField = new DataSourceField();
    String physicalName = "test.p.name";
    dataSourceField.setPhysicalName( physicalName );
    DataSourceMetricManager metricManagerForType =
      dataSourceField.getMetricManagerForType( String.class.getCanonicalName(), true );
    dataSourceFieldManager.addDataSourceField( dataSourceField );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    dataSourceFieldValue.setPhysicalName( physicalName );
    String fieldValue = "test-value";
    dataSourceFieldValue.setFieldValue( fieldValue );
    when( metricManagerContributor.getTypes() )
      .thenReturn( new HashSet<String>( Arrays.asList( String.class.getCanonicalName() ) ) );
    metricManagerBasedMetricContributor.processFields( dataSourceFieldManager, Arrays.asList( dataSourceFieldValue ) );
    verify( metricManagerContributor ).process( metricManagerForType, dataSourceFieldValue );
  }

  @Test
  public void testMerge() throws MetricMergeException {
    when( metricManagerContributor.getTypes() )
      .thenReturn(
        new HashSet<String>( Arrays.asList( String.class.getCanonicalName(), Integer.class.getCanonicalName() ) ) );
    DataSourceFieldManager existing = new DataSourceFieldManager();
    DataSourceFieldManager update = new DataSourceFieldManager();
    DataSourceField firstOnly = new DataSourceField();
    DataSourceField first = new DataSourceField();
    DataSourceField second = new DataSourceField();

    firstOnly.setPhysicalName( "firstOnly" );
    String pname = "pname";
    first.setPhysicalName( pname );
    DataSourceMetricManager firstMetricManagerForType =
      first.getMetricManagerForType( String.class.getCanonicalName(), true );
    first.getMetricManagerForType( Integer.class.getCanonicalName(), true );
    second.setPhysicalName( pname );
    DataSourceMetricManager secondMetricManagerForType =
      second.getMetricManagerForType( String.class.getCanonicalName(), true );

    existing.addDataSourceField( firstOnly );
    existing.addDataSourceField( first );
    update.addDataSourceField( second );
    metricManagerBasedMetricContributor.merge( existing, update );
    verify( metricManagerContributor ).getTypes();
    verify( metricManagerContributor ).merge( firstMetricManagerForType, secondMetricManagerForType );
    verifyNoMoreInteractions( metricManagerContributor );
  }

  @Test
  public void testClearProfileStatus() {
    MutableProfileStatus mutableProfileStatus = mock( MutableProfileStatus.class );
    DataSourceFieldManager dataSourceFieldManager = new DataSourceFieldManager();
    DataSourceField dataSourceField = new DataSourceField();
    dataSourceField.setPhysicalName( "pname" );
    DataSourceMetricManager metricManagerForType =
      dataSourceField.getMetricManagerForType( String.class.getCanonicalName(), true );
    dataSourceFieldManager.addDataSourceField( dataSourceField );
    when( mutableProfileStatus.getFields() ).thenReturn( dataSourceFieldManager.getProfilingFields() );
    when( metricManagerContributor.getTypes() )
      .thenReturn( new HashSet<String>( Arrays.asList( String.class.getCanonicalName() ) ) );
    metricManagerBasedMetricContributor.clearProfileStatus( mutableProfileStatus );
    verify( metricManagerContributor ).clear( eq( metricManagerForType ) );
  }

  @Test
  public void testGetProfileFieldProperties() {
    List<ProfileFieldProperty> profileFieldProperties = mock( List.class );
    when( metricManagerContributor.getProfileFieldProperties() ).thenReturn( profileFieldProperties );
    assertEquals( profileFieldProperties, metricManagerBasedMetricContributor.getProfileFieldProperties() );
  }
}
