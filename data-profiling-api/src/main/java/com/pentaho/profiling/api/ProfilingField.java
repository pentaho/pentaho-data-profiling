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
 */
public class ProfilingField {

  /** The name of the field */
  private String name;

  /** The known types for this field (and potentially their counts) */
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
  }

  /**
   * Get the ID of this field (hashcode of name)
   * 
   * @return the ID of this field
   */
  public int getID() {
    return hashCode();
  }

  @Override
  public int hashCode() {
    int code = super.hashCode();
    if ( name != null ) {
      code = name.hashCode();
    }

    return code;
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
   * @return
   */
  public Iterator<ProfilingFieldType> iterator() {
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
}
