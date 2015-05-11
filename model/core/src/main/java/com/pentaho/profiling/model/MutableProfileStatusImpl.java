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
import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.ProfileField;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileState;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.ProfileStatusMessage;
import com.pentaho.profiling.api.action.ProfileActionExceptionWrapper;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by bryan on 7/31/14.
 */
public class MutableProfileStatusImpl extends ProfileStatusImpl implements MutableProfileStatus {
  public MutableProfileStatusImpl( ProfileStatus profileStatus ) {
    super( profileStatus );
    Map<String, ProfileField> profileFieldMap = new TreeMap<String, ProfileField>();
    for ( Map.Entry<String, ProfileField> fieldEntry : fields.entrySet() ) {
      profileFieldMap.put( fieldEntry.getKey(), new MutableProfileFieldImpl( fieldEntry.getValue() ) );
    }
    fields = profileFieldMap;
  }

  @Override public void setProfileState( ProfileState profileState ) {
    this.state = profileState;
  }

  @Override public MutableProfileField getOrCreateField( String physicalName, String logicalName ) {
    ProfileField profileField = fields.get( physicalName );
    MutableProfileField result;
    if ( profileField == null ) {
      result = new MutableProfileFieldImpl( physicalName, logicalName );
      fields.put( physicalName, result );
    } else {
      result = (MutableProfileField) profileField;
    }
    return result;
  }

  @Override public Map<String, MutableProfileField> getMutableFieldMap() {
    return (Map) fields;
  }

  @Override public void addField( ProfileField field ) {
    if ( fields.containsKey( field.getPhysicalName() ) ) {
      // TODO... Log?
    }
    fields.put( field.getPhysicalName(), new MutableProfileFieldImpl( field ) );
  }

  @Override public void setField( ProfileField field ) {
    fields.put( field.getPhysicalName(), new MutableProfileFieldImpl( field ) );
  }

  @Override public void setName( String name ) {
    this.name = name;
  }

  @Override public void setTotalEntities( Long totalEntities ) {
    this.totalEntities = totalEntities;
  }

  @Override public void setStatusMessages( List<ProfileStatusMessage> statusMessages ) {
    this.statusMessages = statusMessages;
  }

  @Override public void setOperationError( ProfileActionExceptionWrapper operationError ) {
    this.operationError = operationError;
  }

  @Override public void setProfileFieldProperties( List<ProfileFieldProperty> profileFieldProperties ) {
    this.profileFieldProperties = profileFieldProperties;
  }
}
