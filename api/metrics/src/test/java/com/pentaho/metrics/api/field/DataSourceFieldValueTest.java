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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by mhall on 28/01/15.
 */
public class DataSourceFieldValueTest {

  @Test
  public void testNullField() {
    DataSourceFieldValue val = new DataSourceFieldValue(  );
    assertNull(val.getFieldValue());
    assertEquals(0, val.getNumMetadataElements());
  }

  @Test
  public void testNonNullField() {
    DataSourceFieldValue val = new DataSourceFieldValue( "test" );
    assertEquals("test", val.getFieldValue());
    val.setFieldValue( 2L );
    assertEquals(2L, val.getFieldValue());
  }

  @Test
  public void testSetMetadata() {
    DataSourceFieldValue val = new DataSourceFieldValue(  );
    String metadataKey = "some.meta.data";
    int metadataVal = 50;
    val.setFieldMetatdata( metadataKey, metadataVal );
    assertNotNull( val.getFieldMetadata( metadataKey ) );
    assertEquals(metadataVal, val.getFieldMetadata( metadataKey ));
  }
}
