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

import org.junit.Test;

import com.pentaho.profiling.api.stats.Statistic;

public class MinValueProcessorTest {

  @Test
  public void testTypical() {
    MinValueProcessor p = new MinValueProcessor();

    assertTrue( p.getName() != null );
    assertEquals( Statistic.Metric.MIN.toString(), p.getName() );

    assertEquals( Double.MAX_VALUE, ( (Double) p.getValue() ).doubleValue(), 0 );
  }

  @Test
  public void testProcess() throws Exception {
    MinValueProcessor p = new MinValueProcessor();
    p.process( new Double( 5.1 ) );
    double d = ( (Double) p.getValue() ).doubleValue();

    assertEquals( 5.1, d, 0.0 );
    p.process( new Double( -2.0 ) );
    d = ( (Double) p.getValue() ).doubleValue();
    assertEquals( -2.0, d, 0.0 );

    p.process( new Double( 10.1 ) );
    d = ( (Double) p.getValue() ).doubleValue();
    assertEquals( -2.0, d, 0.0 );
  }

  @Test
  public void testAggregate() throws Exception {
    MinValueProcessor p = new MinValueProcessor();
    p.process( new Double( 5.1 ) );

    MinValueProcessor p2 = new MinValueProcessor();
    p2.process( new Double( -2.2 ) );

    p.aggregate( p2 );
    double d = ( (Double) p.getValue() ).doubleValue();
    assertEquals( -2.2, d, 0.0 );

    p2.aggregate( p );
    d = ( (Double) p.getValue() ).doubleValue();
    assertEquals( -2.2, d, 0.0 );
  }

  @Test
  public void testGetStatistic() throws Exception {
    MinValueProcessor p = new MinValueProcessor();
    p.process( new Double( 5.1 ) );

    assertTrue( p.getStatistic() != null );
    assertEquals( p.getName(), p.getStatistic().getName() );
    double d = ( (Double) p.getValue() ).doubleValue();
    double d2 = ( (Double) p.getStatistic().getValue() ).doubleValue();
    assertEquals( d, d2, 0.0 );
  }
}
