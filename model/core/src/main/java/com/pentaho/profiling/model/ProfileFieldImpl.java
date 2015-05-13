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

package com.pentaho.profiling.model;

import com.pentaho.profiling.api.ProfileField;
import com.pentaho.profiling.api.ProfileFieldValueType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.pentaho.profiling.api.util.PublicCloneableUtil.copyMap;

/**
 * Created by bryan on 4/30/15.
 */
public class ProfileFieldImpl implements ProfileField {
  protected Map<String, ProfileFieldValueType> types;
  protected Map<String, String> properties;
  private String physicalName;
  private String logicalName;

  public ProfileFieldImpl( String physicalName, String logicalName ) {
    this( physicalName, logicalName, new HashMap<String, String>(), new HashMap<String, ProfileFieldValueType>() );
  }

  public ProfileFieldImpl( ProfileField profileField ) {
    this( profileField.getPhysicalName(), profileField.getLogicalName(), profileField.getProperties(),
      mapFromList( profileField.getTypes() ) );
  }

  public ProfileFieldImpl( String physicalName, String logicalName, Map<String, String> properties,
                           Map<String, ProfileFieldValueType> types ) {
    this.physicalName = physicalName;
    this.logicalName = logicalName;
    this.properties = new HashMap<String, String>( properties );
    this.types = new HashMap<String, ProfileFieldValueType>();
    copyMap( types, this.types );
  }

  private static Map<String, ProfileFieldValueType> mapFromList( List<ProfileFieldValueType> profileFieldValueTypes ) {
    if ( profileFieldValueTypes == null ) {
      return null;
    }
    TreeMap<String, ProfileFieldValueType> treeMap = new TreeMap<String, ProfileFieldValueType>();
    for ( ProfileFieldValueType profileFieldValueType : profileFieldValueTypes ) {
      treeMap.put( profileFieldValueType.getTypeName(), profileFieldValueType );
    }
    return treeMap;
  }

  @Override public String getPhysicalName() {
    return physicalName;
  }

  @Override public String getLogicalName() {
    return logicalName;
  }

  @Override public List<ProfileFieldValueType> getTypes() {
    return new ArrayList<ProfileFieldValueType>( types.values() );
  }

  @Override public Map<String, String> getProperties() {
    return Collections.unmodifiableMap( properties );
  }

  @Override public ProfileFieldValueType getType( String name ) {
    return types.get( name );
  }

  @Override public Set<String> typeKeys() {
    return new HashSet<String>( types.keySet() );
  }

  public Object clone() {
    return new ProfileFieldImpl( this );
  }
}
