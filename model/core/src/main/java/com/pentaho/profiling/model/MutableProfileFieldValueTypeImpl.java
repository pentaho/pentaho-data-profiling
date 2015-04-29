package com.pentaho.profiling.model;

import com.pentaho.profiling.api.MutableProfileFieldValueType;
import com.pentaho.profiling.api.ProfileFieldValueType;
import com.pentaho.profiling.api.ValueTypeMetrics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bryan on 4/30/15.
 */
public class MutableProfileFieldValueTypeImpl extends ProfileFieldValueTypeImpl
  implements MutableProfileFieldValueType {
  public MutableProfileFieldValueTypeImpl() {
    super();
  }

  public MutableProfileFieldValueTypeImpl( ProfileFieldValueType profileFieldValueType ) {
    this( profileFieldValueType.getTypeName(), profileFieldValueType.getCount(),
      new HashMap<String, ValueTypeMetrics>( profileFieldValueType.getValueTypeMetricsMap() ) );
  }

  public MutableProfileFieldValueTypeImpl( String typeName, long count,
                                           Map<String, ValueTypeMetrics> typeMetrics ) {
    super( typeName, count, typeMetrics );
  }

  @Override public void setTypeName( String typeName ) {
    this.typeName = typeName;
  }

  @Override public void setCount( long count ) {
    this.count = count;
  }

  @Override public void incrementCount() {
    count++;
  }

  @Override public void setValueTypeMetrics( String name, ValueTypeMetrics valueTypeMetrics ) {
    typeMetrics.put( name, valueTypeMetrics );
  }
}
