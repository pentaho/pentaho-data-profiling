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

package com.pentaho.profiling.api.metrics.field;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for encapsulating a field value read from a data source along with arbitrary metadata about it (e.g. its path,
 * whether it's a leaf etc.)
 * <p/>
 * Created by mhall on 23/01/15.
 */
public class DataSourceFieldValue {
  private static final String PHYSICAL_NAME = "physicalName";
  private static final String LOGICAL_NAME = "logicalName";
  /**
   * The actual value
   */
  private Object fieldValue;

  /**
   * String capable of uniquely identifying this field
   */
  private String physicalName;

  /**
   * Human-friendly name
   */
  private String logicalName;

  /**
   * Canonical name of field type
   */
  private String fieldTypeName;
  /**
   * arbitrary data source specific metadata about this value - e.g. path, whether its a leaf etc.
   */
  private Map<String, Object> fieldMetadata = new HashMap<String, Object>();

  /**
   * Constructor for a null-valued DataSourceField
   */
  public DataSourceFieldValue() {
    this( null );
  }

  /**
   * Constructor
   *
   * @param fieldValue the value to encapsulate
   */
  public DataSourceFieldValue( Object fieldValue ) {
    this.fieldValue = fieldValue;
  }

  /**
   * Gets the physical (uniquely identifying) name
   *
   * @return the physical (uniquely identifying) name
   */
  public String getPhysicalName() {
    return physicalName;
  }

  /**
   * Sets the physical (uniquely identifying) name
   *
   * @param physicalName the physical (uniquely identifying) name
   */
  public void setPhysicalName( String physicalName ) {
    this.physicalName = physicalName;
  }

  /**
   * Gets the logical (human friendly) name
   *
   * @return the logical name
   */
  public String getLogicalName() {
    return logicalName;
  }

  /**
   * Sets the logical (human friendly) name
   *
   * @param logicalName the logical name
   */
  public void setLogicalName( String logicalName ) {
    this.logicalName = logicalName;
  }

  /**
   * Get the field value
   *
   * @return the field value
   */
  public Object getFieldValue() {
    return this.fieldValue;
  }

  /**
   * Set the field value
   *
   * @param fieldValue the field value
   */
  public void setFieldValue( Object fieldValue ) {
    this.fieldTypeName = null;
    this.fieldValue = fieldValue;
  }

  /**
   * Set the value of a piece of metadata
   *
   * @param keyName       the name of the metadata
   * @param metadataValue the associated value of the metadata
   * @param <T>           the type of the metadata value
   */
  public <T> void setFieldMetatdata( String keyName, T metadataValue ) {
    fieldMetadata.put( keyName, metadataValue );
  }

  /**
   * Get the value of a piece of metadata
   *
   * @param keyName the name of the metadata to get
   * @param <T>     the type of the metadata
   * @return the value of the metadata
   */
  public <T> T getFieldMetadata( String keyName ) {
    return (T) fieldMetadata.get( keyName );
  }

  /**
   * Clear the metadata map
   */
  public void clearFieldMetadata() {
    fieldMetadata.clear();
  }

  /**
   * Get the number of metadata elements that are currently set
   *
   * @return the number of metadata elements associated with this field value
   */
  public int numMetadataElements() {
    return fieldMetadata.size();
  }

  public String getFieldTypeName() {
    if ( fieldTypeName == null ) {
      fieldTypeName = fieldValue == null ? "null" : fieldValue.getClass().getCanonicalName();
    }
    return fieldTypeName;
  }

  public void setFieldTypeName( String fieldTypeName ) {
    this.fieldTypeName = fieldTypeName;
  }
}
