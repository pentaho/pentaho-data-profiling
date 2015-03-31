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
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 * @version $Revision: $
 */
public class HyperLogLogPlusHolderTest {

  @Test( expected = NullPointerException.class ) public void testEmpty() {
    HyperLogLogPlusHolder holder = new HyperLogLogPlusHolder();
    holder.getBytes();
  }

  @Test( expected = NullPointerException.class ) public void testEmptyMerge() throws CardinalityMergeException {
    HyperLogLogPlusHolder holder = new HyperLogLogPlusHolder();
    HyperLogLogPlusHolder holder2 = new HyperLogLogPlusHolder();
    holder2.merge( holder );
  }

  @Test public void testNormal() {
    HyperLogLogPlus hyperLogLogPlus = new HyperLogLogPlus( 12, 16 );
    HyperLogLogPlusHolder holder = new HyperLogLogPlusHolder( hyperLogLogPlus );
    assertTrue( holder.getBytes() != null );
  }

  @Test public void testSetBytes() throws IOException {
    HyperLogLogPlus hyperLogLogPlus = new HyperLogLogPlus( 12, 16 );
    hyperLogLogPlus.offer( "String" );
    long card = hyperLogLogPlus.cardinality();
    byte[] bytes = hyperLogLogPlus.getBytes();
    HyperLogLogPlusHolder holder = new HyperLogLogPlusHolder();
    holder.setBytes( bytes );
    assertEquals( card, holder.cardinality() );
  }

  @Test public void testIOExceptionGetBytes() throws IOException {
    HyperLogLogPlus hyperLogLogPlus = mock( HyperLogLogPlus.class );
    HyperLogLogPlusHolder hyperLogLogPlusHolder = new HyperLogLogPlusHolder( hyperLogLogPlus );
    when( hyperLogLogPlus.getBytes() ).thenThrow( IOException.class );
    assertTrue( hyperLogLogPlusHolder.getBytes() == null );
  }

  @Test( expected = NullPointerException.class ) public void testIOExceptionSetBytes() {
    HyperLogLogPlusHolder holder = new HyperLogLogPlusHolder();
    holder.setBytes( new byte[0] );
    holder.getBytes();
  }

  @Test
  public void testClone() throws CloneNotSupportedException {
    HyperLogLogPlus hyperLogLogPlus = new HyperLogLogPlus( 12, 16 );
    HyperLogLogPlusHolder holder = new HyperLogLogPlusHolder( hyperLogLogPlus );
    holder.offer( "String1" );
    holder.offer( "String2" );
    long card = holder.cardinality();

    HyperLogLogPlusHolder cloned = (HyperLogLogPlusHolder) holder.clone();
    assertEquals( card, cloned.cardinality() );

    holder = new HyperLogLogPlusHolder(  );
    cloned = (HyperLogLogPlusHolder) holder.clone();
  }
}
