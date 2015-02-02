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

import com.clearspring.analytics.stream.quantile.TDigest;
import com.pentaho.metrics.api.TDigestComplexMetricEstimatorHolder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by mhall on 25/01/15.
 */
public class DataSourceMetricManagerTest {

  private DataSourceMetricManager metricManager;

  @Before public void setup() {
    metricManager = new DataSourceMetricManager( new HashMap<String, Object>() );
  }

  @Test public void testGetValueDefault() {
    metricManager.setValue( "test-value", "a", "b" );
    String defaultValue = "default-value";
    assertEquals( defaultValue, metricManager.getValue( defaultValue, "a", "c" ) );
  }

  @Test public void testGetValueNoDefault() {
    metricManager.setValue( "test-value", "a", "b" );
    assertNull( metricManager.getValueNoDefault( "a", "c" ) );
  }

  @Test public void testGetValue() {
    String value = "test-value";
    metricManager.setValue( value, "a", "b" );
    assertEquals( value, metricManager.getValueNoDefault( "a", "b" ) );
  }

  @Test public void testGetComplexMetricEstimator() {
    TDigestComplexMetricEstimatorHolder holder = new TDigestComplexMetricEstimatorHolder( new TDigest( 50 ) );
    metricManager.setComplexMetricEstimatorHolder( holder, "a", "b" );
    assertNotNull( metricManager.getComplexMetricEstimatorHolder( "a", "b" ) );
  }

  @Test public void setGetNonExistentComplexMetricEstimator() {
    TDigestComplexMetricEstimatorHolder holder = new TDigestComplexMetricEstimatorHolder( new TDigest( 50 ) );
    metricManager.setComplexMetricEstimatorHolder( holder, "a", "b" );
    assertNull( metricManager.getComplexMetricEstimatorHolder( "a", "c" ) );
  }

  @Test public void testTransferToFromSerializedForm() throws IOException {
    TDigest digest = new TDigest( 50.0, new Random( 1 ) );
    TDigestComplexMetricEstimatorHolder holder = new TDigestComplexMetricEstimatorHolder( digest );
    digest.add( 2.25 );
    digest.add( 2.5 );
    digest.add( 2.75 );
    digest.add( 3.75 );
    digest.add( 4.75 );

    metricManager.setComplexMetricEstimatorHolder( holder, "a", "b" );
    metricManager.transferSerializedComplexMetricEstimatorHolders();
    assertNotNull( metricManager.getValueNoDefault( "a", "b" ) );

    byte[] serialized = metricManager.getValueNoDefault( "a", "b" );
    TDigest newDigest = new TDigestComplexMetricEstimatorHolder( serialized ).getEstimator();
    assertNotNull( newDigest );
    assertEquals( Double.valueOf( 2.625 ), newDigest.quantile( 0.5 ) );
  }
}
