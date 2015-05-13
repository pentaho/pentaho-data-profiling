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
