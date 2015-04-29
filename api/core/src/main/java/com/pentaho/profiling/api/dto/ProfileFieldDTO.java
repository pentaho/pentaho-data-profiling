package com.pentaho.profiling.api.dto;

import com.pentaho.profiling.api.ProfileField;
import com.pentaho.profiling.api.ProfileFieldValueType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bryan on 4/30/15.
 */
public class ProfileFieldDTO implements ProfileField {
  private String physicalName;
  private String logicalName;
  private Map<String, String> properties;
  private Map<String, ProfileFieldValueType> profileFieldValueTypes;

  public ProfileFieldDTO() {

  }

  public ProfileFieldDTO( ProfileField profileField ) {
    this.physicalName = profileField.getPhysicalName();
    this.logicalName = profileField.getLogicalName();
    setProperties( profileField.getProperties() );
    setTypes( profileField.getTypes() );
  }

  @Override public String getPhysicalName() {
    return physicalName;
  }

  public void setPhysicalName( String physicalName ) {
    this.physicalName = physicalName;
  }

  @Override public String getLogicalName() {
    return logicalName;
  }

  public void setLogicalName( String logicalName ) {
    this.logicalName = logicalName;
  }

  @Override public Map<String, String> getProperties() {
    return properties;
  }

  public void setProperties( Map<String, String> properties ) {
    if ( properties == null ) {
      this.properties = new HashMap<String, String>();
    } else {
      this.properties = new HashMap<String, String>( properties );
    }
  }

  @Override public List<ProfileFieldValueType> getTypes() {
    return new ArrayList<ProfileFieldValueType>( profileFieldValueTypes.values() );
  }

  public void setTypes( List<ProfileFieldValueType> types ) {
    if ( types == null ) {
      this.profileFieldValueTypes = new HashMap<String, ProfileFieldValueType>();
    } else {
      Map<String, ProfileFieldValueType> newTypes = new HashMap<String, ProfileFieldValueType>( types.size() );
      for ( ProfileFieldValueType type : types ) {
        newTypes.put( type.getTypeName(), new ProfileFieldValueTypeDTO( type ) );
      }
      this.profileFieldValueTypes = newTypes;
    }

  }

  @Override public ProfileFieldValueType getType( String name ) {
    return profileFieldValueTypes.get( name );
  }

  @Override public Set<String> typeKeys() {
    return profileFieldValueTypes.keySet();
  }

  @Override public Object clone() {
    return new ProfileFieldDTO( this );
  }
}
