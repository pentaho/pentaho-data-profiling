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

/**
 * Encapsulates type information for a profiling field
 * 
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 */
public class ProfilingFieldType {

  /** The type */
  protected String typeName = "unknown";

  /** The count for this type */
  protected long count;

  /**
   * Constructor
   */
  public ProfilingFieldType() {
  }

  /**
   * Constructor
   * 
   * @param typeName
   *          the name of the type to create
   */
  public ProfilingFieldType( String typeName ) {
    this.typeName = typeName;
  }

  /**
   * Set the name of the type
   * 
   * @param typeName
   *          the name of the type
   */
  public void setTypeName( String typeName ) {
    this.typeName = typeName;
  }

  /**
   * Get the name of the type
   * 
   * @return the name of the type
   */
  public String getTypeName() {
    return this.typeName;
  }

  /**
   * Set the count (num occurrences) of this type
   * 
   * @param count
   *          the number of occurrences
   */
  public void setCount( long count ) {
    this.count = count;
  }

  /**
   * Get the count (num occurrences) of this type
   * 
   * @return the number of occurrences
   */
  public long getCount() {
    return this.count;
  }
}
