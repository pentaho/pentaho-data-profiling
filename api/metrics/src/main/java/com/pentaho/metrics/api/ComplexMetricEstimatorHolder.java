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

import java.io.IOException;

/**
 * Interface to a container that encapsulates a "complex" metric estimator - i.e.
 * something that requires state to be maintained within the estimator itself
 * <p/>
 * Created by mhall on 24/01/15.
 */
public interface ComplexMetricEstimatorHolder {

  /**
   * Set the estimator to be encapsulated
   *
   * @param estimator the estimator to be encapsulated
   * @param <E>       the type of the estimator
   */
  <E> void setEstimator( E estimator );

  /**
   * Get the encapsulated estimator
   *
   * @param <E> the type of the estimator
   * @return the estimator
   */
  <E> E getEstimator();

  /**
   * Get a serialized copy of the estimator
   *
   * @return a serialized copy of the estimator as an array of bytes
   * @throws IOException if a problem occurs during serialization
   */
  byte[] getSerialized() throws IOException;
}
