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

import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileState;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.ProfileStatusMessage;
import com.pentaho.profiling.api.ProfilingField;
import com.pentaho.profiling.api.action.ProfileActionExceptionWrapper;
import com.pentaho.profiling.api.configuration.ProfileConfiguration;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bryan on 9/29/14.
 */
public class ProfileStatusImpl implements ProfileStatus {
  protected ProfileState state;
  protected String name;
  protected List<ProfilingField> fields;
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

  public ProfileStatusImpl( ProfileState profileState, List<ProfilingField> fields, Long totalEntities,
                            List<ProfileStatusMessage> statusMessages,
                            ProfileActionExceptionWrapper operationError,
                            List<ProfileFieldProperty> profileFieldProperties, String id,
                            ProfileConfiguration profileConfiguration, long sequenceNumber, String name ) {
    this.state = profileState;
    if ( fields == null ) {
      fields = new ArrayList<ProfilingField>();
    }
    this.fields = Collections.unmodifiableList( new ArrayList<ProfilingField>( fields ) );
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
  public List<ProfilingField> getFields() {
    return fields;
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
