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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by bryan on 7/31/14.
 * 
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 */
public class ProfilingField {

  public static final String ARRAY = "ARRAY"; //$NON-NLS-1$
  public static final String DOCUMENT = "DOCUMENT"; //$NON-NLS-1$

  /** The name of the field */
  private String name;

  /** The full path of the field (including the terminal name part) */
  protected String path;

  /** True if this field is a leaf */
  protected boolean leaf;

  /** True if this field is a document */
  protected boolean document;

  /** True if this field is an array */
  protected boolean array;

  /** The known types for this field (and potentially their counts/stats) */
  protected Map<String, ProfilingFieldType> types = new TreeMap<String, ProfilingFieldType>();

  /**
   * Get the name of this field
   * 
   * @return the name of this field
   */
  public String getName() {
    return name;
  }

  /**
   * Set the name of this field
   * 
   * @param name
   *          the name of this field
   */
  public void setName( String name ) {
    this.name = name;
    setPath( name );
  }

  /**
   * Get the ID of this field (hashcode of name)
   * 
   * @return the ID of this field
   */
  public int getID() {
    return hashCode();
  }

  /**
   * Set the full path for this field
   * 
   * @param path
   *          the path to set
   */
  public void setPath( String path ) {
    this.path = path;
  }

  /**
   * Get the full path for this field
   * 
   * @return
   */
  public String getPath() {
    return this.path;
  }

  /**
   * Set whether this field is a leaf or not
   * 
   * @param leaf
   *          true if this field is a leaf in the document structure
   */
  public void setLeaf( boolean leaf ) {
    this.leaf = leaf;
  }

  /**
   * Get whether this field is a leaf in the document structure
   * 
   * @return true if this field is a leaf in the document structure
   */
  public boolean isLeaf() {
    return this.leaf;
  }

  /**
   * Set whether this field is a document or not
   * 
   * @param subdoc
   *          true if this field is a subdocument
   */
  public void setDocument( boolean subdoc ) {
    this.document = subdoc;
  }

  /**
   * Get whether this field is a document or not
   * 
   * @return true if this field is a subdocument
   */
  public boolean isDocument() {
    return this.document;
  }

  /**
   * Set whether this field is an array or not
   * 
   * @param array
   *          true if this field is an array
   */
  public void setArray( boolean array ) {
    this.array = array;
  }

  /**
   * Get whether this field is an array or not
   * 
   * @return true if this field is an array
   */
  public boolean isArray() {
    return this.array;
  }

  @Override
  public int hashCode() {
    int code = super.hashCode();

    if ( this.path != null ) {
      // differentiate a leaf and document with the same name
      String tmp = this.path + ( isDocument() ? "<doc>" : "" ); //$NON-NLS-1$ //$NON-NLS-2$
      code = tmp.hashCode();
    } else if ( name != null ) {
      code = name.hashCode();
    }

    return code;
  }

  @Override
  public boolean equals( Object other ) {
    if ( !( other instanceof ProfilingField ) ) {
      return false;
    }
    ProfilingField o = (ProfilingField) other;
    return getName().equals( o.getName() ) && getPath().equals( o.getPath() ) && isLeaf() == o.isLeaf()
        && isDocument() == o.isDocument() && isArray() == o.isArray();
  }

  /**
   * Add a new type or update the count of an existing type for this field
   * 
   * @param type
   *          the type to add/update
   */
  public synchronized void addOrUpdateType( ProfilingFieldType type ) {
    ProfilingFieldType existing = types.get( type.getTypeName() );
    if ( existing == null ) {
      existing = new ProfilingFieldType( type.getTypeName() );
      types.put( existing.getTypeName(), existing );
    }

    existing.setCount( existing.getCount() + type.getCount() );
  }

  /**
   * Remove the named type from the list of types for this field
   * 
   * @param typeName
   *          the name of the type to remove
   */
  public synchronized void removeNamedType( String typeName ) {
    types.remove( typeName );
  }

  /**
   * Get an immutable iterator over the types for this field
   * 
   * @return an iterator over the types for this field
   */
  public Iterator<ProfilingFieldType> typesIterator() {
    List<ProfilingFieldType> l = new ArrayList<ProfilingFieldType>( types.values() );

    return Collections.unmodifiableList( l ).iterator();
  }

  /**
   * Get the type info for the named type (or null if we have not seen examples of the named type for this field).
   * 
   * @param typeName
   *          the name of the type to get info on
   * @return
   */
  public ProfilingFieldType getNamedType( String typeName ) {
    return types.get( typeName );
  }

  /**
   * Split a path into sub-paths and return a list of ProfilingField objects encapsulating sub-paths. Note that the path
   * supplied is expected to terminate in a primitive leaf field.
   * 
   * @param path
   *          the full path to a leaf in a document
   * @return a list of sub-paths
   */
  public static List<ProfilingField> pathToFields( String path ) {
    List<ProfilingField> fields = new ArrayList<ProfilingField>();

    String[] temp = path.split( "\\." ); //$NON-NLS-1$
    StringBuilder currentPath = new StringBuilder();
    // root indicator - makes each path ready for use in MongoDbInput
    currentPath.append( "$" ); //$NON-NLS-1$

    for ( int i = 0; i < temp.length; i++ ) {
      String field = temp[i].trim();
      if ( field.length() > 0 ) {
        boolean isLeaf = ( i == temp.length - 1 );
        boolean isArray = field.endsWith( "[]" ); //$NON-NLS-1$
        boolean isDoc = !isLeaf && !isArray;
        currentPath.append( i == 0 ? field : "." + field ); //$NON-NLS-1$
        ProfilingField newField = new ProfilingField();
        newField.setName( isArray ? field.substring( 0, field.lastIndexOf( "[" ) ) //$NON-NLS-1$
            : field );
        newField.setPath( currentPath.toString() );
        newField.setLeaf( isLeaf );
        newField.setArray( isArray );
        newField.setDocument( isDoc );
        fields.add( newField );

      }
    }

    return fields;
  }
}
