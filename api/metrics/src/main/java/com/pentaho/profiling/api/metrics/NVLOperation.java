package com.pentaho.profiling.api.metrics;

/**
 * Created by bryan on 2/3/15.
 */
public interface NVLOperation<T> {
  public T perform( T first, T second );
}
