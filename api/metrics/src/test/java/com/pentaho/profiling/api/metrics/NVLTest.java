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

package com.pentaho.profiling.api.metrics;

import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by bryan on 2/5/15.
 */
public class NVLTest {
  private NVL nvl;
  private NVLOperation<String> constantStringOperation;
  private final String constString = "don't return me";

  @Before
  public void setup() {
    nvl = new NVL();
    constantStringOperation = new NVLOperation<String>() {
      @Override public String perform( String first, String second ) {
        return constString;
      }
    };
  }

  @Test
  public void testPerformNullFirstAndSecond() {
    assertNull( nvl.perform( constantStringOperation, null, null ) );
  }

  @Test
  public void testPerformNullFirst() {
    String second = "second";
    assertEquals( second, nvl.perform( constantStringOperation, null, second ) );
  }

  @Test
  public void testPerformNullSecond() {
    String first = "first";
    assertEquals( first, nvl.perform( constantStringOperation, first, null ) );
  }

  @Test
  public void testPerformBoth() {
    String first = "first";
    String second = "second";
    assertEquals( constString, nvl.perform( constantStringOperation, first, second ) );
  }

  @Test
  public void testPerformAndSetValue() {
    DataSourceMetricManager dataSourceMetricManager = new DataSourceMetricManager();
    String testPath = "testPath";
    dataSourceMetricManager.setValue( "dummy", testPath );
    assertEquals( constString,
      nvl.performAndSet( constantStringOperation, dataSourceMetricManager, "fake", testPath ) );
    assertEquals( constString, dataSourceMetricManager.getValueNoDefault( testPath ) );
  }

  @Test
  public void testPerformAndSetManager() {
    DataSourceMetricManager dataSourceMetricManager = new DataSourceMetricManager();
    DataSourceMetricManager dataSourceMetricManager2 = new DataSourceMetricManager();
    String testPath = "testPath";
    dataSourceMetricManager.setValue( "dummy", testPath );
    dataSourceMetricManager2.setValue( "fake", testPath );
    assertEquals( constString,
      nvl.performAndSet( constantStringOperation, dataSourceMetricManager, dataSourceMetricManager2, testPath ) );
    assertEquals( constString, dataSourceMetricManager.getValueNoDefault( testPath ) );
  }
}
