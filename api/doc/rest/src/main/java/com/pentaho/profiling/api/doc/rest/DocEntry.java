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

package com.pentaho.profiling.api.doc.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bryan on 4/6/15.
 */
public class DocEntry {
  private String message;
  private String returnDescription;
  private List<DocParameter> parameters;

  public DocEntry() {
    this( null, null, null );
  }

  public DocEntry( String message, String returnDescription, List<DocParameter> parameters ) {
    this.message = message;
    this.returnDescription = returnDescription;
    this.parameters = parameters == null ? new ArrayList<DocParameter>() : new ArrayList<DocParameter>( parameters );
  }

  public String getMessage() {
    return message;
  }

  public void setMessage( String message ) {
    this.message = message;
  }

  public String getReturnDescription() {
    return returnDescription;
  }

  public void setReturnDescription( String returnDescription ) {
    this.returnDescription = returnDescription;
  }

  public List<DocParameter> getParameters() {
    return Collections.unmodifiableList( parameters == null ? new ArrayList<DocParameter>() : parameters );
  }

  public void setParameters( List<DocParameter> parameters ) {
    this.parameters = parameters == null ? new ArrayList<DocParameter>() : new ArrayList<DocParameter>( parameters );
  }
}
