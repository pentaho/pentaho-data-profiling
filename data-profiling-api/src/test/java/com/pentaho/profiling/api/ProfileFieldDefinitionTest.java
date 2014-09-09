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

package com.pentaho.profiling.api;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

/**
 * Created by bryan on 9/9/14.
 */
public class ProfileFieldDefinitionTest {
  @Test
  public void testNoArgConstructor() {
    assertNull( new ProfileFieldDefinition().getProfileFieldProperties() );
  }

  @Test
  public void testPropertiesConstructor() {
    ProfileFieldProperty profileFieldProperty = mock( ProfileFieldProperty.class );
    List<ProfileFieldProperty> profileFieldProperties =
      new ArrayList<ProfileFieldProperty>( Arrays.asList( profileFieldProperty ) );
    assertEquals( profileFieldProperties,
      new ProfileFieldDefinition( profileFieldProperties ).getProfileFieldProperties() );
  }

  @Test
  public void testSetter() {
    ProfileFieldProperty profileFieldProperty = mock( ProfileFieldProperty.class );
    List<ProfileFieldProperty> profileFieldProperties =
      new ArrayList<ProfileFieldProperty>( Arrays.asList( profileFieldProperty ) );
    ProfileFieldDefinition profileFieldDefinition = new ProfileFieldDefinition( );
    profileFieldDefinition.setProfileFieldProperties( profileFieldProperties );
    assertEquals( profileFieldProperties, profileFieldDefinition.getProfileFieldProperties() );
  }
}
