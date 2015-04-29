package com.pentaho.profiling.api;

import java.util.Map;

/**
 * Created by bryan on 4/30/15.
 */
public interface ProfileFieldValueType extends PublicCloneable {
  String getTypeName();

  Long getCount();

  ValueTypeMetrics getValueTypeMetrics( String name );

  Map<String, ValueTypeMetrics> getValueTypeMetricsMap();
}
