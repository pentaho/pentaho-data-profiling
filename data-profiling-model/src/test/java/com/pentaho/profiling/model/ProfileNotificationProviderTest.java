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

import com.pentaho.profiling.notification.api.NotificationEvent;
import com.pentaho.profiling.notification.api.NotificationHandler;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by bryan on 8/22/14.
 */
public class ProfileNotificationProviderTest {
  @Test
  public void testGetNotificationType() {
    ProfileNotificationProvider profileNotificationProvider = new ProfileNotificationProvider();
    Assert
      .assertEquals( ProfileNotificationProvider.NOTIFICATION_TYPE, profileNotificationProvider.notificationType() );
  }

  @Test
  public void testRegisterHandlerWithPreviousNotifications() {
    NotificationHandler handler = mock( NotificationHandler.class );
    Set<String> interestedIds = new HashSet<String>( Arrays.asList( "TEST" ) );
    when( handler.getInterestedIds() ).thenReturn( interestedIds );
    ProfileNotificationProvider profileNotificationProvider = new ProfileNotificationProvider();
    long timestamp = System.currentTimeMillis();
    profileNotificationProvider.notify( "TEST" );
    profileNotificationProvider.registerHandler( handler );
    ArgumentCaptor<NotificationEvent> eventArgumentCaptor = ArgumentCaptor.forClass( NotificationEvent.class );
    verify( handler ).notify( eventArgumentCaptor.capture() );
    assertTrue( eventArgumentCaptor.getValue().getTimestamp() - timestamp < 2000 );
  }

  @Test
  public void testRegisterHandlerAddInterested() {
    NotificationHandler handler = mock( NotificationHandler.class );
    Set<String> interestedIds = new HashSet<String>( );
    when( handler.getInterestedIds() ).thenReturn( interestedIds );
    ProfileNotificationProvider profileNotificationProvider = new ProfileNotificationProvider();
    long timestamp = System.currentTimeMillis();
    profileNotificationProvider.notify( "TEST" );
    profileNotificationProvider.registerHandler( handler );
    verify( handler, times( 0 ) ).notify( any( NotificationEvent.class ) );
    interestedIds.add( "TEST" );
    profileNotificationProvider.addInterestedId( "TEST", handler );
    ArgumentCaptor<NotificationEvent> eventArgumentCaptor = ArgumentCaptor.forClass( NotificationEvent.class );
    verify( handler ).notify( eventArgumentCaptor.capture() );
    assertTrue( eventArgumentCaptor.getValue().getTimestamp() - timestamp < 2000 );
  }

  @Test
  public void testRegisterHandlerNotify() {
    NotificationHandler handler = mock( NotificationHandler.class );
    Set<String> interestedIds = new HashSet<String>( Arrays.asList( "TEST" ) );
    when( handler.getInterestedIds() ).thenReturn( interestedIds );
    ProfileNotificationProvider profileNotificationProvider = new ProfileNotificationProvider();
    profileNotificationProvider.registerHandler( handler );
    long timestamp = System.currentTimeMillis();
    profileNotificationProvider.notify( "TEST" );
    ArgumentCaptor<NotificationEvent> eventArgumentCaptor = ArgumentCaptor.forClass( NotificationEvent.class );
    verify( handler ).notify( eventArgumentCaptor.capture() );
    assertTrue( eventArgumentCaptor.getValue().getTimestamp() - timestamp < 2000 );
  }

  @Test
  public void testRegisterHandlerIrrelevantNotify() {
    NotificationHandler handler = mock( NotificationHandler.class );
    Set<String> interestedIds = new HashSet<String>( Arrays.asList( "TEST" ) );
    when( handler.getInterestedIds() ).thenReturn( interestedIds );
    ProfileNotificationProvider profileNotificationProvider = new ProfileNotificationProvider();
    profileNotificationProvider.registerHandler( handler );
    long timestamp = System.currentTimeMillis();
    profileNotificationProvider.notify( "TEST2" );
    ArgumentCaptor<NotificationEvent> eventArgumentCaptor = ArgumentCaptor.forClass( NotificationEvent.class );
    verify( handler, times( 0 ) ).notify( any( NotificationEvent.class ) );
  }

  @Test
  public void testRegisterHandlerRemove() {
    NotificationHandler handler = mock( NotificationHandler.class );
    Set<String> interestedIds = new HashSet<String>( Arrays.asList( "TEST" ) );
    when( handler.getInterestedIds() ).thenReturn( interestedIds );
    ProfileNotificationProvider profileNotificationProvider = new ProfileNotificationProvider();
    profileNotificationProvider.registerHandler( handler );
    profileNotificationProvider.unregisterHandler( handler );
    long timestamp = System.currentTimeMillis();
    profileNotificationProvider.notify( "TEST" );
    ArgumentCaptor<NotificationEvent> eventArgumentCaptor = ArgumentCaptor.forClass( NotificationEvent.class );
    verify( handler, times( 0 ) ).notify( any( NotificationEvent.class ) );
  }
}
