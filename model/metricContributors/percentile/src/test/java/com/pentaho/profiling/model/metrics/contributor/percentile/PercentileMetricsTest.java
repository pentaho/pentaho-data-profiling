/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
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

package com.pentaho.profiling.model.metrics.contributor.percentile;

import com.clearspring.analytics.stream.quantile.TDigest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 * @version $Revision: $
 */
public class PercentileMetricsTest {

  @Test( expected = NullPointerException.class ) public void testEmpty() {
    PercentileMetrics holder = new PercentileMetrics();
    holder.getBytes();
  }

  @Test public void testGetBytes() {
    TDigest digest = new TDigest( 50.0d );
    PercentileMetrics holder = new PercentileMetrics( digest );
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

    PercentileMetrics holder = new PercentileMetrics( digest );
    byte[] serialized = holder.getBytes();

    PercentileMetrics holder2 = new PercentileMetrics();
    holder2.setBytes( serialized );
    holder2.setPercentile( 0.5 );
    assertEquals( perc, holder2.getPercentiles().get( "0.5" ), 0.0001 );
  }

  @Test public void testClone() throws CloneNotSupportedException {
    PercentileMetrics holder = new PercentileMetrics();
    Object result = holder.clone();
    assertTrue( result != null );

    TDigest digest = new TDigest( 50.0d );
    holder = new PercentileMetrics( digest );
    holder.add( 5.0d );
    result = holder.clone();
    assertTrue( result != null );
  }

  @Test public void testEquals() {
    PercentileMetrics holder = new PercentileMetrics();
    assertTrue( holder.equals( holder ) );
    assertFalse( holder.equals( null ) );
    assertFalse( holder.equals( "string" ) );

    TDigest digest = new TDigest( 50.0d );
    digest.add( 5.0d );
    TDigest digest2 = new TDigest( 100.0d );
    digest2.add( 55.0d );
    PercentileMetrics holder2 = new PercentileMetrics( digest );
    PercentileMetrics holder3 = new PercentileMetrics( digest2 );
    assertFalse( holder.equals( holder2 ) );
    assertFalse( holder2.equals( holder ) );
    assertFalse( holder2.equals( holder3 ) );

    holder3 = new PercentileMetrics( digest );
    assertTrue( holder2.equals( holder3 ) );
  }

  @Test public void testHash() {
    PercentileMetrics holder = new PercentileMetrics();
    assertEquals( 0, holder.hashCode() );
    TDigest digest = new TDigest( 50.0d );
    digest.add( 5.0d );
    int hash = digest.hashCode();
    holder = new PercentileMetrics( digest );
    assertEquals( hash * 31, holder.hashCode() );
  }

  @Test public void testToString() {
    PercentileMetrics holder = new PercentileMetrics( new TDigest( 50.0d ) );
    assertTrue( holder.toString() != null );
  }
}
