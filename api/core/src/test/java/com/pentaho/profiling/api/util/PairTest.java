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
