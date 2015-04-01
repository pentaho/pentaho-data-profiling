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

package com.pentaho.model.metrics.contributor.metricManager.impl.percentile;

import com.clearspring.analytics.stream.quantile.TDigest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 * @version $Revision: $
 */
public class TDigestHolderTest {

  @Test( expected = NullPointerException.class ) public void testEmpty() {
    TDigestHolder holder = new TDigestHolder();
    holder.getBytes();
  }

  @Test public void testGetBytes() {
    TDigest digest = new TDigest( 50.0d );
    TDigestHolder holder = new TDigestHolder( digest );
    holder.add( 5.0d );
    assertTrue( holder.getBytes() != null );
  }

  @Test public void testSetBytes() {
    TDigest digest = new TDigest( 50.0d );
    digest.add( 2.25d );
    digest.add( 2.5d );
    digest.add( 2.75d );
    digest.add( 3.75d );
    digest.add( 4.75d );
    double perc = digest.quantile( 0.5d );

    TDigestHolder holder = new TDigestHolder( digest );
    byte[] serialized = holder.getBytes();

    TDigestHolder holder2 = new TDigestHolder();
    holder2.setBytes( serialized );
    assertEquals( perc, holder2.quantile( 0.5d ), 0.0001 );
  }

  @Test public void testClone() throws CloneNotSupportedException {
    TDigestHolder holder = new TDigestHolder();
    Object result = holder.clone();
    assertTrue( result != null );

    TDigest digest = new TDigest( 50.0d );
    holder = new TDigestHolder( digest );
    holder.add( 5.0d );
    result = holder.clone();
    assertTrue( holder != null );
  }

  @Test public void testEquals() {
    TDigestHolder holder = new TDigestHolder();
    assertTrue( holder.equals( holder ) );
    assertFalse( holder.equals( null ) );
    assertFalse( holder.equals( "string" ) );

    TDigest digest = new TDigest( 50.0d );
    digest.add( 5.0d );
    TDigest digest2 = new TDigest( 100.0d );
    digest2.add( 55.0d );
    TDigestHolder holder2 = new TDigestHolder( digest );
    TDigestHolder holder3 = new TDigestHolder( digest2 );
    assertFalse( holder.equals( holder2 ) );
    assertFalse( holder2.equals( holder ) );
    assertFalse( holder2.equals( holder3 ) );

    holder3 = new TDigestHolder( digest );
    assertTrue( holder2.equals( holder3 ) );
  }

  @Test public void testHash() {
    TDigestHolder holder = new TDigestHolder();
    assertEquals( 0, holder.hashCode() );
    TDigest digest = new TDigest( 50.0d );
    digest.add( 5.0d );
    int hash = digest.hashCode();
    holder = new TDigestHolder( digest );
    assertEquals( hash, holder.hashCode() );
  }

  @Test public void testToString() {
    TDigestHolder holder = new TDigestHolder( new TDigest( 50.0d ) );
    assertTrue( holder.toString() != null );
  }
}
