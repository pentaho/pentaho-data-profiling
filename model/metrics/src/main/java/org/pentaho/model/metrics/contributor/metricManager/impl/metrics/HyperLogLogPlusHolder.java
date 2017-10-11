/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.model.metrics.contributor.metricManager.impl.metrics;

import com.clearspring.analytics.stream.cardinality.CardinalityMergeException;
import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import org.pentaho.profiling.api.ValueTypeMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by bryan on 3/11/15.
 */
public class HyperLogLogPlusHolder implements ValueTypeMetrics {
  private static final Logger LOGGER = LoggerFactory.getLogger( HyperLogLogPlusHolder.class );
  private long cardinality;
  private HyperLogLogPlus hyperLogLogPlus;

  public HyperLogLogPlusHolder() {
    this( null );
  }

  public HyperLogLogPlusHolder( HyperLogLogPlus hyperLogLogPlus ) {
    this.hyperLogLogPlus = hyperLogLogPlus;
  }

  public long getCardinality() {
    return cardinality;
  }

  public void setCardinality( long cardinality ) {
    this.cardinality = cardinality;
  }

  public synchronized void calculateCardinality() {
    setCardinality( hyperLogLogPlus.cardinality() );
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

  public synchronized HyperLogLogPlusHolder merge( HyperLogLogPlusHolder hyperLogLogPlusHolder )
    throws CardinalityMergeException {
    return new HyperLogLogPlusHolder(
      (HyperLogLogPlus) hyperLogLogPlus.merge( hyperLogLogPlusHolder.hyperLogLogPlus ) );
  }

  public synchronized void offer( Object o ) {
    hyperLogLogPlus.offer( o );
  }

  @Override public synchronized Object clone() {
    HyperLogLogPlusHolder result = new HyperLogLogPlusHolder();
    if ( hyperLogLogPlus != null ) {
      result.setBytes( getBytes() );
    }
    result.cardinality = cardinality;
    return result;
  }
}
