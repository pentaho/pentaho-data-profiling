/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2015 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */

package com.pentaho.profiling.api.metrics.univariate;

import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricContributorUtils;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceField;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldManager;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 2/27/15.
 */
public class MetricManagerBasedMetricContributorTest {
  private MetricManagerContributor metricManagerContributor;

  @Before
  public void setup() {
    metricManagerContributor = mock( MetricManagerContributor.class );
  }

  @Test
  public void testProcess() throws ProfileActionException {
    when( metricManagerContributor.supportedTypes() )
      .thenReturn( new HashSet<String>( Arrays.asList( String.class.getCanonicalName() ) ) );

    MetricManagerBasedMetricContributor metricManagerBasedMetricContributor =
      new MetricManagerBasedMetricContributor( Arrays.asList( metricManagerContributor ) );
    DataSourceFieldManager manager = new DataSourceFieldManager();
    DataSourceField dataSourceField = new DataSourceField();
    String testLogicalName = "testL";
    dataSourceField.setLogicalName( testLogicalName );
    String testPhysicalName = "testP";
    dataSourceField.setPhysicalName( testPhysicalName );
    DataSourceMetricManager metricManagerForType =
      dataSourceField.getMetricManagerForType( String.class.getCanonicalName(), true );
    metricManagerForType
      .setValue( 1L, MetricContributorUtils.COUNT );
    manager.addDataSourceField( dataSourceField );
    List<DataSourceFieldValue> values = new ArrayList<DataSourceFieldValue>();
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    dataSourceFieldValue.setFieldValue( "test-val" );
    dataSourceFieldValue.setPhysicalName( testPhysicalName );
    dataSourceFieldValue.setLogicalName( testLogicalName );
    values.add( dataSourceFieldValue );
    metricManagerBasedMetricContributor.processFields( manager, values );
    verify( metricManagerContributor ).process( metricManagerForType, dataSourceFieldValue );
  }

  @Test
  public void testSetDerived() throws ProfileActionException {
    when( metricManagerContributor.supportedTypes() )
      .thenReturn( new HashSet<String>( Arrays.asList( String.class.getCanonicalName() ) ) );

    MetricManagerBasedMetricContributor metricManagerBasedMetricContributor =
      new MetricManagerBasedMetricContributor( Arrays.asList( metricManagerContributor ) );
    DataSourceFieldManager manager = new DataSourceFieldManager();
    DataSourceField dataSourceField = new DataSourceField();
    String testLogicalName = "testL";
    dataSourceField.setLogicalName( testLogicalName );
    String testPhysicalName = "testP";
    dataSourceField.setPhysicalName( testPhysicalName );
    DataSourceMetricManager metricManagerForType =
      dataSourceField.getMetricManagerForType( String.class.getCanonicalName(), true );
    metricManagerForType
      .setValue( 1L, MetricContributorUtils.COUNT );
    manager.addDataSourceField( dataSourceField );
    metricManagerBasedMetricContributor.setDerived( manager );
    verify( metricManagerContributor ).setDerived( metricManagerForType );
  }

  @Test
  public void testClear() throws ProfileActionException {
    when( metricManagerContributor.supportedTypes() )
      .thenReturn( new HashSet<String>( Arrays.asList( String.class.getCanonicalName() ) ) );

    MetricManagerBasedMetricContributor metricManagerBasedMetricContributor =
      new MetricManagerBasedMetricContributor( Arrays.asList( metricManagerContributor ) );
    DataSourceFieldManager manager = new DataSourceFieldManager();
    DataSourceField dataSourceField = new DataSourceField();
    String testLogicalName = "testL";
    dataSourceField.setLogicalName( testLogicalName );
    String testPhysicalName = "testP";
    dataSourceField.setPhysicalName( testPhysicalName );
    DataSourceMetricManager metricManagerForType =
      dataSourceField.getMetricManagerForType( String.class.getCanonicalName(), true );
    metricManagerForType
      .setValue( 1L, MetricContributorUtils.COUNT );
    manager.addDataSourceField( dataSourceField );
    metricManagerBasedMetricContributor.clear( manager );
    verify( metricManagerContributor ).clear( metricManagerForType );
  }

  @Test
  public void testGetProfileFieldProperties() throws ProfileActionException {
    when( metricManagerContributor.supportedTypes() )
      .thenReturn( new HashSet<String>( Arrays.asList( String.class.getCanonicalName() ) ) );

    MetricManagerBasedMetricContributor metricManagerBasedMetricContributor =
      new MetricManagerBasedMetricContributor( Arrays.asList( metricManagerContributor ) );
    ProfileFieldProperty profileFieldProperty = mock( ProfileFieldProperty.class );
    when( metricManagerContributor.profileFieldProperties() ).thenReturn( Arrays.asList( profileFieldProperty ) );
    List<ProfileFieldProperty> profileFieldProperties = metricManagerBasedMetricContributor.getProfileFieldProperties();
    assertEquals( 1, profileFieldProperties.size() );
    assertEquals( profileFieldProperty, profileFieldProperties.get( 0 ) );
  }

  @Test
  public void testMerge() throws MetricMergeException {
    when( metricManagerContributor.supportedTypes() )
      .thenReturn( new HashSet<String>( Arrays.asList( String.class.getCanonicalName() ) ) );
    MetricManagerBasedMetricContributor metricManagerBasedMetricContributor =
      new MetricManagerBasedMetricContributor( Arrays.asList( metricManagerContributor ) );
    DataSourceFieldManager manager = new DataSourceFieldManager();
    DataSourceField dataSourceField = new DataSourceField();
    String testLogicalName = "testL";
    dataSourceField.setLogicalName( testLogicalName );
    String testPhysicalName = "testP";
    dataSourceField.setPhysicalName( testPhysicalName );
    DataSourceMetricManager metricManagerForType =
      dataSourceField.getMetricManagerForType( String.class.getCanonicalName(), true );
    metricManagerForType.setValue( 1L, MetricContributorUtils.COUNT );
    manager.addDataSourceField( dataSourceField );


    DataSourceFieldManager manager2 = new DataSourceFieldManager();
    DataSourceField dataSourceField2 = new DataSourceField();
    dataSourceField2.setLogicalName( testLogicalName );
    dataSourceField2.setPhysicalName( testPhysicalName );
    DataSourceMetricManager metricManagerForType2 =
      dataSourceField.getMetricManagerForType( String.class.getCanonicalName(), true );
    metricManagerForType2.setValue( 1L, MetricContributorUtils.COUNT );
    manager2.addDataSourceField( dataSourceField );
    metricManagerBasedMetricContributor.merge( manager, manager2 );
    verify( metricManagerContributor ).merge( metricManagerForType, metricManagerForType2 );
  }
}
