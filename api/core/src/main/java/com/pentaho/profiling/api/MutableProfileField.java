package com.pentaho.profiling.api;

/**
 * Created by bryan on 4/30/15.
 */
public interface MutableProfileField extends ProfileField {
  MutableProfileFieldValueType getValueTypeMetrics( String name );

  void putValueTypeMetrics( String name, ProfileFieldValueType profileFieldValueType );

  MutableProfileFieldValueType getOrCreateValueTypeMetrics( String name );
}
