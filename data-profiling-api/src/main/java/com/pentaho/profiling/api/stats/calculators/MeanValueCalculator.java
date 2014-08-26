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

package com.pentaho.profiling.api.stats.calculators;

import java.util.Map;

import com.pentaho.profiling.api.stats.AbstractValueProducer;
import com.pentaho.profiling.api.stats.CalculationException;
import com.pentaho.profiling.api.stats.Statistic;
import com.pentaho.profiling.api.stats.StatisticProducer;
import com.pentaho.profiling.api.stats.ValueCalculator;
import com.pentaho.profiling.api.stats.ValueProducer;
import com.pentaho.profiling.api.stats.processors.CountValueProcessor;
import com.pentaho.profiling.api.stats.processors.SumValueProcessor;

/**
 * Calculator for the mean
 * 
 * @author bryan
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 */
public class MeanValueCalculator extends AbstractValueProducer implements ValueCalculator, StatisticProducer {

  public static final String ID = Statistic.Metric.MEAN.toString();

  /** The count needed for computing the mean */
  protected double count;

  /** The sum needed for computing the mean */
  protected double sum;

  /**
   * Constructor
   */
  public MeanValueCalculator() {
    super( ID );
  }

  @Override
  public Object getValue() {
    if ( count == 0 ) {
      return Double.NaN;
    }
    return sum / count;
  }

  @Override
  public void process( Map<String, ValueProducer> producerMap ) throws CalculationException {
    ValueProducer countProducer = producerMap.get( CountValueProcessor.ID );
    if ( countProducer == null ) {
      throw new CalculationException( "Unable to find a count producer!" );
    }
    ValueProducer sumProducer = producerMap.get( SumValueProcessor.ID );
    if ( sumProducer == null ) {
      throw new CalculationException( "Unable to find a sum producer!" );
    }

    this.count = ( (Number) countProducer.getValue() ).doubleValue();
    this.sum = ( (Number) sumProducer.getValue() ).doubleValue();
  }

  @Override
  public Statistic getStatistic() {
    return new Statistic( getName(), ( getValue() ) );
  }
}
