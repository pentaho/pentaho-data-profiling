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
