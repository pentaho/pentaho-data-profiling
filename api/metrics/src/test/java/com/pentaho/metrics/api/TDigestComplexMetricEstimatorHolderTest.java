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

import com.clearspring.analytics.stream.quantile.TDigest;
import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by mhall on 28/01/15.
 */
public class TDigestComplexMetricEstimatorHolderTest {

  private TDigest getEstimator() {
    TDigest digest = new TDigest( 50 );
    digest.add( 2.25 );
    digest.add( 2.5 );
    digest.add( 2.75 );
    digest.add( 3.75 );
    digest.add( 4.75 );

    return digest;
  }

  @Test public void testSimple() {
    TDigest digest = getEstimator();

    TDigestComplexMetricEstimatorHolder
        tDigestComplexMetricEstimatorHolder =
        new TDigestComplexMetricEstimatorHolder( digest );
    assertNotNull( tDigestComplexMetricEstimatorHolder.getEstimator() );

    assertEquals( digest.quantile( 0.5 ),
        ( (TDigest) tDigestComplexMetricEstimatorHolder.getEstimator() ).quantile( 0.5 ) );
  }

  @Test public void testSerialization() throws IOException {
    TDigest digest = getEstimator();

    TDigestComplexMetricEstimatorHolder
        tDigestComplexMetricEstimatorHolder =
        new TDigestComplexMetricEstimatorHolder( digest );

    Assert.assertNotNull( tDigestComplexMetricEstimatorHolder.getSerialized() );
  }

  @Test public void testDeserialization() throws IOException {
    TDigest digest = getEstimator();

    TDigestComplexMetricEstimatorHolder
        tDigestComplexMetricEstimatorHolder =
        new TDigestComplexMetricEstimatorHolder( digest );

    byte[] serialized = tDigestComplexMetricEstimatorHolder.getSerialized();

    TDigestComplexMetricEstimatorHolder
        tDigestComplexMetricEstimatorHolder1 =
        new TDigestComplexMetricEstimatorHolder( serialized );

    assertEquals( digest.quantile( 0.5 ),
        ( (TDigest) tDigestComplexMetricEstimatorHolder1.getEstimator() ).quantile( 0.5 ) );
  }
}
