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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.pentaho.profiling.api.stats.Statistic;
import com.pentaho.profiling.api.stats.ValueProducer;
import com.pentaho.profiling.api.stats.calculators.MeanValueCalculator;

public class SumValueProcessorTest {

  @Test
  public void testTypical() {
    SumValueProcessor p = new SumValueProcessor();

    assertTrue( p.getName() != null );
    assertEquals( Statistic.Metric.SUM.toString(), p.getName() );
    assertEquals( 0L, ( (Double) p.getValue() ).longValue() );
  }

  @Test
  public void testProcess() throws Exception {
    SumValueProcessor p = new SumValueProcessor();

    p.process( new Double( 12.2 ) );
    assertTrue( ( (Double) p.getValue() ).doubleValue() == 12.2 );
    p.process( new Double( 20.1 ) );
    assertTrue( ( (Double) p.getValue() ).doubleValue() == 32.3 );

    p.process( null );
    assertTrue( ( (Double) p.getValue() ).doubleValue() == 32.3 );

    p = new SumValueProcessor( new Double( -2.2 ) );
    assertTrue( ( (Double) p.getValue() ).doubleValue() == -2.2 );
  }

  @Test
  public void testAggregate() throws Exception {
    SumValueProcessor p = new SumValueProcessor();
    p.process( new Double( 12.2 ) );

    SumValueProcessor p2 = new SumValueProcessor();
    p2.process( new Double( 20.1 ) );

    p.aggregate( p2 );
    assertTrue( ( (Double) p.getValue() ).doubleValue() == 32.3 );
    assertTrue( ( (Double) p2.getValue() ).doubleValue() == 20.1 );
  }

  @Test
  public void testGetStatistic() throws Exception {
    SumValueProcessor p = new SumValueProcessor();
    p.process( new Double( 12.2 ) );

    assertTrue( p.getStatistic() != null );
    assertEquals( p.getName(), p.getStatistic().getName() );
    double d = ( (Double) p.getValue() ).doubleValue();
    double d2 = ( (Double) p.getStatistic().getValue() ).doubleValue();

    assertEquals( d, d2, 0.000000001 );
  }

  @Test
  public void testProcessMissingCountProducer() throws Exception {
    Map<String, ValueProducer> producerMap = new HashMap<String, ValueProducer>();

    MeanValueCalculator p = new MeanValueCalculator();
    SumValueProcessor sumP = new SumValueProcessor();
    sumP.process( new Double( 2.2 ) );
    sumP.process( new Double( 5.0 ) );
    sumP.process( new Double( -1.1 ) );

    producerMap.put( SumValueProcessor.ID, sumP );
    try {
      p.process( producerMap );
      fail( "Expected an exception for missing count producer" );
    } catch ( Exception e ) {
      //
    }
  }

  @Test
  public void testProcessMissingSumProducer() throws Exception {
    Map<String, ValueProducer> producerMap = new HashMap<String, ValueProducer>();

    MeanValueCalculator p = new MeanValueCalculator();
    CountValueProcessor countP = new CountValueProcessor();
    countP.setCount( 4L );
    producerMap.put( CountValueProcessor.ID, countP );
    try {
      p.process( producerMap );
      fail( "Expected an exception for missing sum producer" );
    } catch ( Exception e ) {
      //
    }
  }

}
