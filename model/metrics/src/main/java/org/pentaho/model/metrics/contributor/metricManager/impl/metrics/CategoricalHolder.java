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

package org.pentaho.model.metrics.contributor.metricManager.impl.metrics;

import org.pentaho.profiling.api.ValueTypeMetrics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bryan on 4/30/15.
 */
public class CategoricalHolder implements ValueTypeMetrics {
  private int maxSize;
  private Map<String, Long> categories;

  public CategoricalHolder() {
    this( 100, new HashMap<String, Long>() );
  }

  public CategoricalHolder( int maxSize, Map<String, Long> categories ) {
    this.maxSize = maxSize;
    this.categories = categories;
  }

  public void addEntry( String value ) {
    if ( categories != null ) {
      Long aLong = categories.get( value );
      if ( aLong == null ) {
        if ( categories.size() >= maxSize ) {
          categories = null;
        } else {
          categories.put( value, 1L );
        }
      } else {
        categories.put( value, aLong + 1 );
      }
    }
  }

  public void add( CategoricalHolder other ) {
    if ( other.getCategorical() && categories != null ) {
      for ( Map.Entry<String, Long> stringLongEntry : other.getCategories().entrySet() ) {
        Long existing = categories.get( stringLongEntry.getKey() );
        if ( existing == null ) {
          categories.put( stringLongEntry.getKey(), stringLongEntry.getValue() );
        } else {
          categories.put( stringLongEntry.getKey(), existing + stringLongEntry.getValue() );
        }
      }
      if ( categories.size() > maxSize ) {
        categories = null;
      }
    } else {
      categories = null;
    }
  }

  public int getMaxSize() {
    return maxSize;
  }

  public void setMaxSize( int maxSize ) {
    this.maxSize = maxSize;
  }

  public boolean getCategorical() {
    return categories != null;
  }

  public Map<String, Long> getCategories() {
    return categories;
  }

  public void setCategories( Map<String, Long> categories ) {
    this.categories = categories;
  }

  @Override public Object clone() {
    return new CategoricalHolder( maxSize, categories );
  }
}
