/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
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

package com.pentaho.profiling.api.stats.processors;

import com.pentaho.profiling.api.stats.AbstractValueProducer;
import com.pentaho.profiling.api.stats.Aggregatable;
import com.pentaho.profiling.api.stats.Statistic;
import com.pentaho.profiling.api.stats.StatisticProducer;
import com.pentaho.profiling.api.stats.ValueProcessor;

/**
 * Maintains the sum of the squared values seen for a field
 * 
 * @author bryan
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 */
public class SumOfSquaresValueProcessor extends AbstractValueProducer implements
    Aggregatable<SumOfSquaresValueProcessor>, ValueProcessor, StatisticProducer {

  public static final String ID = Statistic.Metric.SUMSQ.toString();

  /** The sum of values seen */
  protected double sumOfSquares;

  /**
   * Constructor
   */
  public SumOfSquaresValueProcessor() {
    super( ID );
  }

  /**
   * Construct a new SumOfSquaresValueProcessor with an initial value
   * 
   * @param sumOfSq
   *          the initial value
   */
  public SumOfSquaresValueProcessor( double sumOfSq ) {
    this();
    sumOfSquares = sumOfSq;
  }

  @Override
  public Object getValue() {
    return this.sumOfSquares;
  }

  @Override
  public void process( Object input ) throws Exception {
    if ( input != null ) {
      double val = ( (Number) input ).doubleValue();
      sumOfSquares += val * val;
    }
  }

  /**
   * Process an already squared value
   * 
   * @param input
   *          the squared value to process
   * @throws Exception
   *           if a problem occurs
   */
  public void processSquaredValue( Object input ) throws Exception {
    if ( input != null ) {
      double val = ( (Number) input ).doubleValue();
      sumOfSquares += val;
    }
  }

  @Override
  public SumOfSquaresValueProcessor aggregate( SumOfSquaresValueProcessor toAggregate ) throws Exception {
    this.sumOfSquares += toAggregate.sumOfSquares;

    return this;
  }

  @Override
  public Statistic getStatistic() {
    return new Statistic( getName(), sumOfSquares );
  }
}
