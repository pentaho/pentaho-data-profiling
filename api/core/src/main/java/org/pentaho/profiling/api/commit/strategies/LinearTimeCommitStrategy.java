/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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

package org.pentaho.profiling.api.commit.strategies;

import org.pentaho.profiling.api.commit.CommitAction;
import org.pentaho.profiling.api.commit.CommitStrategy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by bryan on 5/6/15.
 */
public class LinearTimeCommitStrategy implements CommitStrategy {
  private final AtomicBoolean refreshQueued = new AtomicBoolean( false );
  private volatile long nextRefreshMillis = 0L;
  private CommitAction commitAction;
  private final Runnable commitRunnable = new Runnable() {
    @Override public void run() {
      try {
        Thread.sleep( Math.max( 0L, nextRefreshMillis - System.currentTimeMillis() ) );
      } catch ( InterruptedException e ) {
        // Ignore
      }
      nextRefreshMillis = System.currentTimeMillis() + timeStep;
      refreshQueued.set( false );
      commitAction.perform();
    }
  };
  private ExecutorService executorService;
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

  @Override public void init( CommitAction commitAction, ExecutorService executorService ) {
    this.commitAction = commitAction;
    this.executorService = executorService;
  }

  @Override public void eventProcessed() {
    if ( !refreshQueued.getAndSet( true ) ) {
      executorService.submit( commitRunnable );
    }
  }
}
