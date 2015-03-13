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

import com.pentaho.profiling.api.metrics.field.DataSourceField;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.metrics.MetricContributorUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by mhall on 28/01/15.
 */
public class MetricContributorUtilsTest {
  @Test
  public void testConstructor() {
    // This is only done for cobertura's sake
    new MetricContributorUtils();
  }

  @Test public void testCreateMetricProperty() {
    String keyPath = "test-key-path";
    String nameKey = "test-name-key";
    String path1 = "path1";
    String path2 = "path2";
    ProfileFieldProperty metricProperty = MetricContributorUtils.createMetricProperty( keyPath, nameKey, path1, path2 );
    assertNotNull( metricProperty );
    assertEquals( keyPath, metricProperty.getNamePath() );
    assertEquals( nameKey, metricProperty.getNameKey() );
    assertEquals( 3, metricProperty.getPathToProperty().size() );
    assertEquals( DataSourceField.TYPE, metricProperty.getPathToProperty().get( 0 ) );
    assertEquals( path1, metricProperty.getPathToProperty().get( 1 ) );
    assertEquals( path2, metricProperty.getPathToProperty().get( 2 ) );
  }
}
