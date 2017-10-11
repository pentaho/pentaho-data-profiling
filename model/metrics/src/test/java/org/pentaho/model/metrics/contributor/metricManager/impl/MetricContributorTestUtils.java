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

package org.pentaho.model.metrics.contributor.metricManager.impl;

import org.pentaho.profiling.api.MutableProfileFieldValueType;
import org.pentaho.profiling.api.ValueTypeMetrics;
import org.pentaho.profiling.api.util.ObjectHolder;
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
