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

package com.pentaho.profiling.api;

import com.pentaho.profiling.api.action.ProfileActionExceptionWrapper;
import com.pentaho.profiling.api.datasource.DataSourceReference;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bryan on 7/31/14.
 */
@XmlRootElement
public class ProfileStatus {
  private String id;
  private DataSourceReference dataSourceReference;
  private List<ProfilingField> fields;
  private Long totalEntities;
  private ProfileStatusMessage currentOperation;
  private ProfileActionExceptionWrapper operationError;
  private List<ProfileFieldProperty> profileFieldProperties;


  @XmlElement
  public String getId() {
    return id;
  }

  public void setId( String id ) {
    this.id = id;
  }

  @XmlElement
  public DataSourceReference getDataSourceReference() {
    return dataSourceReference;
  }

  public void setDataSourceReference( DataSourceReference dataSourceReference ) {
    this.dataSourceReference = dataSourceReference;
  }

  @XmlElement
  public List<ProfilingField> getFields() {
    if ( fields != null ) {
      List<ProfilingField> result = new ArrayList<ProfilingField>( fields.size() );
      for ( ProfilingField field : fields ) {
        result.add( field.copy() );
      }
      return result;
    }
    return null;
  }

  public void setFields( List<ProfilingField> fields ) {
    if ( fields == null ) {
      this.fields = null;
    } else {
      List<ProfilingField> newFields = new ArrayList<ProfilingField>( fields.size() );
      for ( ProfilingField field : fields ) {
        newFields.add( field.copy() );
      }
      this.fields = newFields;
    }
  }

  @XmlElement
  public Long getTotalEntities() {
    return totalEntities;
  }

  public void setTotalEntities( Long totalEntities ) {
    this.totalEntities = totalEntities;
  }

  @XmlElement
  public ProfileStatusMessage getCurrentOperation() {
    return currentOperation;
  }

  public void setCurrentOperation( ProfileStatusMessage currentOperation ) {
    this.currentOperation = currentOperation;
  }

  @XmlElement
  public ProfileActionExceptionWrapper getOperationError() {
    return operationError;
  }

  public void setOperationError( ProfileActionExceptionWrapper operationError ) {
    this.operationError = operationError;
  }

  @XmlElement
  public List<ProfileFieldProperty> getProfileFieldProperties() {
    return profileFieldProperties;
  }

  public void setProfileFieldProperties( List<ProfileFieldProperty> profileFieldProperties ) {
    this.profileFieldProperties = profileFieldProperties;
  }
}
