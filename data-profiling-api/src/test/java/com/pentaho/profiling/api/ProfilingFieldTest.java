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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

/**
 * Created by bryan on 8/14/14.
 */
public class ProfilingFieldTest {
  @Test
  public void testSetName() {
    String name = "NAME_VALUE";
    ProfilingField profilingField = new ProfilingField();
    profilingField.setName( name );
    assertEquals( name, profilingField.getName() );
  }

  @Test
  public void testID() {
    String name = "NAME_VALUE";
    ProfilingField profilingField = new ProfilingField();
    profilingField.setName( name );
    assertEquals( name.hashCode(), profilingField.getID() );
  }

  @Test
  public void testAddType() {
    String name = "NAME_VALUE";
    ProfilingField profilingField = new ProfilingField();
    profilingField.setName( name );

    assertFalse( profilingField.iterator().hasNext() );
    ProfilingFieldType type = new ProfilingFieldType( "numeric" );
    type.setCount( 1 );
    profilingField.addOrUpdateType( type );
    assertTrue( profilingField.iterator().hasNext() );
    Iterator<ProfilingFieldType> i = profilingField.iterator();

    ProfilingFieldType retrieved = i.next();
    assertTrue( retrieved != null );
    assertEquals( type.getTypeName(), retrieved.getTypeName() );
    assertEquals( type.getCount(), retrieved.getCount() );
  }

  @Test
  public void testUpdateType() {
    String name = "NAME_VALUE";
    ProfilingField profilingField = new ProfilingField();
    profilingField.setName( name );

    ProfilingFieldType type = new ProfilingFieldType( "numeric" );
    type.setCount( 1 );
    profilingField.addOrUpdateType( type );
    profilingField.addOrUpdateType( type );
    assertTrue( profilingField.iterator().hasNext() );
    Iterator<ProfilingFieldType> i = profilingField.iterator();

    ProfilingFieldType retrieved = i.next();
    assertTrue( retrieved != null );
    assertEquals( type.getTypeName(), retrieved.getTypeName() );
    assertEquals( type.getCount() + type.getCount(), retrieved.getCount() );
  }

  @Test
  public void testAddUpdateTwoTypes() {
    String name = "NAME_VALUE";
    ProfilingField profilingField = new ProfilingField();
    profilingField.setName( name );

    ProfilingFieldType type = new ProfilingFieldType( "numeric" );
    type.setCount( 1 );
    profilingField.addOrUpdateType( type );

    ProfilingFieldType type2 = new ProfilingFieldType( "string" );
    type2.setCount( 10 );
    profilingField.addOrUpdateType( type2 );

    assertTrue( profilingField.iterator().hasNext() );
    Iterator<ProfilingFieldType> i = profilingField.iterator();
    i.next();
    assertTrue( profilingField.iterator().hasNext() );

    ProfilingFieldType retrieved = i.next();
    assertTrue( retrieved != null );
    assertEquals( type2.getTypeName(), retrieved.getTypeName() );
    assertEquals( type2.getCount(), retrieved.getCount() );
  }

  @Test
  public void testGetNamedType() {
    String name = "NAME_VALUE";
    ProfilingField profilingField = new ProfilingField();
    profilingField.setName( name );

    ProfilingFieldType type = new ProfilingFieldType( "numeric" );
    type.setCount( 1 );
    profilingField.addOrUpdateType( type );

    ProfilingFieldType type2 = new ProfilingFieldType( "string" );
    type2.setCount( 10 );
    profilingField.addOrUpdateType( type2 );

    assertTrue( profilingField.getNamedType( "numeric" ) != null );
    assertTrue( profilingField.getNamedType( "string" ) != null );
    assertTrue( profilingField.getNamedType( "goofy" ) == null );

    assertEquals( 10L, profilingField.getNamedType( "string" ).getCount() );
  }

  @Test
  public void testRemoveNamedType() {
    String name = "NAME_VALUE";
    ProfilingField profilingField = new ProfilingField();
    profilingField.setName( name );

    ProfilingFieldType type = new ProfilingFieldType( "numeric" );
    type.setCount( 1 );
    profilingField.addOrUpdateType( type );

    ProfilingFieldType type2 = new ProfilingFieldType( "string" );
    type2.setCount( 10 );
    profilingField.addOrUpdateType( type2 );

    assertTrue( profilingField.getNamedType( "numeric" ) != null );
    assertTrue( profilingField.getNamedType( "string" ) != null );

    profilingField.removeNamedType( "numeric" );
    assertTrue( profilingField.getNamedType( "string" ) != null );
    assertTrue( profilingField.getNamedType( "numeric" ) == null );
  }
}
