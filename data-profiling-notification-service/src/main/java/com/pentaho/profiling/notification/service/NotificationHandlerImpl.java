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

package com.pentaho.profiling.notification.service;

import com.pentaho.profiling.notification.api.NotificationEvent;
import com.pentaho.profiling.notification.api.NotificationHandler;
import com.pentaho.profiling.notification.api.NotificationProvider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by bryan on 8/22/14.
 */
public class NotificationHandlerImpl implements NotificationHandler {
  private final NotificationProvider notificationProvider;
  private final Map<String, Long> interestedMap;

  public NotificationHandlerImpl( NotificationProvider notificationProvider ) {
    this.notificationProvider = notificationProvider;
    this.interestedMap = new HashMap<String, Long>( );
  }

  @Override public Set<String> getInterestedIds() {
    synchronized ( interestedMap ) {
      return new HashSet<String>( interestedMap.keySet() );
    }
  }

  @Override public void notify( NotificationEvent notificationEvent ) {
    synchronized ( interestedMap ) {
      String id = notificationEvent.getId();
      Long oldTimestamp = interestedMap.get( id );
      Long newTimestamp = notificationEvent.getTimestamp();
      if ( oldTimestamp < newTimestamp ) {
        interestedMap.put( notificationEvent.getId(), notificationEvent.getTimestamp() );
      }
    }
  }

  public Long getLastModified( String id, Long timestamp ) {
    synchronized ( interestedMap ) {
      Long oldTimestamp = interestedMap.get( id );
      if ( oldTimestamp == null ) {
        interestedMap.put( id, timestamp );
        notificationProvider.addInterestedId( id, this );
        return  interestedMap.get( id );
      }
      return oldTimestamp;
    }
  }

  public NotificationProvider getNotificationProvider() {
    return notificationProvider;
  }
}
