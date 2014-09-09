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

package com.pentaho.profiling.api.operations;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by bryan on 9/9/14.
 */
public class ProfileOperationTest {
  @Test
  public void testNoArgConstructor() {
    ProfileOperation profileOperation = new ProfileOperation();
    assertNull( profileOperation.getNameKey() );
    assertNull( profileOperation.getNamePath() );
    assertNull( profileOperation.getId() );
  }

  @Test
  public void testIdNamePathNameKeyConstructor() {
    String id = "id";
    String namePath = "name-path";
    String nameKey = "name-key";
    ProfileOperation profileOperation = new ProfileOperation( id, namePath, nameKey );
    assertEquals( id, profileOperation.getId() );
    assertEquals( namePath, profileOperation.getNamePath() );
    assertEquals( nameKey, profileOperation.getNameKey() );
  }

  @Test
  public void testSetId() {
    String id = "id";
    ProfileOperation profileOperation = new ProfileOperation();
    profileOperation.setId( id );
    assertEquals( id, profileOperation.getId() );
  }

  @Test
  public void testSetNamePath() {
    String namePath = "name-path";
    ProfileOperation profileOperation = new ProfileOperation();
    profileOperation.setNamePath( namePath );
    assertEquals( namePath, profileOperation.getNamePath() );
  }

  @Test
  public void testSetNameKey() {
    String nameKey = "name-key";
    ProfileOperation profileOperation = new ProfileOperation();
    profileOperation.setNameKey( nameKey );
    assertEquals( nameKey, profileOperation.getNameKey() );
  }
}
