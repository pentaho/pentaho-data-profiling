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

package com.pentaho.metrics.api.field;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for encapsulating a field value read from a data source along with
 * arbitrary metadata about it (e.g. its path, whether it's a leaf etc.)
 * <p/>
 * Created by mhall on 23/01/15.
 */
public class DataSourceFieldValue {

  /**
   * key for path
   */
  public static final String PATH = "path";

  /**
   * key for leaf
   */
  public static final String LEAF = "leaf";

  /**
   * The actual value
   */
  private Object fieldValue;

  /**
   * arbitrary data source specific metadata about this value - e.g. path, whether its a leaf etc.
   */
  private Map<String, Object> fieldMetadata = new HashMap<String, Object>();

  /**
   * Constructor for a null-valued DataSourceField
   */
  public DataSourceFieldValue() {
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
   * Set the field value
   *
   * @param fieldValue the field value
   */
  public void setFieldValue( Object fieldValue ) {
    this.fieldValue = fieldValue;
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
  public int getNumMetadataElements() {
    return fieldMetadata.size();
  }
}
