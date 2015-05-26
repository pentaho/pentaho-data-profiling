/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package com.pentaho.profiling.api.commit.strategies;

import com.pentaho.profiling.api.commit.CommitAction;
import com.pentaho.profiling.api.commit.CommitStrategy;

import java.util.concurrent.ExecutorService;

/**
 * Created by bryan on 5/6/15.
 */
public class ExponentialCommitStrategy implements CommitStrategy {
  private CommitAction commitAction;
  private long processed = 0L;
  private long nextCommit = 1L;
  private long step;
  private double multiplier;

  public ExponentialCommitStrategy() {
  }

  public ExponentialCommitStrategy( long step, double multiplier ) {
    this.step = step;
    this.multiplier = multiplier;
  }

  @Override public void init( CommitAction commitAction, ExecutorService executorService ) {
    this.commitAction = commitAction;
  }

  @Override
  public void eventProcessed() {
    if ( ++processed >= nextCommit ) {
      commitAction.perform();
      nextCommit += step;
      step *= multiplier;
    }
  }

  public double getMultiplier() {
    return multiplier;
  }

  public void setMultiplier( double multiplier ) {
    this.multiplier = multiplier;
  }

  public long getStep() {
    return step;
  }

  public void setStep( long step ) {
    this.step = step;
  }
}
