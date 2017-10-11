/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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

package org.pentaho.profiling.model.metrics.contributor.percentile;

import org.pentaho.profiling.api.MutableProfileFieldValueType;
import org.pentaho.profiling.api.ProfileFieldValueType;
import org.pentaho.profiling.api.ValueTypeMetrics;
import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.metrics.MetricMergeException;
import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.pentaho.profiling.api.util.ObjectHolder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by mhall on 27/01/15.
 */
public class PercentileMetricContributorTest {
  private MutableProfileFieldValueType mutableProfileFieldValueType;
  private PercentileMetricContributor percentileMetricContributor;

  private MutableProfileFieldValueType createMockMutableProfileFieldValueType() {
    final ObjectHolder<PercentileMetrics> objectHolder = new ObjectHolder<PercentileMetrics>();
    MutableProfileFieldValueType mutableProfileFieldValueType = mock( MutableProfileFieldValueType.class );
    when( mutableProfileFieldValueType.getValueTypeMetrics( PercentileMetricContributor.SIMPLE_NAME ) ).thenAnswer(
      new Answer<Object>() {
        @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
          return objectHolder.getObject();
        }
      } );
    doAnswer( new Answer() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        if ( PercentileMetricContributor.SIMPLE_NAME.equals( invocation.getArguments()[ 0 ] ) ) {
          objectHolder.setObject( (PercentileMetrics) invocation.getArguments()[ 1 ] );
        }
        return null;
      }
    } ).when( mutableProfileFieldValueType ).setValueTypeMetrics( eq( PercentileMetricContributor.SIMPLE_NAME ), any(
      ValueTypeMetrics.class ) );
    return mutableProfileFieldValueType;
  }

  @Before
  public void setup() {
    mutableProfileFieldValueType = createMockMutableProfileFieldValueType();
    percentileMetricContributor = new PercentileMetricContributor();
    percentileMetricContributor.setPercentileDefinitions( new ArrayList<PercentileDefinition>( Arrays
      .asList( new PercentileDefinition( null, null, .25 ), new PercentileDefinition( null, null, .5 ),
        new PercentileDefinition( null, null, .75 ) ) ) );
  }


  @Test public void testProcessField() throws ProfileActionException {
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue();
    dataSourceFieldValue.setFieldValue( 2.25d );
    percentileMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( 2.5d );
    percentileMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( 2.75d );
    percentileMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( 3.75d );
    percentileMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( 4.75d );
    percentileMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    when( mutableProfileFieldValueType.getCount() ).thenReturn( 5L );
    percentileMetricContributor.setDerived( mutableProfileFieldValueType );
    PercentileMetrics percentileMetrics =
      (PercentileMetrics) mutableProfileFieldValueType.getValueTypeMetrics( PercentileMetricContributor.SIMPLE_NAME );
    assertEquals( Double.valueOf( 2.625 ), percentileMetrics.getPercentiles().get( "0.5" ) );
    assertEquals( Double.valueOf( 2.4375 ), percentileMetrics.getPercentiles().get( "0.25" ) );
    assertEquals( Double.valueOf( 3 ), percentileMetrics.getPercentiles().get( "0.75" ) );
  }

  @Test public void testMerge() throws ProfileActionException, MetricMergeException {
    MutableProfileFieldValueType mutableProfileFieldValueType2 = createMockMutableProfileFieldValueType();
    when( mutableProfileFieldValueType2.getCount() ).thenReturn( 1L, 2L, 3L );
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( 2.25d );
    percentileMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    dataSourceFieldValue = new DataSourceFieldValue( 2.5d );
    percentileMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    dataSourceFieldValue = new DataSourceFieldValue( 2.75d );
    percentileMetricContributor.process( mutableProfileFieldValueType2, dataSourceFieldValue );
    dataSourceFieldValue = new DataSourceFieldValue( 3.75d );
    percentileMetricContributor.process( mutableProfileFieldValueType2, dataSourceFieldValue );
    dataSourceFieldValue = new DataSourceFieldValue( 4.75d );
    percentileMetricContributor.process( mutableProfileFieldValueType2, dataSourceFieldValue );
    percentileMetricContributor.merge( mutableProfileFieldValueType, mutableProfileFieldValueType2 );
    when( mutableProfileFieldValueType.getCount() ).thenReturn( 5L );
    percentileMetricContributor.setDerived( mutableProfileFieldValueType );

    PercentileMetrics percentileMetrics =
      (PercentileMetrics) mutableProfileFieldValueType.getValueTypeMetrics( PercentileMetricContributor.SIMPLE_NAME );
    assertEquals( Double.valueOf( 2.625 ), percentileMetrics.getPercentiles().get( "0.5" ) );
    assertEquals( Double.valueOf( 2.4375 ), percentileMetrics.getPercentiles().get( "0.25" ) );
    assertEquals( Double.valueOf( 3 ), percentileMetrics.getPercentiles().get( "0.75" ) );

    MutableProfileFieldValueType mutableProfileFieldValueType3 = createMockMutableProfileFieldValueType();
    when( mutableProfileFieldValueType3.getCount() ).thenReturn( 5L );
    percentileMetricContributor.merge( mutableProfileFieldValueType3, mutableProfileFieldValueType );
    percentileMetricContributor.setDerived( mutableProfileFieldValueType3 );
    assertEquals( Double.valueOf( 2.625 ),
      ( (PercentileMetrics) mutableProfileFieldValueType3
        .getValueTypeMetrics( PercentileMetricContributor.SIMPLE_NAME ) ).getPercentiles().get( "0.5" ) );

    ProfileFieldValueType emptyValueType = mock( ProfileFieldValueType.class );
    percentileMetricContributor.merge( mutableProfileFieldValueType, emptyValueType );
  }

  @Test
  public void testGetTypes() {
    assertNotNull( new PercentileMetricContributor().supportedTypes() );
  }

  @Test
  public void testGetProfileFieldProperties() {
    assertNotNull( new PercentileMetricContributor().profileFieldProperties() );
  }
}