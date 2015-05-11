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
