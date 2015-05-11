package com.pentaho.profiling.model;

import com.pentaho.profiling.api.ProfileFieldValueType;
import com.pentaho.profiling.api.ValueTypeMetrics;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.pentaho.profiling.api.util.PublicCloneableUtil.copyMap;

/**
 * Created by bryan on 4/30/15.
 */
public class ProfileFieldValueTypeImpl implements ProfileFieldValueType {
  protected String typeName;
  protected long count;
  protected Map<String, ValueTypeMetrics> typeMetrics;

  public ProfileFieldValueTypeImpl() {
    this( null, 0L, new HashMap<String, ValueTypeMetrics>() );
  }

  public ProfileFieldValueTypeImpl( String typeName, long count, Map<String, ValueTypeMetrics> typeMetrics ) {
    this.typeName = typeName;
    this.count = count;
    this.typeMetrics = new TreeMap<String, ValueTypeMetrics>();
    copyMap( typeMetrics, this.typeMetrics );
  }

  @Override public Object clone() {
    return new ProfileFieldValueTypeImpl( typeName, count, typeMetrics );
  }

  @Override public String getTypeName() {
    return typeName;
  }

  @Override public Long getCount() {
    return count;
  }

  @Override public ValueTypeMetrics getValueTypeMetrics( String name ) {
    return typeMetrics.get( name );
  }

  @Override public Map<String, ValueTypeMetrics> getValueTypeMetricsMap() {
    return new HashMap<String, ValueTypeMetrics>( typeMetrics );
  }
}
