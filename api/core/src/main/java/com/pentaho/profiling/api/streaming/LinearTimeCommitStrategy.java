package com.pentaho.profiling.api.streaming;

import com.pentaho.profiling.api.StreamingCommitStrategy;

/**
 * Created by bryan on 5/6/15.
 */
public class LinearTimeCommitStrategy implements StreamingCommitStrategy {
  private long timeStep;

  public LinearTimeCommitStrategy() {
  }

  public LinearTimeCommitStrategy( long timeStep ) {
    this.timeStep = timeStep;
  }

  public long getTimeStep() {
    return timeStep;
  }

  public void setTimeStep( long timeStep ) {
    this.timeStep = timeStep;
  }

  @Override public boolean isTimestamp() {
    return true;
  }

  @Override public long getNextCommit( long currentCommit ) {
    return currentCommit + timeStep;
  }
}
