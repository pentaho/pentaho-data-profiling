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

package com.pentaho.profiling.model.metrics.contributor.percentile;

import com.pentaho.profiling.api.MutableProfileFieldValueType;
import com.pentaho.profiling.api.ProfileFieldValueType;
import com.pentaho.profiling.api.ValueTypeMetrics;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.util.ObjectHolder;
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