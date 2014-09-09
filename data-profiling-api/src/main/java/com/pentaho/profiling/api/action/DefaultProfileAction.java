/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
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

package com.pentaho.profiling.api.action;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by bryan on 8/1/14.
 */
public abstract class DefaultProfileAction implements ProfileAction {
  private final AtomicBoolean stopped;
  private ProfileAction then;
  private volatile boolean thenRequested = false;

  public DefaultProfileAction() {
    this( null, new AtomicBoolean( false ) );
  }

  public DefaultProfileAction( ProfileAction then, AtomicBoolean stopped ) {
    this.then = then;
    this.stopped = stopped;
  }

  @Override
  public synchronized ProfileAction then() {
    thenRequested = true;
    if ( stopped.get() ) {
      return null;
    } else {
      if ( then == null ) {
        stopped.set( true );
      }
      return then;
    }
  }

  public synchronized void setThen( ProfileAction then ) throws ThenAlreadyRequestedException {
    if ( !thenRequested ) {
      this.then = then;
    } else {
      throw new ThenAlreadyRequestedException();
    }
  }

  @Override public void stop() {
    stopped.set( true );
  }

  public AtomicBoolean getStopped() {
    return stopped;
  }
}
