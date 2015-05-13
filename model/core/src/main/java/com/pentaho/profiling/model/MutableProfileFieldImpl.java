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

import com.pentaho.profiling.api.MutableProfileField;
import com.pentaho.profiling.api.MutableProfileFieldValueType;
import com.pentaho.profiling.api.ProfileField;
import com.pentaho.profiling.api.ProfileFieldValueType;

import java.util.Map;

/**
 * Created by bryan on 4/30/15.
 */
public class MutableProfileFieldImpl extends ProfileFieldImpl implements MutableProfileField {
  public MutableProfileFieldImpl( String physicalName, String logicalName ) {
    super( physicalName, logicalName );
  }

  public MutableProfileFieldImpl( ProfileField profileField ) {
    super( profileField );
  }

  @Override public MutableProfileFieldValueType getValueTypeMetrics( String name ) {
    ProfileFieldValueType profileFieldValueType = types.get( name );
    MutableProfileFieldValueType result;
    if ( profileFieldValueType == null ) {
      return null;
    }
    if ( profileFieldValueType instanceof MutableProfileFieldValueType ) {
      return (MutableProfileFieldValueType) profileFieldValueType;
    }
    result = new MutableProfileFieldValueTypeImpl( profileFieldValueType );
    types.put( name, result );
    return result;
  }

  @Override public void putValueTypeMetrics( String name, ProfileFieldValueType profileFieldValueType ) {
    types.put( name, new MutableProfileFieldValueTypeImpl( profileFieldValueType ) );
  }

  @Override public MutableProfileFieldValueType getOrCreateValueTypeMetrics( String name ) {
    ProfileFieldValueType profileFieldValueType = types.get( name );
    MutableProfileFieldValueType result;
    if ( profileFieldValueType == null ) {
      result = new MutableProfileFieldValueTypeImpl();
      result.setTypeName( name );
    } else {
      if ( profileFieldValueType instanceof MutableProfileFieldValueType ) {
        return (MutableProfileFieldValueType) profileFieldValueType;
      } else {
        result = new MutableProfileFieldValueTypeImpl( profileFieldValueType );
      }
    }
    types.put( name, result );
    return result;
  }

  @Override public Map<String, String> getProperties() {
    return properties;
  }
}
