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

package com.pentaho.profiling.model;

import com.pentaho.profiling.api.ProfilingService;
import com.pentaho.profiling.notification.api.NotificationEvent;
import com.pentaho.profiling.notification.api.NotificationHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bryan on 8/21/14.
 */
public class ProfileNotificationProviderImpl implements ProfileNotificationProvider {
  public static String NOTIFICATION_TYPE = "com.pentaho.profiling.model.ProfileNotificationProvider";
  private final Map<String, Long> changedIds = new ConcurrentHashMap<String, Long>();
  private final List<NotificationHandler> notificationHandlers = new ArrayList<NotificationHandler>();
  private ProfilingService profilingService;

  public void setProfilingService( ProfilingService profilingService ) {
    this.profilingService = profilingService;
  }

  @Override public void notify( String id ) {
    List<NotificationHandler> notificationHandlers;
    synchronized ( this.notificationHandlers ) {
      notificationHandlers = new ArrayList<NotificationHandler>( this.notificationHandlers );
    }
    changedIds.put( id, System.currentTimeMillis() );
    for ( NotificationHandler notificationHandler : notificationHandlers ) {
      notify( id, notificationHandler );
    }
  }

  @Override public String notificationType() {
    return NOTIFICATION_TYPE;
  }

  @Override public void addInterestedId( String id, NotificationHandler notificationHandler ) {
    notify( id, notificationHandler );
  }

  private void notify( String id, NotificationHandler notificationHandler ) {
    if ( notificationHandler.getInterestedIds().contains( id ) ) {
      Long timestamp = changedIds.get( id );
      if ( timestamp != null ) {
        notificationHandler
          .notify( new NotificationEvent( NOTIFICATION_TYPE, id, profilingService.getProfileUpdate( id ), timestamp ) );
      }
    }
  }

  @Override public void registerHandler( NotificationHandler notificationHandler ) {
    synchronized ( notificationHandlers ) {
      notificationHandlers.add( notificationHandler );
    }
    for ( String id : notificationHandler.getInterestedIds() ) {
      notify( id, notificationHandler );
    }
  }

  @Override public void unregisterHandler( NotificationHandler notificationHandler ) {
    synchronized ( notificationHandlers ) {
      notificationHandlers.remove( notificationHandler );
    }
  }
}
