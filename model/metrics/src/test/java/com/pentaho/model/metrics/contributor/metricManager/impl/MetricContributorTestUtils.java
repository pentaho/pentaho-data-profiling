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

package com.pentaho.model.metrics.contributor.metricManager.impl;

import com.pentaho.profiling.api.MutableProfileFieldValueType;
import com.pentaho.profiling.api.ValueTypeMetrics;
import com.pentaho.profiling.api.util.ObjectHolder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 5/5/15.
 */
public class MetricContributorTestUtils {
  public static MutableProfileFieldValueType createMockMutableProfileFieldValueType( final String valueTypeName ) {
    final ObjectHolder<ValueTypeMetrics> objectHolder = new ObjectHolder<ValueTypeMetrics>();
    MutableProfileFieldValueType mutableProfileFieldValueType = mock( MutableProfileFieldValueType.class );
    when( mutableProfileFieldValueType.getValueTypeMetrics( valueTypeName ) ).thenAnswer(
      new Answer<Object>() {
        @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
          return objectHolder.getObject();
        }
      } );
    doAnswer( new Answer() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        if ( valueTypeName.equals( invocation.getArguments()[ 0 ] ) ) {
          objectHolder.setObject( (ValueTypeMetrics) invocation.getArguments()[ 1 ] );
        }
        return null;
      }
    } ).when( mutableProfileFieldValueType ).setValueTypeMetrics( eq( valueTypeName ), any(
      ValueTypeMetrics.class ) );
    return mutableProfileFieldValueType;
  }
}
