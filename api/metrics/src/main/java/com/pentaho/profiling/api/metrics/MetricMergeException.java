package com.pentaho.profiling.api.metrics;

/**
 * Exception indicating something wrong with a merge
 */
public class MetricMergeException extends Exception {
  public MetricMergeException( String message, Throwable cause ) {
    super( message, cause );
  }
}
