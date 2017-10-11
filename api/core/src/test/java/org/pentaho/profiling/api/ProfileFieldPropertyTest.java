/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.profiling.api;

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

  @Test
  public void testToString() {
    String testPath = "test-path";
    String testKey = "test-key";
    List<String> testPathToProperty = new ArrayList<String>( Arrays.asList( "A", "b", "C" ) );
    ProfileFieldProperty profileFieldProperty = new ProfileFieldProperty( testPath, testKey, testPathToProperty );
    assertTrue( profileFieldProperty.toString().startsWith( ProfileFieldProperty.class.getSimpleName() ) );
    assertTrue( profileFieldProperty.toString().contains( testKey ) );
    assertTrue( profileFieldProperty.toString().contains( testPath ) );
    assertTrue( profileFieldProperty.toString().contains( testPathToProperty.toString() ) );
  }
}
