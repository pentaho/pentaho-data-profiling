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

package com.pentaho.profiling.api.util;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by bryan on 2/18/15.
 */
public class PairTest {

  @Test
  public void testCreate() {
    String test1 = "test1";
    Integer test2 = 2442;
    Pair pair = Pair.of( test1, test2 );
    assertEquals( test1, pair.getFirst() );
    assertEquals( test2, pair.getSecond() );
    assertTrue( pair.toString().contains( test1 ) );
    assertTrue( pair.toString().contains( test2.toString() ) );
  }

  @Test
  public void testSet() {
    String test1 = "test";
    Integer test2 = new Integer( 2442 );
    Set<Pair<String, Integer>> pairs = new HashSet<Pair<String, Integer>>();
    pairs.add( Pair.of( test1, test2 ) );
    assertFalse( pairs.add( Pair.of( test1.substring( 0 ), new Integer( test2 ) ) ) );
  }
}
