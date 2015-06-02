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

package org.pentaho.profiling.api.performance;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by bryan on 2/27/15.
 */
public class Collector {
  private final Thread thread;
  private final Thread monitorThread;
  private final Node rootNode;
  private final AtomicBoolean stopped;
  private final String name;

  public Collector( final Thread thread ) {
    this.thread = thread;
    this.name = thread.getName() + "-perf-collector";
    this.stopped = new AtomicBoolean( false );
    this.rootNode = new Node();
    this.monitorThread = new Thread( new Runnable() {
      @Override public void run() {
        while ( !stopped.get() && thread.isAlive() ) {
          long start = System.currentTimeMillis();
          Node currentNode = rootNode;
          StackTraceElement[] stackTrace = thread.getStackTrace();
          for ( int i = stackTrace.length - 1; i >= 0; i-- ) {
            currentNode = currentNode.locateNode( stackTrace[ i ].toString() );
            currentNode.incrementCount();
          }
          try {
            Thread.sleep( Math.max( 0L, start + 50L - System.currentTimeMillis() ) );
          } catch ( InterruptedException e ) {
            stopped.set( true );
          }
        }
      }
    } );
  }

  public void start() {
    monitorThread.start();
  }

  public void stop() {
    stopped.set( true );
    try {
      monitorThread.join();
    } catch ( InterruptedException e ) {
      // Ignore
    }
  }

  public Node getRootNode() {
    return rootNode;
  }

  public String getName() {
    return name;
  }
}
