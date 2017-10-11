/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.profiling.model;

import org.pentaho.profiling.api.MutableProfileField;
import org.pentaho.profiling.api.MutableProfileStatus;
import org.pentaho.profiling.api.ProfileField;
import org.pentaho.profiling.api.ProfileFieldProperty;
import org.pentaho.profiling.api.ProfileState;
import org.pentaho.profiling.api.ProfileStatus;
import org.pentaho.profiling.api.ProfileStatusMessage;
import org.pentaho.profiling.api.action.ProfileActionExceptionWrapper;

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
