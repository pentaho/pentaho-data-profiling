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

import org.pentaho.profiling.api.ProfileField;
import org.pentaho.profiling.api.ProfileFieldProperty;
import org.pentaho.profiling.api.ProfileState;
import org.pentaho.profiling.api.ProfileStatus;
import org.pentaho.profiling.api.ProfileStatusMessage;
import org.pentaho.profiling.api.action.ProfileActionExceptionWrapper;
import org.pentaho.profiling.api.configuration.ProfileConfiguration;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bryan on 9/29/14.
 */
public class ProfileStatusImpl implements ProfileStatus {
  protected ProfileState state;
  protected String name;
  protected Map<String, ProfileField> fields;
  protected Long totalEntities;
  protected List<ProfileStatusMessage> statusMessages;
  protected ProfileActionExceptionWrapper operationError;
  protected List<ProfileFieldProperty> profileFieldProperties;
  protected String id;
  protected ProfileConfiguration profileConfiguration;
  protected long sequenceNumber;

  public ProfileStatusImpl( String id, String name, ProfileConfiguration profileConfiguration ) {
    this( id, name, profileConfiguration, 0L );
  }

  public ProfileStatusImpl( String id, String name, ProfileConfiguration profileConfiguration, long sequenceNumber ) {
    this( ProfileState.ACTIVE, null, null, null, null, null, id, profileConfiguration, sequenceNumber, name );
  }
  public ProfileStatusImpl( ProfileStatus profileStatus ) {
    this( profileStatus.getProfileState(), profileStatus.getFields(), profileStatus.getTotalEntities(),
      profileStatus.getStatusMessages(),
      profileStatus.getOperationError(), profileStatus.getProfileFieldProperties(), profileStatus.getId(),
      profileStatus.getProfileConfiguration(), profileStatus.getSequenceNumber() + 1, profileStatus.getName() );
  }

  public ProfileStatusImpl( ProfileState profileState, List<ProfileField> fields, Long totalEntities,
                            List<ProfileStatusMessage> statusMessages,
                            ProfileActionExceptionWrapper operationError,
                            List<ProfileFieldProperty> profileFieldProperties, String id,
                            ProfileConfiguration profileConfiguration, long sequenceNumber, String name ) {
    this.state = profileState;
    if ( fields == null ) {
      fields = new ArrayList<ProfileField>();
    }
    this.fields = new HashMap<String, ProfileField>();
    for ( ProfileField field : fields ) {
      this.fields.put( field.getPhysicalName(), (ProfileField) field.clone() );
    }
    this.totalEntities = totalEntities;
    this.statusMessages = statusMessages;
    this.operationError = operationError;
    if ( profileFieldProperties == null ) {
      profileFieldProperties = new ArrayList<ProfileFieldProperty>();
    }
    this.profileFieldProperties =
      Collections.unmodifiableList( new ArrayList<ProfileFieldProperty>( profileFieldProperties ) );
    this.id = id;
    this.profileConfiguration = profileConfiguration;
    this.sequenceNumber = sequenceNumber;
    this.name = name;
  }

  @Override @XmlElement
  public String getId() {
    return id;
  }

  @Override public String getName() {
    return this.name;
  }

  @Override @XmlElement
  public ProfileConfiguration getProfileConfiguration() {
    return profileConfiguration;
  }

  @Override @XmlElement
  public List<ProfileField> getFields() {
    return new ArrayList<ProfileField>( fields.values() );
  }

  @Override public ProfileField getField( String physicalName ) {
    return fields.get( physicalName );
  }

  @Override @XmlElement
  public Long getTotalEntities() {
    return totalEntities;
  }

  @Override @XmlElement
  public List<ProfileStatusMessage> getStatusMessages() {
    return statusMessages;
  }

  @Override @XmlElement
  public ProfileActionExceptionWrapper getOperationError() {
    return operationError;
  }

  @Override @XmlElement
  public List<ProfileFieldProperty> getProfileFieldProperties() {
    return profileFieldProperties;
  }

  @XmlElement
  @Override public long getSequenceNumber() {
    return sequenceNumber;
  }

  @XmlElement
  @Override public ProfileState getProfileState() {
    return state;
  }
}
