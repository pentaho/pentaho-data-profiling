/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2015 Pentaho Corporation (Pentaho). All rights reserved.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 8/14/14.
 */
public class ProfilingFieldTest {
  @Test
  public void testConstructor() {
    ProfilingField p = new ProfilingField();
    assertTrue( p.getValues() != null );

    Map<String, Object> vals = new HashMap<String, Object>();
    vals.put( "akey", "avalue" );
    p = new ProfilingField( vals );

    assertTrue( p.getValues() != null );
    assertEquals( vals.size(), p.getValues().size() );
    assertEquals( "avalue", p.getValues().get( "akey" ).toString() );
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testMapConstructor() {
    Map<String, Object> values = new HashMap<String, Object>();
    values.put( "test", 2 );
    ProfilingField profilingField = new ProfilingField( values );
    assertEquals( values, profilingField.getValues() );
  }

  @Test
  @SuppressWarnings( "unchecked" )
  public void testCopyConstructor() {
    String nestedMapKey = "nestedMap";
    String nestedListKey = "nestedKey";
    PublicCloneable publicCloneable = mock( PublicCloneable.class );
    PublicCloneable publicCloneable2 = mock( PublicCloneable.class );
    when( publicCloneable.clone() ).thenReturn( publicCloneable2 );
    when( publicCloneable2.clone() ).thenReturn( publicCloneable2 );

    Map<String, Object> topMap = new HashMap<String, Object>();
    Map<String, Object> nestedMap = new HashMap<String, Object>();
    List<Object> nestedList = new ArrayList<Object>();
    nestedList.add( 1L );
    nestedList.add( null );
    nestedList.add( publicCloneable2 );
    topMap.put( nestedMapKey, nestedMap );
    nestedMap.put( nestedListKey, nestedList );
    ProfilingField profilingField = new ProfilingField( topMap );
    Map<String, Object> deepCopy = new ProfilingField( profilingField.getValues() ).getValues();

    assertFalse( deepCopy == topMap );
    assertEquals( 1, deepCopy.size() );
    assertTrue( deepCopy.containsKey( nestedMapKey ) );
    Map<String, Object> nestedMapCopy = (Map<String, Object>) deepCopy.get( nestedMapKey );

    assertFalse( nestedMapCopy == nestedMap );
    assertEquals( 1, nestedMapCopy.size() );
    assertTrue( nestedMapCopy.containsKey( nestedListKey ) );
    List<Object> nestedListCopy = (List<Object>) nestedMapCopy.get( nestedListKey );

    assertEquals( 3, nestedListCopy.size() );
    assertEquals( 1L, nestedListCopy.get( 0 ) );
    assertNull( nestedListCopy.get( 1 ) );
    assertEquals( publicCloneable2, nestedListCopy.get( 2 ) );
  }

  @Test( expected = RuntimeException.class )
  public void testCopyError() {
    String nestedListKey = "nestedKey";
    Map<String, Object> topMap = new HashMap<String, Object>();
    topMap.put( nestedListKey, Collections.unmodifiableList( new ArrayList<Object>(  ) ) );
    new ProfilingField( topMap );
  }
}
