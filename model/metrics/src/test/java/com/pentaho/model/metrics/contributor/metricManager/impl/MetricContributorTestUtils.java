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
