package com.pentaho.profiling.api.streaming;

import com.pentaho.profiling.api.StreamingCommitStrategy;

/**
 * Created by bryan on 5/6/15.
 */
public class ExponentialCommitStrategy implements StreamingCommitStrategy {
  private long commit;
  private double multiplier;

  public ExponentialCommitStrategy() {
  }

  public ExponentialCommitStrategy( long commit, double multiplier ) {
    this.commit = commit;
    this.multiplier = multiplier;
  }

  public long getCommit() {
    return commit;
  }

  public void setCommit( long commit ) {
    this.commit = commit;
  }

  public double getMultiplier() {
    return multiplier;
  }

  public void setMultiplier( double multiplier ) {
    this.multiplier = multiplier;
  }

  @Override public boolean isTimestamp() {
    return false;
  }

  @Override public long getNextCommit( long currentCommit ) {
    if ( commit <= currentCommit ) {
      commit = (long) ( currentCommit + ( currentCommit * multiplier ) );
    }
    return commit;
  }
}
