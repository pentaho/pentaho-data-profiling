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

package com.pentaho.profiling.model;

import com.pentaho.profiling.api.ProfileFieldValueType;
import com.pentaho.profiling.api.ValueTypeMetrics;

import java.util.HashMap;
import java.util.Map;

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
    this.typeMetrics = new HashMap<String, ValueTypeMetrics>();
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
