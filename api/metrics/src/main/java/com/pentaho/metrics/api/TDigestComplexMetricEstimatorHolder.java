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

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Implementation of a complex metric holder for the TDigest estimator
 *
 * Created by mhall on 24/01/15.
 */
public class TDigestComplexMetricEstimatorHolder extends DefaultComplexMetricEstimatorHolder {

  /**
   * Constructor
   *
   * @param estimator the TDigest instance to encapsulate
   */
  public TDigestComplexMetricEstimatorHolder( TDigest estimator ) {
    super( estimator );
  }

  /**
   * Construct from a serialized TDigest estimator
   *
   * @param serialized the serialized estimator to construct from
   */
  public TDigestComplexMetricEstimatorHolder( byte[] serialized ) {
    super( (TDigest) null );
    ByteBuffer buff = ByteBuffer.wrap( serialized );
    estimator = TDigest.fromBytes( buff );
  }

  /**
   * Get a serialized copy of the encapsulated estimator
   *
   * @return a serialized copy of the encapsulated estimator
   * @throws IOException if a problem occurs during serialization
   */
  @Override public byte[] getSerialized() throws IOException {
    if ( estimator == null ) {
      return super.getSerialized();
    }

    ByteBuffer buff = ByteBuffer.allocate( ( (TDigest) estimator ).byteSize() );
    ( (TDigest) estimator ).asSmallBytes( buff );

    return buff.array();
  }
}
