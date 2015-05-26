/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
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

package com.pentaho.profiling.api.dto;

import com.pentaho.profiling.api.ProfileField;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileState;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.ProfileStatusMessage;
import com.pentaho.profiling.api.action.ProfileActionExceptionWrapper;
import com.pentaho.profiling.api.configuration.ProfileConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bryan on 3/27/15.
 */
public class ProfileStatusDTO implements ProfileStatus {
  private ProfileState profileState;
  private String name;
  private String id;
  private ProfileConfiguration profileConfiguration;
  private List<ProfileField> fields;
  private Long totalEntities;
  private List<ProfileStatusMessage> statusMessages;
  private ProfileActionExceptionWrapper operationError;
  private List<ProfileFieldProperty> profileFieldProperties;
  private long sequenceNumber;

  public ProfileStatusDTO() {
    this( null, null, null, null, null, null, null, null, null, 0L );
  }

  public ProfileStatusDTO( ProfileStatus profileStatus ) {
    this( profileStatus.getProfileState(), profileStatus.getName(), profileStatus.getId(),
      profileStatus.getProfileConfiguration(), createFieldDtos( profileStatus.getFields() ),
      profileStatus.getTotalEntities(),
      profileStatus.getStatusMessages(), profileStatus.getOperationError(), profileStatus.getProfileFieldProperties(),
      profileStatus.getSequenceNumber() );
  }

  public ProfileStatusDTO( ProfileState profileState, String name, String id,
                           ProfileConfiguration profileConfiguration,
                           List<ProfileField> fields, Long totalEntities,
                           List<ProfileStatusMessage> statusMessages,
                           ProfileActionExceptionWrapper operationError,
                           List<ProfileFieldProperty> profileFieldProperties, long sequenceNumber ) {
    this.profileState = profileState;
    this.name = name;
    this.id = id;
    this.profileConfiguration = profileConfiguration;
    this.fields = fields;
    this.totalEntities = totalEntities;
    this.statusMessages = statusMessages;
    this.operationError = operationError;
    this.profileFieldProperties = profileFieldProperties;
    this.sequenceNumber = sequenceNumber;
  }

  private static List<ProfileField> createFieldDtos( List<ProfileField> profileFields ) {
    if ( profileFields == null ) {
      return null;
    }
    List<ProfileField> result = new ArrayList<ProfileField>( profileFields.size() );
    for ( ProfileField profileField : profileFields ) {
      result.add( new ProfileFieldDTO( profileField ) );
    }
    return result;
  }

  @Override public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  @Override public ProfileState getProfileState() {
    return profileState;
  }

  public void setProfileState( ProfileState profileState ) {
    this.profileState = profileState;
  }

  @Override public String getId() {
    return id;
  }

  public void setId( String id ) {
    this.id = id;
  }

  @Override public ProfileConfiguration getProfileConfiguration() {
    return profileConfiguration;
  }

  public void setProfileConfiguration(
    ProfileConfiguration profileConfiguration ) {
    this.profileConfiguration = profileConfiguration;
  }

  @Override public List<ProfileField> getFields() {
    return fields;
  }

  public void setFields( List<ProfileField> fields ) {
    this.fields = fields;
  }

  @Override public ProfileField getField( String physicalName ) {
    return null;
  }

  @Override public Long getTotalEntities() {
    return totalEntities;
  }

  public void setTotalEntities( Long totalEntities ) {
    this.totalEntities = totalEntities;
  }

  @Override public List<ProfileStatusMessage> getStatusMessages() {
    return statusMessages;
  }

  public void setStatusMessages( List<ProfileStatusMessage> statusMessages ) {
    this.statusMessages = statusMessages;
  }

  @Override public ProfileActionExceptionWrapper getOperationError() {
    return operationError;
  }

  public void setOperationError( ProfileActionExceptionWrapper operationError ) {
    this.operationError = operationError;
  }

  @Override public List<ProfileFieldProperty> getProfileFieldProperties() {
    return profileFieldProperties;
  }

  public void setProfileFieldProperties( List<ProfileFieldProperty> profileFieldProperties ) {
    this.profileFieldProperties = profileFieldProperties;
  }

  @Override public long getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber( long sequenceNumber ) {
    this.sequenceNumber = sequenceNumber;
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

    ProfileStatusDTO that = (ProfileStatusDTO) o;

    if ( sequenceNumber != that.sequenceNumber ) {
      return false;
    }
    if ( profileState != that.profileState ) {
      return false;
    }
    if ( name != null ? !name.equals( that.name ) : that.name != null ) {
      return false;
    }
    if ( id != null ? !id.equals( that.id ) : that.id != null ) {
      return false;
    }
    if ( profileConfiguration != null ? !profileConfiguration.equals( that.profileConfiguration ) :
      that.profileConfiguration != null ) {
      return false;
    }
    if ( fields != null ? !fields.equals( that.fields ) : that.fields != null ) {
      return false;
    }
    if ( totalEntities != null ? !totalEntities.equals( that.totalEntities ) : that.totalEntities != null ) {
      return false;
    }
    if ( statusMessages != null ? !statusMessages.equals( that.statusMessages ) : that.statusMessages != null ) {
      return false;
    }
    if ( operationError != null ? !operationError.equals( that.operationError ) : that.operationError != null ) {
      return false;
    }
    return !( profileFieldProperties != null ? !profileFieldProperties.equals( that.profileFieldProperties ) :
      that.profileFieldProperties != null );

  }

  @Override public int hashCode() {
    int result = profileState != null ? profileState.hashCode() : 0;
    result = 31 * result + ( name != null ? name.hashCode() : 0 );
    result = 31 * result + ( id != null ? id.hashCode() : 0 );
    result = 31 * result + ( profileConfiguration != null ? profileConfiguration.hashCode() : 0 );
    result = 31 * result + ( fields != null ? fields.hashCode() : 0 );
    result = 31 * result + ( totalEntities != null ? totalEntities.hashCode() : 0 );
    result = 31 * result + ( statusMessages != null ? statusMessages.hashCode() : 0 );
    result = 31 * result + ( operationError != null ? operationError.hashCode() : 0 );
    result = 31 * result + ( profileFieldProperties != null ? profileFieldProperties.hashCode() : 0 );
    result = 31 * result + (int) ( sequenceNumber ^ ( sequenceNumber >>> 32 ) );
    return result;
  }

  @Override public String toString() {
    return "ProfileStatusDTO{" +
      "profileState=" + profileState +
      ", name='" + name + '\'' +
      ", id='" + id + '\'' +
      ", profileConfiguration=" + profileConfiguration +
      ", fields=" + fields +
      ", totalEntities=" + totalEntities +
      ", statusMessages=" + statusMessages +
      ", operationError=" + operationError +
      ", profileFieldProperties=" + profileFieldProperties +
      ", sequenceNumber=" + sequenceNumber +
      '}';
  }
  //CHECKSTYLE:OperatorWrap:ON


  @Override public Object clone() throws CloneNotSupportedException {
    return new ProfileStatusDTO( this );
  }
}
