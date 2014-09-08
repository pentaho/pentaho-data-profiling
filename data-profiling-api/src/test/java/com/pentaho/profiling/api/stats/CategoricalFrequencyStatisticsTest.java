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

package com.pentaho.profiling.api.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.pentaho.profiling.api.stats.CategoricalFrequencyStatistics.CategoryHolder;

public class CategoricalFrequencyStatisticsTest {

  @Test
  public void testConstructor() {
    CategoricalFrequencyStatistics stats = new CategoricalFrequencyStatistics();
    assertEquals( Statistic.Metric.FREQUENCY_DISTRIBUTION.toString(), stats.getName() );
    assertTrue( stats.getValue() != null );
    assertTrue( stats.getValue() instanceof TreeMap );

    assertEquals( CategoricalFrequencyStatistics.DEFAULT_MAX_CATEGORIES, stats.getMaxCategories() );

    stats = new CategoricalFrequencyStatistics( 50 );
    assertEquals( 50, stats.getMaxCategories() );

    Map<String, CategoryHolder> initialCategories = new HashMap<String, CategoryHolder>();
    initialCategories.put( "cat1", new CategoryHolder( "cat1", 10 ) );
    initialCategories.put( "cat2", new CategoryHolder( "cat2", 100 ) );
    initialCategories.put( "cat3", new CategoryHolder( "cat2", 1 ) );

    stats = new CategoricalFrequencyStatistics( initialCategories, 50 );
    assertTrue( stats.getDistribution() != null );
    assertEquals( 3, stats.getDistribution().size() );

    stats = new CategoricalFrequencyStatistics( initialCategories, 2 );
    assertTrue( stats.getDistribution() != null );
    assertEquals( 2, stats.getDistribution().size() );

  }

  @Test
  public void testAddOrUpdate() {
    CategoricalFrequencyStatistics stats = new CategoricalFrequencyStatistics();

    assertEquals( 0, stats.numCategories() );

    stats.addOrUpdateCountForCategory( "a label", 10 );
    assertEquals( 1, stats.numCategories() );
    assertEquals( 10, stats.countForCategory( "a label" ) );

    stats.addOrUpdateCountForCategory( "a label", 5 );
    assertEquals( 1, stats.numCategories() );
    assertEquals( 15, stats.countForCategory( "a label" ) );

    stats.addOrUpdateCountForCategory( "label 2", 1 );
    assertEquals( 2, stats.numCategories() );
    assertEquals( 1, stats.countForCategory( "label 2" ) );
  }

  @Test
  public void testTrackTheTopNMostFrequent() {

    // track the top 3
    CategoricalFrequencyStatistics stats = new CategoricalFrequencyStatistics( 3 );
    stats.addOrUpdateCountForCategory( "label 1", 10 );
    stats.addOrUpdateCountForCategory( "label 2", 1 );
    stats.addOrUpdateCountForCategory( "label 3", 5 );

    assertEquals( 3, stats.numCategories() );

    stats.addOrUpdateCountForCategory( "label 4", 2 );
    assertEquals( 3, stats.numCategories() );

    // we should have dropped tracking label 2
    assertEquals( -1, stats.countForCategory( "label 2" ) );

    assertEquals( 5, stats.countForCategory( "label 3" ) );
    assertEquals( 10, stats.countForCategory( "label 1" ) );
  }
}
