/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
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
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.ProfileStatusMessage;
import com.pentaho.profiling.api.ProfilingField;
import com.pentaho.profiling.api.action.ProfileActionExceptionWrapper;
import com.pentaho.profiling.api.datasource.DataSourceReference;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bryan on 9/29/14.
 */
public class ProfileStatusImpl implements ProfileStatus {
  protected List<ProfilingField> fields;
  protected Long totalEntities;
  protected ProfileStatusMessage currentOperation;
  protected ProfileActionExceptionWrapper operationError;
  protected List<ProfileFieldProperty> profileFieldProperties;
  protected String id;
  protected DataSourceReference dataSourceReference;
  protected long sequenceNumber;

  public ProfileStatusImpl( String id, DataSourceReference dataSourceReference ) {
    this( id, dataSourceReference, 0L );
  }

  public ProfileStatusImpl( String id, DataSourceReference dataSourceReference, long sequenceNumber ) {
    this( null, null, null, null, null, id, dataSourceReference, sequenceNumber );
  }

  public ProfileStatusImpl( ProfileStatus profileStatus ) {
    this( profileStatus.getFields(), profileStatus.getTotalEntities(), profileStatus.getCurrentOperation(),
      profileStatus.getOperationError(), profileStatus.getProfileFieldProperties(), profileStatus.getId(),
      profileStatus.getDataSourceReference(), profileStatus.getSequenceNumber() + 1 );
  }

  public ProfileStatusImpl( List<ProfilingField> fields, Long totalEntities,
                            ProfileStatusMessage currentOperation,
                            ProfileActionExceptionWrapper operationError,
                            List<ProfileFieldProperty> profileFieldProperties, String id,
                            DataSourceReference dataSourceReference, long sequenceNumber ) {
    if ( fields == null ) {
      fields = new ArrayList<ProfilingField>();
    }
    this.fields = Collections.unmodifiableList( new ArrayList<ProfilingField>( fields ) );
    this.totalEntities = totalEntities;
    this.currentOperation = currentOperation;
    this.operationError = operationError;
    if ( profileFieldProperties == null ) {
      profileFieldProperties = new ArrayList<ProfileFieldProperty>();
    }
    this.profileFieldProperties =
      Collections.unmodifiableList( new ArrayList<ProfileFieldProperty>( profileFieldProperties ) );
    this.id = id;
    this.dataSourceReference = dataSourceReference;
    this.sequenceNumber = sequenceNumber;
  }

  @Override @XmlElement
  public String getId() {
    return id;
  }

  @Override @XmlElement
  public DataSourceReference getDataSourceReference() {
    return dataSourceReference;
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
  public ProfileStatusMessage getCurrentOperation() {
    return currentOperation;
  }

  @Override @XmlElement
  public ProfileActionExceptionWrapper getOperationError() {
    return operationError;
  }

  @Override @XmlElement
  public List<ProfileFieldProperty> getProfileFieldProperties() {
    return profileFieldProperties;
  }

  @Override public long getSequenceNumber() {
    return sequenceNumber;
  }
}
