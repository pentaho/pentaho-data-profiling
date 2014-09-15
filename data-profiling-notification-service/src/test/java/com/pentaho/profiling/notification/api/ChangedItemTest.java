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

package com.pentaho.profiling.notification.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by bryan on 8/22/14.
 */
public class ChangedItemTest {
  @Test
  public void testNoArgConstructor() {
    ChangedItem changedItem = new ChangedItem(  );
    assertEquals( 0L, changedItem.getTimestamp() );
    assertNull( changedItem.getId() );
    assertNull( changedItem.getChangedObject() );
  }

  @Test
  public void testTimestampIdObjectConstructor() {
    Object object = new Object();
    ChangedItem changedItem = new ChangedItem( 1L, "TEST", object );
    assertEquals( 1L, changedItem.getTimestamp() );
    assertEquals( "TEST", changedItem.getId() );
    assertEquals( object, changedItem.getChangedObject() );
  }

  @Test
  public void testSetTimestamp() {
    ChangedItem changedItem = new ChangedItem(  );
    changedItem.setTimestamp( 2L );
    assertEquals( 2L, changedItem.getTimestamp() );
  }

  @Test
  public void testSetId() {
    ChangedItem changedItem = new ChangedItem(  );
    changedItem.setId( "TEST_ID" );
    assertEquals( "TEST_ID", changedItem.getId() );
  }

  @Test
  public void testSetChangedObject() {
    Object object = new Object();
    ChangedItem changedItem = new ChangedItem();
    changedItem.setChangedObject( object );
    assertEquals( object, changedItem.getChangedObject() );
  }
}
