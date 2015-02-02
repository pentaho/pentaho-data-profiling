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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Default implementation of a complex metric holder that uses standard java serialization
 * <p/>
 * Created by mhall on 24/01/15.
 */
public class DefaultComplexMetricEstimatorHolder implements ComplexMetricEstimatorHolder {

  /**
   * The estimator encapsulated
   */
  protected transient Object estimator;

  /**
   * Constructor
   *
   * @param estimator the estimator to encapsulate
   * @param <E>       the type of the estimator
   */
  public <E> DefaultComplexMetricEstimatorHolder( E estimator ) {
    this.estimator = estimator;
  }

  /**
   * Construct a new instance from an array of bytes (containing a serialized estimator)
   *
   * @param serialized an array of bytes containing the serialized estimator
   * @throws IOException            if a problem occurs when de-serializing
   * @throws ClassNotFoundException if the class of the serialized estimator can't be found
   */
  public DefaultComplexMetricEstimatorHolder( byte[] serialized ) throws IOException, ClassNotFoundException {

    ObjectInputStream ois = null;
    try {
      ois = new ObjectInputStream( new BufferedInputStream( new ByteArrayInputStream( serialized ) ) );
      estimator = ois.readObject();
    } finally {
      if ( ois != null ) {
        ois.close();
      }
    }
  }

  /**
   * Set the estimator to encapsulate
   *
   * @param estimator the estimator to be encapsulated
   * @param <E>       the type of the estimator
   */
  public <E> void setEstimator( E estimator ) {
    this.estimator = estimator;
  }

  /**
   * Get the encapsulated estimator
   *
   * @param <E> the type of the estimator
   * @return the estimator
   */
  public <E> E getEstimator() {
    return (E) estimator;
  }

  /**
   * Get a serialized copy of the estimator using standard object serialization
   *
   * @return a serialized copy of the estimator as an array of bytes
   * @throws IOException if a problem occurs during serialization
   */
  public byte[] getSerialized() throws IOException {
    if ( estimator == null ) {
      throw new IOException( "No estimator set to serialize" );
    }
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = null;
    try {
      oos = new ObjectOutputStream( new BufferedOutputStream( bos ) );

      oos.writeObject( estimator );
    } finally {
      if ( oos != null ) {
        oos.flush();
        oos.close();
      }
    }
    return bos.toByteArray();
  }
}
