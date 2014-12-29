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

import static org.junit.Assert.*;

/**
 * Created by bryan on 9/9/14.
 */
public class ProfileFieldPropertyTest {
  @Test
  public void testNoArgConstructor() {
    ProfileFieldProperty profileFieldProperty = new ProfileFieldProperty();
    assertNull( profileFieldProperty.getNameKey() );
    assertNull( profileFieldProperty.getNamePath() );
    assertEquals( 0, profileFieldProperty.getPathToProperty().size() );
  }

  @Test
  public void testNamePathNameKeyPathToPropertyConstructor() {
    String testPath = "test-path";
    String testKey = "test-key";
    List<String> testPathToProperty = new ArrayList<String>( Arrays.asList( "A", "b", "C" ) );
    ProfileFieldProperty profileFieldProperty = new ProfileFieldProperty( testPath, testKey, testPathToProperty );
    assertEquals( testPath, profileFieldProperty.getNamePath() );
    assertEquals( testKey, profileFieldProperty.getNameKey() );
    assertEquals( testPathToProperty, profileFieldProperty.getPathToProperty() );
  }

  @Test
  public void testHashCodeConsistent() {
    String testPath = "test-path";
    String testKey = "test-key";
    List<String> testPathToProperty = new ArrayList<String>( Arrays.asList( "A", "b", "C" ) );
    assertEquals( new ProfileFieldProperty( testPath, testKey, testPathToProperty ).hashCode(),
      new ProfileFieldProperty( testPath, testKey, testPathToProperty ).hashCode() );
  }

  @Test
  public void testEqualsPositiveCase() {
    String testPath = "test-path";
    String testKey = "test-key";
    List<String> testPathToProperty = new ArrayList<String>( Arrays.asList( "A", "b", "C" ) );
    assertTrue( new ProfileFieldProperty( testPath, testKey, testPathToProperty ).equals(
      new ProfileFieldProperty( testPath, testKey, testPathToProperty ) ) );
  }

  @Test
  public void testEqualsReferenceEqual() {
    String testPath = "test-path";
    String testKey = "test-key";
    List<String> testPathToProperty = new ArrayList<String>( Arrays.asList( "A", "b", "C" ) );
    ProfileFieldProperty profileFieldProperty = new ProfileFieldProperty( testPath, testKey, testPathToProperty );
    assertTrue( profileFieldProperty.equals( profileFieldProperty ) );
  }

  @Test
  public void testEqualsNegativeDiffClass() {
    String testPath = "test-path";
    String testKey = "test-key";
    List<String> testPathToProperty = new ArrayList<String>( Arrays.asList( "A", "b", "C" ) );
    ProfileFieldProperty profileFieldProperty = new ProfileFieldProperty( testPath, testKey, testPathToProperty );
    assertFalse( profileFieldProperty.equals( new Object() ) );
  }

  @Test
  public void testEqualsNegativeNull() {
    String testPath = "test-path";
    String testKey = "test-key";
    List<String> testPathToProperty = new ArrayList<String>( Arrays.asList( "A", "b", "C" ) );
    ProfileFieldProperty profileFieldProperty = new ProfileFieldProperty( testPath, testKey, testPathToProperty );
    assertFalse( profileFieldProperty.equals( null ) );
  }

  @Test
  public void testEqualsNegativeDiffPath() {
    String testPath = "test-path";
    String testKey = "test-key";
    List<String> testPathToProperty = new ArrayList<String>( Arrays.asList( "A", "b", "C" ) );
    ProfileFieldProperty profileFieldProperty = new ProfileFieldProperty( testPath, testKey, testPathToProperty );
    assertFalse( profileFieldProperty.equals( new ProfileFieldProperty( "fake", testKey, testPathToProperty ) ) );
  }

  @Test
  public void testEqualsNegativeDiffKey() {
    String testPath = "test-path";
    String testKey = "test-key";
    List<String> testPathToProperty = new ArrayList<String>( Arrays.asList( "A", "b", "C" ) );
    ProfileFieldProperty profileFieldProperty = new ProfileFieldProperty( testPath, testKey, testPathToProperty );
    assertFalse( profileFieldProperty.equals( new ProfileFieldProperty( testPath, "fake", testPathToProperty ) ) );
  }

  @Test
  public void testEqualsNegativeDiffPathToProperty() {
    String testPath = "test-path";
    String testKey = "test-key";
    List<String> testPathToProperty = new ArrayList<String>( Arrays.asList( "A", "b", "C" ) );
    ProfileFieldProperty profileFieldProperty = new ProfileFieldProperty( testPath, testKey, testPathToProperty );
    assertFalse( profileFieldProperty.equals(
      new ProfileFieldProperty( testPath, testKey, new ArrayList<String>( Arrays.asList( "D", "e", "F" ) ) ) ) );
  }
}
