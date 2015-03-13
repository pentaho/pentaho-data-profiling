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

package com.pentaho.profiling.api.metrics;

import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;

/**
 * Created by bryan on 2/3/15.
 */
public class NVL {
  /**
   * Performs the operation if neither first nor second are null, if either is null, it returns the other
   *
   * @param nvlOperation the operation
   * @param first
   * @param second
   * @param <T>
   * @return the result of the operation or the non-null argument if one is null or null if both are null
   */
  public <T> T perform( NVLOperation<T> nvlOperation, T first, T second ) {
    if ( first == null ) {
      return second;
    } else if ( second == null ) {
      return first;
    } else {
      return nvlOperation.perform( first, second );
    }
  }

  /**
   * Performs the operation and sets the result into the metric manager based on the path
   *
   * @param nvlOperation the operation
   * @param into         the metric manager to set the value back on
   * @param from         the metric manager that provides the second value
   * @param path         the path that the value is at and should be set back to
   * @param <T>
   * @return the value that has been set
   */
  public <T> T performAndSet( NVLOperation<T> nvlOperation, DataSourceMetricManager into,
                              DataSourceMetricManager from, String... path ) {
    T result = perform( nvlOperation, (T) into.getValueNoDefault( path ), (T) from.getValueNoDefault( path ) );
    into.setValue( result, path );
    return result;
  }

  /**
   * Performs the operation and sets the result into the metric manager based on the path
   *
   * @param nvlOperation the operation
   * @param into         the metric manager to provide the first value and set the value back on
   * @param value        the second value
   * @param path         the path that the value is at and should be set back to
   * @param <T>
   * @return the value that has been set
   */
  public <T> T performAndSet( NVLOperation<T> nvlOperation, DataSourceMetricManager into,
                              T value, String... path ) {
    T result = perform( nvlOperation, (T) into.getValueNoDefault( path ), value );
    into.setValue( result, path );
    return result;
  }
}
