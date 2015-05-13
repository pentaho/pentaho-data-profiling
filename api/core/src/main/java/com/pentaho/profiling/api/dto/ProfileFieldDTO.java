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
