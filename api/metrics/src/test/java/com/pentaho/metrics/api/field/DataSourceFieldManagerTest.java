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

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class DataSourceFieldManagerTest {

  private DataSourceFieldManager dataSourceFieldManager;

  @Before public void setup() {
    DataSourceField dataSourceField = new DataSourceField();
    dataSourceField.setName( "field1" );
    dataSourceField.setPath( "a.b" );
    dataSourceField.setFieldProperty( "OrigPath", "a[].b" );
    dataSourceField.getMetricManagerForType( "test", true );
    dataSourceField.getMetricManagerForType( "test2", true );
    DataSourceField dataSourceField2 = new DataSourceField();
    dataSourceField2.setName( "field2" );
    dataSourceField2.setPath( "a.c" );
    dataSourceField2.setFieldProperty( "OrigPath", "a[].c" );
    dataSourceField2.getMetricManagerForType( "test", true );

    dataSourceFieldManager =
        new DataSourceFieldManager(
            Arrays.asList( dataSourceField.getProfilingField(), dataSourceField2.getProfilingField() ) );
  }

  @Test public void testGetPathToMetricManagerMap() {
    assertEquals( 2, dataSourceFieldManager.getPathToDataSourceFieldMap().size() );
    assertEquals( "field1", dataSourceFieldManager.getPathToDataSourceFieldMap().get( "a.b" ).getName() );
    assertEquals( "field2", dataSourceFieldManager.getPathToDataSourceFieldMap().get( "a.c" ).getName() );
  }

  @Test public void testGetPathToMetricManagerForTypeMap() {
    assertEquals( 2, dataSourceFieldManager.getPathToMetricManagerForTypeMap( "test" ).size() );
    assertEquals( 1, dataSourceFieldManager.getPathToMetricManagerForTypeMap( "test2" ).size() );
  }

  @Test public void testGetFieldPropertyToFieldMap() {
    Map<String, DataSourceField>
        fieldPropToFieldMap =
        dataSourceFieldManager.getFieldPropertyToDataSourceFieldMap( "OrigPath" );
    assertEquals( 2, fieldPropToFieldMap.size() );
    assertTrue( fieldPropToFieldMap.containsKey( "a[].b" ) );
    assertTrue( fieldPropToFieldMap.containsKey( "a[].c" ) );
  }

  @Test public void testGetFieldPropertyToTypeMap() {
    assertEquals( 2, dataSourceFieldManager.getFieldPropertyToMetricManagerForTypeMap( "OrigPath", "test" ).size() );
    assertEquals( 1, dataSourceFieldManager.getFieldPropertyToMetricManagerForTypeMap( "OrigPath", "test2" ).size() );
  }

  @Test public void getGetDataSourceFields() {
    assertEquals( 2, dataSourceFieldManager.getDataSourceFields().size() );
  }
}
