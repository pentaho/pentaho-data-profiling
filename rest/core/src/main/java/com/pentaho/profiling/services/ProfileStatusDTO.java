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

package com.pentaho.profiling.services;

import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileState;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.ProfileStatusMessage;
import com.pentaho.profiling.api.ProfilingField;
import com.pentaho.profiling.api.action.ProfileActionExceptionWrapper;
import com.pentaho.profiling.api.configuration.ProfileConfiguration;

import java.util.List;

/**
 * Created by bryan on 3/27/15.
 */
public class ProfileStatusDTO implements ProfileStatus {
  private ProfileState profileState;
  private String name;
  private String id;
  private ProfileConfiguration profileConfiguration;
  private List<ProfilingField> fields;
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
      profileStatus.getProfileConfiguration(), profileStatus.getFields(), profileStatus.getTotalEntities(),
      profileStatus.getStatusMessages(), profileStatus.getOperationError(), profileStatus.getProfileFieldProperties(),
      profileStatus.getSequenceNumber() );
  }

  public ProfileStatusDTO( ProfileState profileState, String name, String id,
                           ProfileConfiguration profileConfiguration,
                           List<ProfilingField> fields, Long totalEntities,
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

  @Override public List<ProfilingField> getFields() {
    return fields;
  }

  public void setFields( List<ProfilingField> fields ) {
    this.fields = fields;
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
}
