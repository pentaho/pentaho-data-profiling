/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.profiling.api.metrics.univariate;

import org.pentaho.profiling.api.MutableProfileField;
import org.pentaho.profiling.api.MutableProfileFieldValueType;
import org.pentaho.profiling.api.MutableProfileStatus;
import org.pentaho.profiling.api.ProfileField;
import org.pentaho.profiling.api.ProfileFieldProperty;
import org.pentaho.profiling.api.ProfileFieldValueType;
import org.pentaho.profiling.api.ProfileStatus;
import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.metrics.MetricManagerContributor;
import org.pentaho.profiling.api.metrics.MetricMergeException;
import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
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
