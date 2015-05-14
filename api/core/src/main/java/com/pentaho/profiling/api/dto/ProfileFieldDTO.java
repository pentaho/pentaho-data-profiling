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

package com.pentaho.profiling.api.dto;

import com.pentaho.profiling.api.ProfileField;
import com.pentaho.profiling.api.ProfileFieldValueType;

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
  private List<ProfileFieldValueType> types;
  private Map<String, ProfileFieldValueType> profileFieldValueTypeMap;

  public ProfileFieldDTO() {

  }

  public ProfileFieldDTO( ProfileField profileField ) {
    this.physicalName = profileField.getPhysicalName();
    this.logicalName = profileField.getLogicalName();
    this.properties = profileField.getProperties();
    this.types = profileField.getTypes();
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
    this.properties = properties;
  }

  @Override public List<ProfileFieldValueType> getTypes() {
    return types;
  }

  public void setTypes( List<ProfileFieldValueType> types ) {
    this.types = types;
    profileFieldValueTypeMap = null;
  }

  @Override public ProfileFieldValueType getType( String name ) {
    if ( types == null ) {
      return null;
    }
    if ( profileFieldValueTypeMap == null ) {
      profileFieldValueTypeMap = new HashMap<String, ProfileFieldValueType>();
      for ( ProfileFieldValueType profileFieldValueType : types ) {
        profileFieldValueTypeMap.put( profileFieldValueType.getTypeName(), profileFieldValueType );
      }
    }
    return profileFieldValueTypeMap.get( name );
  }

  @Override public Set<String> typeKeys() {
    return profileFieldValueTypeMap.keySet();
  }

  @Override public Object clone() {
    return new ProfileFieldDTO( this );
  }

  //OperatorWrap isn't helpful for autogenerated methods
  //CHECKSTYLE:OperatorWrap:OFF
  @Override public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    ProfileFieldDTO that = (ProfileFieldDTO) o;

    if ( physicalName != null ? !physicalName.equals( that.physicalName ) : that.physicalName != null ) {
      return false;
    }
    if ( logicalName != null ? !logicalName.equals( that.logicalName ) : that.logicalName != null ) {
      return false;
    }
    if ( properties != null ? !properties.equals( that.properties ) : that.properties != null ) {
      return false;
    }
    return !( types != null ? !types.equals( that.types ) :
      that.types != null );

  }

  @Override public int hashCode() {
    int result = physicalName != null ? physicalName.hashCode() : 0;
    result = 31 * result + ( logicalName != null ? logicalName.hashCode() : 0 );
    result = 31 * result + ( properties != null ? properties.hashCode() : 0 );
    result = 31 * result + ( types != null ? types.hashCode() : 0 );
    return result;
  }

  @Override public String toString() {
    return "ProfileFieldDTO{" +
      "physicalName='" + physicalName + '\'' +
      ", logicalName='" + logicalName + '\'' +
      ", properties=" + properties +
      ", types=" + types +
      '}';
  }
  //CHECKSTYLE:OperatorWrap:ON
}
