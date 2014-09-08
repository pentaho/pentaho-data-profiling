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

public class CountValueProcessorTest {

  @Test
  public void testTypical() {
    CountValueProcessor p = new CountValueProcessor();

    assertTrue( p.getName() != null );
    assertEquals( Statistic.Metric.COUNT.toString(), p.getName() );
    assertEquals( 0L, ( (Long) p.getValue() ).longValue() );
  }

  @Test
  public void testProcess() throws Exception {
    CountValueProcessor p = new CountValueProcessor();
    assertEquals( 0L, ( (Number) p.getValue() ).longValue() );

    p.process( "bob" );
    p.process( new Integer( 10 ) );

    assertEquals( 2L, ( (Number) p.getValue() ).longValue() );

    p.process( null );
    assertEquals( 2L, ( (Number) p.getValue() ).longValue() );
  }

  @Test
  public void testAggregate() throws Exception {
    CountValueProcessor p = new CountValueProcessor();
    p.setCount( 5L );

    CountValueProcessor p2 = new CountValueProcessor();
    p2.setCount( 10L );

    p.aggregate( p2 );
    assertEquals( 15L, ( (Number) p.getValue() ).longValue() );
    assertEquals( 10L, ( (Number) p2.getValue() ).longValue() );
  }

  @Test
  public void testGetStatistic() throws Exception {
    CountValueProcessor p = new CountValueProcessor();
    p.setCount( 5L );

    assertTrue( p.getStatistic() != null );
    assertEquals( p.getName(), p.getStatistic().getName() );
    assertEquals( p.getValue(), p.getStatistic().getValue() );
  }
}
