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
