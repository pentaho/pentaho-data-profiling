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
import java.util.List;

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
  public void testSetPath() {
    String path = "TEST.PATH.ONE[].TWO";
    ProfilingField field = new ProfilingField();
    field.setPath( path );
    assertEquals( path, field.getPath() );
  }

  @Test
  public void testSetLeaf() {
    ProfilingField field = new ProfilingField();
    assertFalse( field.isLeaf() );
    field.setLeaf( true );
    assertTrue( field.isLeaf() );
  }

  @Test
  public void testSetSubDoc() {
    ProfilingField field = new ProfilingField();
    assertFalse( field.isDocument() );
    field.setDocument( true );
    assertTrue( field.isDocument() );
  }

  @Test
  public void testSetArray() {
    ProfilingField field = new ProfilingField();
    assertFalse( field.isArray() );
    field.setArray( true );
    assertTrue( field.isArray() );
  }

  @Test
  public void testPathToFieldsTopLevelTerminal() {
    String path = "A_SIMPLE_FIELD";

    List<ProfilingField> fields = ProfilingField.pathToFields( path );
    assertEquals( 1, fields.size() );
    assertEquals( path, fields.get( 0 ).getName() );
    assertEquals( "$" + path, fields.get( 0 ).getPath() );
    assertTrue( fields.get( 0 ).isLeaf() );
    assertFalse( fields.get( 0 ).isArray() );
    assertFalse( fields.get( 0 ).isDocument() );
  }

  @Test
  public void testPathToFieldsOneSubDoc() {
    String path = "SUB_DOC.TERMINAL";

    List<ProfilingField> fields = ProfilingField.pathToFields( path );
    assertEquals( 2, fields.size() );
    assertEquals( "SUB_DOC", fields.get( 0 ).getName() );
    assertEquals( "$SUB_DOC", fields.get( 0 ).getPath() );
    assertFalse( fields.get( 0 ).isLeaf() );
    assertTrue( fields.get( 0 ).isDocument() );
    assertFalse( fields.get( 0 ).isArray() );

    assertEquals( "TERMINAL", fields.get( 1 ).getName() );
    assertEquals( "$" + path, fields.get( 1 ).getPath() );
    assertTrue( fields.get( 1 ).isLeaf() );
    assertFalse( fields.get( 1 ).isArray() );
    assertFalse( fields.get( 1 ).isDocument() );
  }

  @Test
  public void testPathToFieldsSubDocWithSubDoc() {
    String path = "SUB_DOC.SUB_SUB_DOC.TERMINAL";

    List<ProfilingField> fields = ProfilingField.pathToFields( path );
    assertEquals( 3, fields.size() );
    assertEquals( "SUB_DOC", fields.get( 0 ).getName() );
    assertEquals( "$SUB_DOC", fields.get( 0 ).getPath() );
    assertFalse( fields.get( 0 ).isLeaf() );
    assertFalse( fields.get( 0 ).isArray() );
    assertTrue( fields.get( 0 ).isDocument() );

    assertEquals( "SUB_SUB_DOC", fields.get( 1 ).getName() );
    assertEquals( "$SUB_DOC.SUB_SUB_DOC", fields.get( 1 ).getPath() );
    assertFalse( fields.get( 1 ).isLeaf() );
    assertFalse( fields.get( 1 ).isArray() );
    assertTrue( fields.get( 1 ).isDocument() );

    assertEquals( "TERMINAL", fields.get( 2 ).getName() );
    assertEquals( "$" + path, fields.get( 2 ).getPath() );
    assertTrue( fields.get( 2 ).isLeaf() );
    assertFalse( fields.get( 2 ).isArray() );
    assertFalse( fields.get( 2 ).isDocument() );
  }

  @Test
  public void testPathToFieldsTerminalArray() {
    String path = "TERMINAL[]";

    List<ProfilingField> fields = ProfilingField.pathToFields( path );
    assertEquals( 1, fields.size() );
    assertEquals( "TERMINAL", fields.get( 0 ).getName() );
    assertEquals( "$" + path, fields.get( 0 ).getPath() );
    assertTrue( fields.get( 0 ).isLeaf() );
    assertTrue( fields.get( 0 ).isArray() );
    assertFalse( fields.get( 0 ).isDocument() );
  }

  @Test
  public void testPathToFieldsNonTerminalArray() {
    String path = "SUB_DOC.ARRAY[].TERMINAL";

    List<ProfilingField> fields = ProfilingField.pathToFields( path );
    assertEquals( 3, fields.size() );
    assertEquals( "SUB_DOC", fields.get( 0 ).getName() );
    assertEquals( "$SUB_DOC", fields.get( 0 ).getPath() );
    assertFalse( fields.get( 0 ).isLeaf() );
    assertFalse( fields.get( 0 ).isArray() );
    assertTrue( fields.get( 0 ).isDocument() );

    assertEquals( "ARRAY", fields.get( 1 ).getName() );
    assertEquals( "$SUB_DOC.ARRAY[]", fields.get( 1 ).getPath() );
    assertFalse( fields.get( 1 ).isLeaf() );
    assertFalse( fields.get( 1 ).isDocument() );
    assertTrue( fields.get( 1 ).isArray() );

    assertEquals( "TERMINAL", fields.get( 2 ).getName() );
    assertEquals( "$" + path, fields.get( 2 ).getPath() );
    assertTrue( fields.get( 2 ).isLeaf() );
    assertFalse( fields.get( 2 ).isArray() );
    assertFalse( fields.get( 2 ).isDocument() );
  }

  @Test
  public void testAddType() {
    String name = "NAME_VALUE";
    ProfilingField profilingField = new ProfilingField();
    profilingField.setName( name );

    assertFalse( profilingField.typesIterator().hasNext() );
    ProfilingFieldType type = new ProfilingFieldType( ProfilingFieldType.Type.NUMBER.toString() );
    type.setCount( 1 );
    profilingField.addOrUpdateType( type );
    assertTrue( profilingField.typesIterator().hasNext() );
    Iterator<ProfilingFieldType> i = profilingField.typesIterator();

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

    ProfilingFieldType type = new ProfilingFieldType( ProfilingFieldType.Type.NUMBER.toString() );
    type.setCount( 1 );
    profilingField.addOrUpdateType( type );
    profilingField.addOrUpdateType( type );
    assertTrue( profilingField.typesIterator().hasNext() );
    Iterator<ProfilingFieldType> i = profilingField.typesIterator();

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

    ProfilingFieldType type = new ProfilingFieldType( ProfilingFieldType.Type.NUMBER.toString() );
    type.setCount( 1 );
    profilingField.addOrUpdateType( type );

    ProfilingFieldType type2 = new ProfilingFieldType( ProfilingFieldType.Type.STRING.toString() );
    type2.setCount( 10 );
    profilingField.addOrUpdateType( type2 );

    assertTrue( profilingField.typesIterator().hasNext() );
    Iterator<ProfilingFieldType> i = profilingField.typesIterator();
    i.next();
    assertTrue( profilingField.typesIterator().hasNext() );

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

    ProfilingFieldType type = new ProfilingFieldType( ProfilingFieldType.Type.NUMBER.toString() );
    type.setCount( 1 );
    profilingField.addOrUpdateType( type );

    ProfilingFieldType type2 = new ProfilingFieldType( ProfilingFieldType.Type.STRING.toString() );
    type2.setCount( 10 );
    profilingField.addOrUpdateType( type2 );

    assertTrue( profilingField.getNamedType( ProfilingFieldType.Type.NUMBER.toString() ) != null );
    assertTrue( profilingField.getNamedType( ProfilingFieldType.Type.STRING.toString() ) != null );
    assertTrue( profilingField.getNamedType( "goofy" ) == null );

    assertEquals( 10L, profilingField.getNamedType( ProfilingFieldType.Type.STRING.toString() ).getCount() );
  }

  @Test
  public void testRemoveNamedType() {
    String name = "NAME_VALUE";
    ProfilingField profilingField = new ProfilingField();
    profilingField.setName( name );

    ProfilingFieldType type = new ProfilingFieldType( ProfilingFieldType.Type.NUMBER.toString() );
    type.setCount( 1 );
    profilingField.addOrUpdateType( type );

    ProfilingFieldType type2 = new ProfilingFieldType( ProfilingFieldType.Type.STRING.toString() );
    type2.setCount( 10 );
    profilingField.addOrUpdateType( type2 );

    assertTrue( profilingField.getNamedType( ProfilingFieldType.Type.NUMBER.toString() ) != null );
    assertTrue( profilingField.getNamedType( ProfilingFieldType.Type.STRING.toString() ) != null );

    profilingField.removeNamedType( ProfilingFieldType.Type.NUMBER.toString() );
    assertTrue( profilingField.getNamedType( ProfilingFieldType.Type.STRING.toString() ) != null );
    assertTrue( profilingField.getNamedType( ProfilingFieldType.Type.NUMBER.toString() ) == null );
  }
}
