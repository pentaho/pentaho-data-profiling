/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
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

import org.pentaho.profiling.api.MutableProfileFieldValueType;
import org.pentaho.profiling.api.ProfileFieldValueType;
import org.pentaho.profiling.api.ValueTypeMetrics;

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
