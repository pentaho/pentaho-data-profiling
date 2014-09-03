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
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by bryan on 8/22/14.
 */
public class NotificationServiceTest {
  @Test
  public void testWithOneEligibleProviderNewTimestamp() {
    final String notificationType = "TEST_TYPE";
    final String id = "TEST_ID";
    NotificationRequest notificationRequest = mock( NotificationRequest.class );
    NotificationProvider notificationProvider = mock( NotificationProvider.class );
    NotificationService service = new NotificationService();
    NotificationRequestWrapper notificationRequestWrapper = mock( NotificationRequestWrapper.class );
    NotificationRequestEntry notificationRequestEntry = mock( NotificationRequestEntry.class );

    List<NotificationResponse> result = new ArrayList<NotificationResponse>();
    when( notificationRequestWrapper.getRequests() ).thenReturn( Arrays.asList( notificationRequest ) );
    when( notificationRequest.getNotificationType() ).thenReturn( notificationType );
    when( notificationRequest.getEntries() ).thenReturn( Arrays.asList( notificationRequestEntry ) );
    when( notificationProvider.notificationType() ).thenReturn( notificationType );
    when( notificationRequestEntry.getKey() ).thenReturn( id );
    when( notificationRequestEntry.getValue() ).thenReturn( 10L );
    doAnswer( new Answer<Void>() {
      @Override public Void answer( InvocationOnMock invocation ) throws Throwable {
        ( (NotificationHandler) invocation.getArguments()[ 1 ] )
          .notify( new NotificationEvent( notificationType, id, 11L ) );
        return null;
      }
    } ).when( notificationProvider ).addInterestedId( eq( id ), any( NotificationHandler.class ) );

    service.addNotificationProvider( notificationProvider );
    service.getNotificationsHelper( notificationRequestWrapper, result );
    assertEquals( 1, result.size() );
    assertEquals( 1, result.get( 0 ).getChangedItems().size() );
    assertEquals( id, result.get( 0 ).getChangedItems().get( 0 ).getId() );
    assertEquals( 11L, result.get( 0 ).getChangedItems().get( 0 ).getTimestamp() );
  }

  @Test
  public void testWithOneEligibleProviderOldTimestamp() {
    final String notificationType = "TEST_TYPE";
    final String id = "TEST_ID";
    NotificationRequest notificationRequest = mock( NotificationRequest.class );
    NotificationProvider notificationProvider = mock( NotificationProvider.class );
    NotificationService service = new NotificationService();
    NotificationRequestWrapper notificationRequestWrapper = mock( NotificationRequestWrapper.class );
    NotificationRequestEntry notificationRequestEntry = mock( NotificationRequestEntry.class );

    List<NotificationResponse> result = new ArrayList<NotificationResponse>();
    when( notificationRequestWrapper.getRequests() ).thenReturn( Arrays.asList( notificationRequest ) );
    when( notificationRequest.getNotificationType() ).thenReturn( notificationType );
    when( notificationRequest.getEntries() ).thenReturn( Arrays.asList( notificationRequestEntry ) );
    when( notificationProvider.notificationType() ).thenReturn( notificationType );
    when( notificationRequestEntry.getKey() ).thenReturn( id );
    when( notificationRequestEntry.getValue() ).thenReturn( 10L );
    doAnswer( new Answer<Void>() {
      @Override public Void answer( InvocationOnMock invocation ) throws Throwable {
        ( (NotificationHandler) invocation.getArguments()[ 1 ] )
          .notify( new NotificationEvent( notificationType, id, 9L ) );
        return null;
      }
    } ).when( notificationProvider ).addInterestedId( eq( id ), any( NotificationHandler.class ) );

    service.addNotificationProvider( notificationProvider );
    service.getNotificationsHelper( notificationRequestWrapper, result );
    assertEquals( 0, result.size() );
  }

  @Test
  public void testWithNoEligibleProvider() {
    final String notificationType = "TEST_TYPE";
    final String id = "TEST_ID";
    NotificationRequest notificationRequest = mock( NotificationRequest.class );
    NotificationProvider notificationProvider = mock( NotificationProvider.class );
    NotificationService service = new NotificationService();
    NotificationRequestWrapper notificationRequestWrapper = mock( NotificationRequestWrapper.class );
    NotificationRequestEntry notificationRequestEntry = mock( NotificationRequestEntry.class );
    List<NotificationResponse> result = new ArrayList<NotificationResponse>();
    when( notificationRequestWrapper.getRequests() ).thenReturn( Arrays.asList( notificationRequest ) );
    when( notificationRequest.getNotificationType() ).thenReturn( notificationType );
    when( notificationRequest.getEntries() ).thenReturn( Arrays.asList( notificationRequestEntry ) );
    when( notificationProvider.notificationType() ).thenReturn( "DUNNO" );
    when( notificationRequestEntry.getKey() ).thenReturn( id );
    when( notificationRequestEntry.getValue() ).thenReturn( 10L );
    service.addNotificationProvider( notificationProvider );
    service.getNotificationsHelper( notificationRequestWrapper, result );
  }

  @Test
  public void testUnregister() {
    final String notificationType = "TEST_TYPE";
    final String id = "TEST_ID";
    NotificationProvider notificationProvider = mock( NotificationProvider.class );
    NotificationService service = new NotificationService();
    NotificationRequestWrapper notificationRequestWrapper = mock( NotificationRequestWrapper.class );
    NotificationRequestEntry notificationRequestEntry = mock( NotificationRequestEntry.class );

    when( notificationProvider.notificationType() ).thenReturn( notificationType );

    service.addNotificationProvider( notificationProvider );
    service.removeNotificationProvider( notificationProvider );
    verify( notificationProvider, times( 1 ) ).unregisterHandler( any( NotificationHandler.class ) );
  }
}
