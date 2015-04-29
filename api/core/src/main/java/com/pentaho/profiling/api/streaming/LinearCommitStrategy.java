package com.pentaho.profiling.api.streaming;

import com.pentaho.profiling.api.StreamingCommitStrategy;

/**
 * Created by bryan on 5/6/15.
 */
public class LinearCommitStrategy implements StreamingCommitStrategy {
  private long step;

  public LinearCommitStrategy() {
  }

  public LinearCommitStrategy( long step ) {
    this.step = step;
  }

  public long getStep() {
    return step;
  }

  public void setStep( long step ) {
    this.step = step;
  }

  @Override public boolean isTimestamp() {
    return false;
  }

  @Override public long getNextCommit( long currentCommit ) {
    return currentCommit + step;
  }
}
