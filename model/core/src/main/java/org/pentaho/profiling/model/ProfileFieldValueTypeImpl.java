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

package org.pentaho.profiling.model;

import org.pentaho.profiling.api.ProfileFieldValueType;
import org.pentaho.profiling.api.ValueTypeMetrics;

import java.util.HashMap;
import java.util.Map;

import static org.pentaho.profiling.api.util.PublicCloneableUtil.copyMap;

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
