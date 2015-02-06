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

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by mhall on 25/01/15.
 */
public class DataSourceMetricManagerTest {

  private DataSourceMetricManager metricManager;

  @Before public void setup() {
    metricManager = new DataSourceMetricManager();
  }

  @Test public void testGetValueDefault() {
    metricManager.setValue( "test-value", "a", "b" );
    String defaultValue = "default-value";
    assertEquals( defaultValue, metricManager.getValue( defaultValue, "a", "c" ) );
  }

  @Test public void testGetValueDefault2() {
    metricManager.setValue( "test-value", "a", "b" );
    String defaultValue = "default-value";
    assertEquals( defaultValue, metricManager.getValue( defaultValue, "b", "c" ) );
  }

  @Test public void testGetValueNoDefault() {
    metricManager.setValue( "test-value", "a", "b" );
    assertNull( metricManager.getValueNoDefault( "a", "c" ) );
  }

  @Test public void testGetValueAndClear() {
    String value = "test-value";
    metricManager.setValue( value, "a", "b" );
    assertEquals( value, metricManager.getValueNoDefault( "a", "b" ) );
    metricManager.clear( new ArrayList<String[]>( Arrays.<String[]>asList( new String[] { "a", "b" } ) ) );
    assertEquals( null, metricManager.getValueNoDefault( "a", "b" ) );
  }
}
