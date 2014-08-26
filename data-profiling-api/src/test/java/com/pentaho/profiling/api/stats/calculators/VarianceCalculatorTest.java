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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.pentaho.profiling.api.stats.Statistic;
import com.pentaho.profiling.api.stats.ValueProducer;
import com.pentaho.profiling.api.stats.processors.CountValueProcessor;
import com.pentaho.profiling.api.stats.processors.SumOfSquaresValueProcessor;
import com.pentaho.profiling.api.stats.processors.SumValueProcessor;

public class VarianceCalculatorTest {
  @Test
  public void testTypical() {
    VarianceCalculator p = new VarianceCalculator();

    assertTrue( p.getName() != null );
    assertEquals( Statistic.Metric.VARIANCE.toString(), p.getName() );

    assertTrue( Double.isNaN( ( (Double) p.getValue() ).doubleValue() ) );
  }

  @Test
  public void testProcess() throws Exception {
    Map<String, ValueProducer> producerMap = new HashMap<String, ValueProducer>();
    SumValueProcessor sumP = new SumValueProcessor();
    sumP.process( new Double( 2.2 ) );
    sumP.process( new Double( 5.0 ) );
    sumP.process( new Double( -1.1 ) );

    SumOfSquaresValueProcessor sumSqP = new SumOfSquaresValueProcessor();
    sumSqP.process( new Double( 2.2 ) );
    sumSqP.process( new Double( 5.0 ) );
    sumSqP.process( new Double( -1.1 ) );

    CountValueProcessor countP = new CountValueProcessor();
    countP.setCount( 3L );

    producerMap.put( SumValueProcessor.ID, sumP );
    producerMap.put( SumOfSquaresValueProcessor.ID, sumSqP );
    producerMap.put( CountValueProcessor.ID, countP );

    VarianceCalculator p = new VarianceCalculator();
    p.process( producerMap );

    double var = ( (Double) p.getValue() ).doubleValue();
    assertEquals( 9.323333, var, 0.00001 );
  }

  @Test
  public void testGetStatistic() throws Exception {
    Map<String, ValueProducer> producerMap = new HashMap<String, ValueProducer>();
    SumValueProcessor sumP = new SumValueProcessor();
    sumP.process( new Double( 2.2 ) );
    sumP.process( new Double( 5.0 ) );
    sumP.process( new Double( -1.1 ) );

    SumOfSquaresValueProcessor sumSqP = new SumOfSquaresValueProcessor();
    sumSqP.process( new Double( 2.2 ) );
    sumSqP.process( new Double( 5.0 ) );
    sumSqP.process( new Double( -1.1 ) );

    CountValueProcessor countP = new CountValueProcessor();
    countP.setCount( 3L );

    producerMap.put( SumValueProcessor.ID, sumP );
    producerMap.put( SumOfSquaresValueProcessor.ID, sumSqP );
    producerMap.put( CountValueProcessor.ID, countP );

    VarianceCalculator p = new VarianceCalculator();
    p.process( producerMap );

    assertTrue( p.getStatistic() != null );
    assertEquals( p.getName(), p.getStatistic().getName() );

    double var = ( (Double) p.getValue() ).doubleValue();
    double var2 = ( (Double) p.getStatistic().getValue() ).doubleValue();
    assertEquals( var2, var, 0 );
  }
}
