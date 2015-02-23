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

package com.pentaho.profiling.api.metrics.field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that maintains metrics being computed for a given DataSourceField.
 * <p/>
 * Created by bryan on 10/29/14.
 */
public class DataSourceMetricManager {
  /**
   * map of metric values
   */
  private final Map<String, Object> metricMap;

  public DataSourceMetricManager() {
    this( new HashMap<String, Object>() );
  }

  public DataSourceMetricManager( Map<String, Object> metricMap ) {
    this.metricMap = metricMap;
  }

  /**
   * Get the value of a particular metric
   *
   * @param defaultValue default value for the metric if it doesn't exist yet
   * @param path         identifying path (not to be confused with a hierarchical field path) to the metric to get
   * @param <T>          type of the metric
   * @return the metric value
   */
  public <T> T getValue( T defaultValue, String... path ) {
    Map<String, Object> current = metricMap;
    for ( int i = 0; i < path.length - 1; i++ ) {
      Object next = current.get( path[ i ] );
      if ( next == null || !( next instanceof Map ) ) {
        return defaultValue;
      }
      current = (Map<String, Object>) next;
    }
    T result = (T) current.get( path[ path.length - 1 ] );
    if ( result == null ) {
      result = defaultValue;
    }
    return result;
  }

  /**
   * Get the value of a particular metric. Returns null if the metric doesn't exist yet
   *
   * @param path path to the metric to get
   * @param <T>  type of the metric
   * @return the metric value
   */
  public <T> T getValueNoDefault( String... path ) {
    return getValue( null, path );
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    DataSourceMetricManager that = (DataSourceMetricManager) o;

    if ( metricMap != null ? !metricMap.equals( that.metricMap ) : that.metricMap != null ) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return metricMap != null ? metricMap.hashCode() : 0;
  }

  /**
   * Set the value of a particular metric
   *
   * @param value the metric value to set
   * @param path  the identifying path to the metric. Not to be confused with a hierarchical field value path
   * @param <T>   the type of metric value
   */
  public <T> void setValue( T value, String... path ) {
    Map<String, Object> current = metricMap;
    for ( int i = 0; i < path.length - 1; i++ ) {
      Object next = current.get( path[ i ] );
      if ( next == null ) {
        next = new HashMap<String, Object>();
        current.put( path[ i ], next );
      }
      current = (Map<String, Object>) next;
    }
    if ( value != null ) {
      current.put( path[ path.length - 1 ], value );
    } else {
      current.remove( path[ path.length - 1 ] );
    }
  }

  public void update( DataSourceMetricManager other ) {
    metricMap.putAll( other.metricMap );
  }

  public void clear( List<String[]> paths ) {
    for ( String[] path : paths ) {
      setValue( null, path );
    }
  }
}
