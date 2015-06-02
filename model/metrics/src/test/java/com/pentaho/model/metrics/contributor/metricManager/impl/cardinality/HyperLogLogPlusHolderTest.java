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

package org.pentaho.model.metrics.contributor.metricManager.impl.cardinality;

import com.clearspring.analytics.stream.cardinality.CardinalityMergeException;
import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import org.pentaho.model.metrics.contributor.metricManager.impl.metrics.HyperLogLogPlusHolder;
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
    holder.calculateCardinality();
    assertEquals( card, holder.getCardinality() );
  }

  @Test public void testIOExceptionGetBytes() throws IOException {
    HyperLogLogPlus hyperLogLogPlus = mock( HyperLogLogPlus.class );
    HyperLogLogPlusHolder hyperLogLogPlusHolder = new HyperLogLogPlusHolder( hyperLogLogPlus );
    when( hyperLogLogPlus.getBytes() ).thenThrow( IOException.class );
    assertTrue( hyperLogLogPlusHolder.getBytes() == null );
  }

  @Test( expected = NullPointerException.class ) public void testIOExceptionSetBytes() {
    HyperLogLogPlusHolder holder = new HyperLogLogPlusHolder();
    holder.setBytes( new byte[ 0 ] );
    holder.getBytes();
  }

  @Test
  public void testClone() throws CloneNotSupportedException {
    HyperLogLogPlus hyperLogLogPlus = new HyperLogLogPlus( 12, 16 );
    HyperLogLogPlusHolder holder = new HyperLogLogPlusHolder( hyperLogLogPlus );
    holder.offer( "String1" );
    holder.offer( "String2" );
    holder.calculateCardinality();
    long card = holder.getCardinality();

    HyperLogLogPlusHolder cloned = (HyperLogLogPlusHolder) holder.clone();
    assertEquals( card, cloned.getCardinality() );
  }
}
