package com.pentaho.profiling.api;

/**
 * Created by bryan on 4/30/15.
 */
public interface MutableProfileFieldValueType extends ProfileFieldValueType {
  void setTypeName( String typeName );

  void setCount( long count );

  void incrementCount();

  void setValueTypeMetrics( String name, ValueTypeMetrics valueTypeMetrics );
}
