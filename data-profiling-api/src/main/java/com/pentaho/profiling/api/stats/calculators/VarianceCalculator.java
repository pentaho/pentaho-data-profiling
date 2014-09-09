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
import com.pentaho.profiling.api.stats.processors.SumOfSquaresValueProcessor;
import com.pentaho.profiling.api.stats.processors.SumValueProcessor;

/**
 * Calculator for the variance
 * 
 * @author Mark Hall bryan
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 */
public class VarianceCalculator extends AbstractValueProducer implements ValueCalculator, StatisticProducer {

  public static final String ID = Statistic.Metric.VARIANCE.toString();

  /** Holds the last computed variance */
  protected double variance = Double.NaN;

  public VarianceCalculator() {
    super( ID );
  }

  @Override
  public Object getValue() {
    return variance;
  }

  @Override
  public Statistic getStatistic() {
    return new Statistic( getName(), variance );
  }

  @Override
  public void process( Map<String, ValueProducer> producerMap ) throws CalculationException {
    ValueProducer countProducer = producerMap.get( CountValueProcessor.ID );
    if ( countProducer == null ) {
      throw new CalculationException( "Unable to find a count producer!" );
    }

    double count = ( (Number) countProducer.getValue() ).doubleValue();
    if ( count > 0 ) {
      variance = Double.POSITIVE_INFINITY;
      if ( count > 1 ) {
        ValueProducer sumSqProducer = producerMap.get( SumOfSquaresValueProcessor.ID );
        if ( sumSqProducer == null ) {
          throw new CalculationException( "Unable to find a sum of squares producer!" );
        }
        ValueProducer sumProducer = producerMap.get( SumValueProcessor.ID );
        if ( sumProducer == null ) {
          throw new CalculationException( "Unable to find a sum producer!" );
        }

        double sumSq = ( (Number) sumSqProducer.getValue() ).doubleValue();
        double sum = ( (Number) sumProducer.getValue() ).doubleValue();
        variance = sumSq - ( sum * sum ) / count;
        variance /= ( count - 1 );
        if ( variance < 0 ) {
          variance = 0;
        }
      }
    }
  }
}
