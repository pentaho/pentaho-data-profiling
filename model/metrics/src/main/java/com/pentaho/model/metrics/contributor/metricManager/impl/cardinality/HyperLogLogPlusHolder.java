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

package com.pentaho.model.metrics.contributor.metricManager.impl.cardinality;

import com.clearspring.analytics.stream.cardinality.CardinalityMergeException;
import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by bryan on 3/11/15.
 */
public class HyperLogLogPlusHolder implements Cloneable {
  private static final Logger LOGGER = LoggerFactory.getLogger( HyperLogLogPlusHolder.class );
  private HyperLogLogPlus hyperLogLogPlus;

  public HyperLogLogPlusHolder() {
    this( null );
  }

  public HyperLogLogPlusHolder( HyperLogLogPlus hyperLogLogPlus ) {
    this.hyperLogLogPlus = hyperLogLogPlus;
  }

  public synchronized byte[] getBytes() {
    try {
      return hyperLogLogPlus.getBytes();
    } catch ( IOException e ) {
      LOGGER.error( e.getMessage(), e );
      return null;
    }
  }

  public synchronized void setBytes( byte[] bytes ) {
    try {
      hyperLogLogPlus = HyperLogLogPlus.Builder.build( bytes );
    } catch ( IOException e ) {
      LOGGER.error( e.getMessage(), e );
      hyperLogLogPlus = null;
    }
  }

  public synchronized HyperLogLogPlusHolder merge( HyperLogLogPlusHolder hyperLogLogPlusHolder ) throws CardinalityMergeException {
    return new HyperLogLogPlusHolder(
      (HyperLogLogPlus) hyperLogLogPlus.merge( hyperLogLogPlusHolder.hyperLogLogPlus ) );
  }

  public synchronized void offer( Object o ) {
    hyperLogLogPlus.offer( o );
  }

  public synchronized long cardinality() {
    return hyperLogLogPlus.cardinality();
  }

  @Override protected synchronized Object clone() throws CloneNotSupportedException {
    HyperLogLogPlusHolder result = (HyperLogLogPlusHolder) super.clone();
    if ( hyperLogLogPlus != null ) {
      result.setBytes( getBytes() );
    }
    return result;
  }
}
