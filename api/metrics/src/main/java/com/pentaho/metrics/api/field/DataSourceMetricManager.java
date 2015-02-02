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

package com.pentaho.metrics.api.field;

import com.pentaho.metrics.api.ComplexMetricEstimatorHolder;

import java.io.IOException;
import java.util.HashMap;
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
  private Map<String, Object> metricMap;

  /**
   * map of complex metric value estimators
   */
  private Map<String, Object> complexMap;

  public DataSourceMetricManager( Map<String, Object> metricMap ) {
    this.metricMap = metricMap;

    complexMap = new HashMap<String, Object>();
  }

  /**
   * Get the value of a particular metric
   *
   * @param defaultValue default value for the metric if it doesn't exist yet
   * @param path         identifying path (not to be confused with a hierarchical field
   *                     path) to the metric to get
   * @param <T>          type of the metric
   * @return the metric value
   */
  public <T> T getValue( T defaultValue, String... path ) {
    Map<String, Object> current = metricMap;
    for ( int i = 0; i < path.length - 1; i++ ) {
      Object next = current.get( path[i] );
      if ( next == null || !( next instanceof Map ) ) {
        return defaultValue;
      }
      current = (Map<String, Object>) next;
    }
    T result = (T) current.get( path[path.length - 1] );
    if ( result == null ) {
      result = defaultValue;
    }
    return result;
  }

  /**
   * Get the value of a particular metric. Returns null if the metric doesn't
   * exist yet
   *
   * @param path path to the metric to get
   * @param <T>  type of the metric
   * @return the metric value
   */
  public <T> T getValueNoDefault( String... path ) {
    return getValue( null, path );
  }

  /**
   * Set the value of a particular metric
   *
   * @param value the metric value to set
   * @param path  the identifying path to the metric. Not to be confused with a
   *              hierarchical field value path
   * @param <T>   the type of metric value
   */
  public <T> void setValue( T value, String... path ) {
    Map<String, Object> current = metricMap;
    for ( int i = 0; i < path.length - 1; i++ ) {
      Object next = current.get( path[i] );
      if ( next == null ) {
        next = new HashMap<String, Object>();
        current.put( path[i], next );
      }
      current = (Map<String, Object>) next;
    }
    if ( value != null ) {
      current.put( path[path.length - 1], value );
    } else {
      current.remove( path[path.length - 1] );
    }
  }

  /**
   * Get a particular complex metric estimator. Returns null if the estimator
   * does not exist yet
   *
   * @param path identifying path to the metric estimator to get. Path is not to
   *             be confused with confused with a hierarchical field value path
   * @return the complex metric estimator
   */
  public ComplexMetricEstimatorHolder getComplexMetricEstimatorHolder( String... path ) {
    Map<String, Object> current = complexMap;
    for ( int i = 0; i < path.length - 1; i++ ) {
      Object next = current.get( path[i] );
      if ( next == null || !( next instanceof Map ) ) {
        return null;
      }
      current = (Map<String, Object>) next;
    }

    ComplexMetricEstimatorHolder result = (ComplexMetricEstimatorHolder) current.get( path[path.length - 1] );

    return result;
  }

  /**
   * Set a complex metric estimator
   *
   * @param holder the complex metric estimator (wrapped in a suitable
   *               ComplexMetricEstimatorHolder)
   * @param path   the identifying path to the estimator
   */
  public void setComplexMetricEstimatorHolder( ComplexMetricEstimatorHolder holder, String... path ) {
    Map<String, Object> current = complexMap;
    for ( int i = 0; i < path.length - 1; i++ ) {
      Object next = current.get( path[i] );
      if ( next == null ) {
        next = new HashMap<String, Object>();
        current.put( path[i], next );
      }
      current = (Map<String, Object>) next;
    }
    if ( holder != null ) {
      current.put( path[path.length - 1], holder );
    } else {
      current.remove( path[path.length - 1] );
    }
  }

  /**
   * Transfers each complex metric, as a byte array, to the main value
   * map. Preserves the paths for each estimator
   *
   * @throws IOException if a serialization problem occurs
   */
  public void transferSerializedComplexMetricEstimatorHolders() throws IOException {
    // traverse the map of maps and build up paths

    transfer( "", complexMap );
  }

  protected void transfer( String path, Object o ) throws IOException {
    if ( !( o instanceof Map ) ) {
      String[] pathParts = path.split( "," );
      byte[] serialized = ( (ComplexMetricEstimatorHolder) o ).getSerialized();
      setValue( serialized, pathParts );
    } else {
      for ( Map.Entry<String, Object> e : ( (Map<String, Object>) o ).entrySet() ) {
        String newPath = path.length() == 0 ? e.getKey() : path + "," + e.getKey();
        transfer( newPath, e.getValue() );
      }
    }
  }
}
