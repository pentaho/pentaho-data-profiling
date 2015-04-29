package com.pentaho.profiling.api;

/**
 * Created by bryan on 5/6/15.
 */
public interface StreamingCommitStrategy {
  boolean isTimestamp();
  long getNextCommit( long currentCommit );
}
