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

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

/**
 * Created by bryan on 8/22/14.
 */
public class NotificationRequestTest {
  @Test
  public void testNoArgConstructor() {
    NotificationRequest notificationRequest = new NotificationRequest(  );
    assertNull( notificationRequest.getEntries() );
    assertNull( notificationRequest.getNotificationType() );
  }

  @Test
  public void testNotificationTypeEntriesConstructor() {
    NotificationRequestEntry entry = mock( NotificationRequestEntry.class );
    List<NotificationRequestEntry> list = new ArrayList<NotificationRequestEntry>( Arrays.asList(entry) );
    NotificationRequest notificationRequest = new NotificationRequest( "TEST_TYPE", list );
    assertEquals( "TEST_TYPE", notificationRequest.getNotificationType() );
    assertEquals( list, notificationRequest.getEntries() );
  }

  @Test
  public void testSetNotificationType() {
    NotificationRequest notificationRequest = new NotificationRequest(  );
    notificationRequest.setNotificationType( "TEST_TYPE" );
    assertEquals( "TEST_TYPE", notificationRequest.getNotificationType() );
  }

  @Test
  public void testSetEntries() {
    NotificationRequestEntry entry = mock( NotificationRequestEntry.class );
    List<NotificationRequestEntry> list = new ArrayList<NotificationRequestEntry>( Arrays.asList(entry) );
    NotificationRequest notificationRequest = new NotificationRequest( );
    notificationRequest.setEntries( list );
    assertEquals( list, notificationRequest.getEntries() );
  }
}
