/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2015 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */

package com.pentaho.profiling.api.performance;

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
