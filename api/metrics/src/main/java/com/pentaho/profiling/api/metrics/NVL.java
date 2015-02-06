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
