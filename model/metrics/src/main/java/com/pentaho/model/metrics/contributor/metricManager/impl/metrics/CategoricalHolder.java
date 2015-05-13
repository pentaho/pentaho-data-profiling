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

package com.pentaho.model.metrics.contributor.metricManager.impl.metrics;

import com.pentaho.profiling.api.ValueTypeMetrics;

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
