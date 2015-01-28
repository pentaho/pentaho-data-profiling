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

package com.pentaho.metrics.api;

import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by mhall on 28/01/15.
 */
public class HyperLogLogPlusComplexMetricEstimatorHolderTest {

  private HyperLogLogPlus getEstimator() {
    HyperLogLogPlus estimator = new HyperLogLogPlus( 12, 16 );
    estimator.offer( "one" );
    estimator.offer( "two" );
    estimator.offer( "three" );

    return estimator;
  }

  @Test public void testSimple() {
    HyperLogLogPlus estimator = getEstimator();

    HyperLogLogPlusComplexMetricEstimatorHolder
        hyperLogLogPlusComplexMetricEstimatorHolder =
        new HyperLogLogPlusComplexMetricEstimatorHolder( estimator );

    assertNotNull( hyperLogLogPlusComplexMetricEstimatorHolder.getEstimator() );
    assertEquals( estimator.cardinality(),
        ( (HyperLogLogPlus) hyperLogLogPlusComplexMetricEstimatorHolder.getEstimator() ).cardinality() );
  }

  @Test public void testSerialization() throws IOException {
    HyperLogLogPlus estimator = getEstimator();

    HyperLogLogPlusComplexMetricEstimatorHolder
        hyperLogLogPlusComplexMetricEstimatorHolder =
        new HyperLogLogPlusComplexMetricEstimatorHolder( estimator );

    assertNotNull( hyperLogLogPlusComplexMetricEstimatorHolder.getSerialized() );
  }

  @Test public void testDesialization() throws IOException {
    HyperLogLogPlus estimator = getEstimator();

    HyperLogLogPlusComplexMetricEstimatorHolder
        hyperLogLogPlusComplexMetricEstimatorHolder =
        new HyperLogLogPlusComplexMetricEstimatorHolder( estimator );

    byte[] serialized = hyperLogLogPlusComplexMetricEstimatorHolder.getSerialized();

    HyperLogLogPlusComplexMetricEstimatorHolder
        hyperLogLogPlusComplexMetricEstimatorHolder1 =
        new HyperLogLogPlusComplexMetricEstimatorHolder( serialized );

    assertEquals( estimator.cardinality(),
        ( (HyperLogLogPlus) hyperLogLogPlusComplexMetricEstimatorHolder1.getEstimator() ).cardinality() );
  }
}
