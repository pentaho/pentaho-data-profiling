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

import com.pentaho.profiling.api.MutableProfileField;
import com.pentaho.profiling.api.MutableProfileFieldValueType;
import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.ProfileField;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileFieldValueType;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
  private String testPhysicalName;
  private String testLogicalName;
  private MutableProfileStatus mutableProfileStatus;
  private MutableProfileField mutableProfileField;
  private MutableProfileFieldValueType mutableProfileFieldValueType;
  private MetricManagerBasedMetricContributor metricManagerBasedMetricContributor;

  @Before
  public void setup() {
    metricManagerContributor = mock( MetricManagerContributor.class );
    when( metricManagerContributor.supportedTypes() )
      .thenReturn( new HashSet<String>( Arrays.asList( String.class.getCanonicalName() ) ) );
    metricManagerBasedMetricContributor =
      new MetricManagerBasedMetricContributor( Arrays.asList( metricManagerContributor ) );

    testPhysicalName = "testP";
    testLogicalName = "testL";

    mutableProfileStatus = mock( MutableProfileStatus.class );
    mutableProfileField = mock( MutableProfileField.class );
    when( mutableProfileField.getPhysicalName() ).thenReturn( testPhysicalName );

    mutableProfileFieldValueType = mock( MutableProfileFieldValueType.class );
    HashMap<String, MutableProfileField> mutableProfileFieldHashMap = new HashMap<String, MutableProfileField>();
    mutableProfileFieldHashMap.put( testPhysicalName, mutableProfileField );
    when( mutableProfileStatus.getMutableFieldMap() ).thenReturn( mutableProfileFieldHashMap );

    when( mutableProfileStatus.getOrCreateField( testPhysicalName, testLogicalName ) )
      .thenReturn( mutableProfileField );
    when( mutableProfileField.getValueTypeMetrics( String.class.getCanonicalName() ) ).thenReturn(
      mutableProfileFieldValueType );
  }

  @Test
  public void testProcess() throws ProfileActionException {
    List<DataSourceFieldValue> values = new ArrayList<DataSourceFieldValue>();
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    dataSourceFieldValue.setFieldValue( "test-val" );
    dataSourceFieldValue.setPhysicalName( testPhysicalName );
    dataSourceFieldValue.setLogicalName( testLogicalName );
    values.add( dataSourceFieldValue );
    metricManagerBasedMetricContributor.processFields( mutableProfileStatus, values );
    verify( metricManagerContributor ).process( mutableProfileFieldValueType, dataSourceFieldValue );
  }

  @Test
  public void testSetDerived() throws ProfileActionException {
    metricManagerBasedMetricContributor.setDerived( mutableProfileStatus );
    verify( metricManagerContributor ).setDerived( mutableProfileFieldValueType );
  }

  @Test
  public void testGetProfileFieldProperties() throws ProfileActionException {
    ProfileFieldProperty profileFieldProperty = mock( ProfileFieldProperty.class );
    when( metricManagerContributor.profileFieldProperties() ).thenReturn( Arrays.asList( profileFieldProperty ) );
    List<ProfileFieldProperty> profileFieldProperties = metricManagerBasedMetricContributor.getProfileFieldProperties();
    assertEquals( 1, profileFieldProperties.size() );
    assertEquals( profileFieldProperty, profileFieldProperties.get( 0 ) );
  }

  @Test
  public void testMerge() throws MetricMergeException {
    ProfileStatus profileStatus = mock( ProfileStatus.class );
    ProfileField profileField = mock( ProfileField.class );
    ProfileFieldValueType profileFieldValueType = mock( ProfileFieldValueType.class );

    when( profileStatus.getField( testPhysicalName ) ).thenReturn( profileField );
    when( profileField.getType( String.class.getCanonicalName() ) ).thenReturn( profileFieldValueType );

    metricManagerBasedMetricContributor.merge( mutableProfileStatus, profileStatus );
    verify( metricManagerContributor ).merge( mutableProfileFieldValueType, profileFieldValueType );
  }
}
