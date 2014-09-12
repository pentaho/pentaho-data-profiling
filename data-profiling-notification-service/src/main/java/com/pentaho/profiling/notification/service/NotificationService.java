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

import com.pentaho.profiling.notification.api.ChangedItem;
import com.pentaho.profiling.notification.api.NotificationProvider;

import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bryan on 8/21/14.
 */
@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
@Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
@WebService
public class NotificationService {
  public static final long TIMEOUT = 30 * 1000;
  public static final long INTERVAL = 500;
  private volatile Map<String, NotificationHandlerImpl> notificationHandlerMap =
    new HashMap<String, NotificationHandlerImpl>();

  public void addNotificationProvider( NotificationProvider notificationProvider ) {
    NotificationHandlerImpl notificationHandler = notificationHandlerMap.get( notificationProvider.notificationType() );
    if ( notificationHandler != null ) {
      throw new IllegalStateException( "Can't handle multiple notification providers of same type" );
    }
    notificationHandler = new NotificationHandlerImpl( notificationProvider );
    notificationHandlerMap.put( notificationProvider.notificationType(), notificationHandler );
    notificationProvider.registerHandler( notificationHandler );
  }

  public void removeNotificationProvider( NotificationProvider notificationProvider ) {
    notificationProvider.unregisterHandler( notificationHandlerMap.remove( notificationProvider.notificationType() ) );
  }

  protected void getNotificationsHelper( NotificationRequestWrapper notificationRequestWrapper,
                                         List<NotificationResponse> result ) {
    Map<String, NotificationHandlerImpl> notificationHandlerMap = this.notificationHandlerMap;
    for ( NotificationRequest notificationRequest : notificationRequestWrapper.getRequests() ) {
      NotificationHandlerImpl notificationHandlerImpl =
        notificationHandlerMap.get( notificationRequest.getNotificationType() );
      if ( notificationHandlerImpl != null ) {
        List<ChangedItem> changedItems = new ArrayList<ChangedItem>();
        for ( NotificationRequestEntry entry : notificationRequest.getEntries() ) {
          String id = entry.getKey();
          Long oldTimestamp = entry.getValue();
          NotificationState notificationState = notificationHandlerImpl.getLastModified( id, oldTimestamp );
          Long newTimestamp = notificationState.getTimestamp();
          if ( newTimestamp > oldTimestamp ) {
            changedItems.add( new ChangedItem( newTimestamp, id, notificationState.getChangedObject() ) );
          }
        }
        if ( changedItems.size() > 0 ) {
          NotificationResponse notificationResponse = new NotificationResponse();
          notificationResponse.setNotificationType( notificationRequest.getNotificationType() );
          notificationResponse.setChangedItems( changedItems );
          result.add( notificationResponse );
        }
      }
    }
  }

  @POST
  @Path( "/" )
  public List<NotificationResponse> getNotifications( NotificationRequestWrapper notificationRequestWrapper ) {
    long before = System.currentTimeMillis();
    List<NotificationResponse> result = new ArrayList<NotificationResponse>();
    Map<String, NotificationHandlerImpl> notificationHandlerMap = this.notificationHandlerMap;
    do {
      getNotificationsHelper( notificationRequestWrapper, result );
      if ( result.size() > 0 ) {
        break;
      }
      try {
        Thread.sleep( INTERVAL );
      } catch ( InterruptedException e ) {
        // Noop
      }
    }
    while ( System.currentTimeMillis() - before < TIMEOUT );
    return result;
  }
}
