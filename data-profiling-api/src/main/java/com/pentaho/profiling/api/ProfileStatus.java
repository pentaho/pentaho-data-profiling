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

import com.pentaho.profiling.api.datasource.DataSourceReference;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bryan on 7/31/14.
 */
@XmlRootElement
public class ProfileStatus {
  String id;
  DataSourceReference dataSourceReference;
  List<ProfilingField> fields;
  Long totalEntities;
  String currentOperationPath;
  String currentOperation;
  List<String> currentOperationVariables;
  ProfileFieldDefinition profileFieldDefinition;


  public String getId() {
    return id;
  }

  public void setId( String id ) {
    this.id = id;
  }

  public DataSourceReference getDataSourceReference() {
    return dataSourceReference;
  }

  public void setDataSourceReference( DataSourceReference dataSourceReference ) {
    this.dataSourceReference = dataSourceReference;
  }

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

  public Long getTotalEntities() {
    return totalEntities;
  }

  public void setTotalEntities( Long totalEntities ) {
    this.totalEntities = totalEntities;
  }

  public String getCurrentOperation() {
    return currentOperation;
  }

  public void setCurrentOperation( String currentOperation ) {
    this.currentOperation = currentOperation;
  }

  public List<String> getCurrentOperationVariables() {
    return currentOperationVariables;
  }

  public void setCurrentOperationVariables( List<String> currentOperationVariables ) {
    this.currentOperationVariables = new ArrayList<String>( currentOperationVariables );
  }

  public ProfileFieldDefinition getProfileFieldDefinition() {
    return profileFieldDefinition;
  }

  public void setProfileFieldDefinition( ProfileFieldDefinition profileFieldDefinition ) {
    this.profileFieldDefinition = profileFieldDefinition;
  }

  public String getCurrentOperationPath() {
    return currentOperationPath;
  }

  public void setCurrentOperationPath( String currentOperationPath ) {
    this.currentOperationPath = currentOperationPath;
  }
}
