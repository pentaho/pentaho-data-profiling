package com.pentaho.profiling.api.dto;

import com.pentaho.profiling.api.ProfileFieldValueType;
import com.pentaho.profiling.api.ValueTypeMetrics;

import java.util.Map;

/**
 * Created by bryan on 5/8/15.
 */
public class ProfileFieldValueTypeDTO implements ProfileFieldValueType {
  private String typeName;
  private Long count;
  private Map<String, ValueTypeMetrics> valueTypeMetricsMap;

  public ProfileFieldValueTypeDTO() {

  }

  public ProfileFieldValueTypeDTO( ProfileFieldValueType profileFieldValueType ) {
    this.typeName = profileFieldValueType.getTypeName();
    this.count = profileFieldValueType.getCount();
    this.valueTypeMetricsMap = profileFieldValueType.getValueTypeMetricsMap();
  }

  @Override public String getTypeName() {
    return typeName;
  }

  public void setTypeName( String typeName ) {
    this.typeName = typeName;
  }

  @Override public Long getCount() {
    return count;
  }

  public void setCount( Long count ) {
    this.count = count;
  }

  @Override public Map<String, ValueTypeMetrics> getValueTypeMetricsMap() {
    return valueTypeMetricsMap;
  }

  public void setValueTypeMetricsMap(
    Map<String, ValueTypeMetrics> valueTypeMetricsMap ) {
    this.valueTypeMetricsMap = valueTypeMetricsMap;
  }

  @Override public ValueTypeMetrics getValueTypeMetrics( String name ) {
    return valueTypeMetricsMap.get( name );
  }

  @Override public Object clone() {
    return new ProfileFieldValueTypeDTO( this );
  }
}
