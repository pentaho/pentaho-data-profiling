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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Holds frequency counts for categorical/string fields
 * 
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 */
public class CategoricalFrequencyStatistics extends Statistic {

  public static int DEFAULT_MAX_CATEGORIES = 100;

  protected int maxCategories = DEFAULT_MAX_CATEGORIES;

  protected List<CategoryHolder> sortedByCount;

  /**
   * Construct an initially empty CategoricalFrequencyStatistic with default maximum number of categories to track (100)
   * 
   * @param name
   *          the name of this statistic
   */
  public CategoricalFrequencyStatistics() {
    this( new TreeMap<String, CategoryHolder>(), DEFAULT_MAX_CATEGORIES );
  }

  /**
   * Construct an initially empty CategoricalFrequencyStatistics with specified maximum number of categories to track
   * 
   * @param maxCategories
   *          the number of categories to track
   */
  public CategoricalFrequencyStatistics( int maxCategories ) {
    this( new TreeMap<String, CategoryHolder>(), maxCategories );
  }

  /**
   * Construct a CategoricalFrequencyStatistics with the supplied map of category counts and maximum number of
   * categories to track
   * 
   * @param counts
   *          the map of values to counts
   * @param maxCategories
   *          the maximum number of categories to track
   */
  @SuppressWarnings( "unchecked" )
  public CategoricalFrequencyStatistics( Map<String, CategoryHolder> counts, int maxCategories ) {
    super( Statistic.Metric.FREQUENCY_DISTRIBUTION.toString(), new TreeMap<String, CategoryHolder>() );
    this.maxCategories = maxCategories;
    sortedByCount = new ArrayList<CategoryHolder>( maxCategories );

    for ( Map.Entry<String, CategoryHolder> e : counts.entrySet() ) {
      if ( sortedByCount.size() < maxCategories ) {
        sortedByCount.add( e.getValue() );
        ( (Map<String, CategoryHolder>) value ).put( e.getKey(), e.getValue() );
      }
    }
    Collections.sort( sortedByCount );
  }

  /**
   * Set the maximum number of categories to track
   * 
   * @param maxCategories
   *          the maximum number of categories to track
   */
  public void setMaxCategories( int maxCategories ) {
    this.maxCategories = maxCategories;
  }

  /**
   * Get the maximum number of categories to track
   * 
   * @return the maximum number of categories to track
   */
  public int getMaxCategories() {
    return this.maxCategories;
  }

  /**
   * Get the count of a particular category. Returns -1 if the category has not been seen
   * 
   * @param category
   *          the category to get the count for
   * @return the count for the named category
   */
  @SuppressWarnings( "unchecked" )
  public int countForCategory( String category ) {
    CategoryHolder label = ( (Map<String, CategoryHolder>) value ).get( category );
    return label == null ? -1 : label.getCount();
  }

  /**
   * Adds or updates the count of a category
   * 
   * @param category
   *          the category to add/update
   * @param count
   *          the count of the cateogry
   */
  @SuppressWarnings( "unchecked" )
  public void addOrUpdateCountForCategory( String category, int count ) {

    boolean reSort = false;
    CategoryHolder existing = ( (Map<String, CategoryHolder>) value ).get( category );
    if ( existing == null ) {
      if ( sortedByCount.size() < maxCategories || sortedByCount.get( 0 ).getCount() < count ) {

        existing = new CategoryHolder();
        existing.setLabel( category );
        existing.setCount( count );
        ( (Map<String, CategoryHolder>) value ).put( category, existing );
        sortedByCount.add( existing );

        if ( sortedByCount.size() > maxCategories ) {
          CategoryHolder removed = sortedByCount.remove( 0 );
          ( (Map<String, CategoryHolder>) value ).remove( removed.getLabel() );
        }
        reSort = true;
      }
    } else {
      existing.increment( count );
      reSort = true;
    }

    if ( reSort ) {
      Collections.sort( sortedByCount );
    }
  }

  /**
   * @return
   */
  protected String getLeastFrequentLabel() {
    return sortedByCount.get( 0 ).getLabel();
  }

  /**
   * Return the number of values in this distribution
   * 
   * @return the number of values in this distribution
   */
  @SuppressWarnings( "unchecked" )
  public int numCategories() {
    return ( (Map<String, CategoryHolder>) value ).size();
  }

  /**
   * Get the distribution of counts for the known categorical values. Convenience method that casts getValue() to a map.
   * 
   * @return the distribution as a map of counts keyed by category
   */
  @SuppressWarnings( "unchecked" )
  public Map<String, CategoryHolder> getDistribution() {
    return (Map<String, CategoryHolder>) getValue();
  }

  public static class CategoryHolder implements Comparable<CategoryHolder> {
    public String label;
    public int count;

    public CategoryHolder() {
    }

    public CategoryHolder( String label, int count ) {
      this.label = label;
      this.count = count;
    }

    public void setLabel( String label ) {
      this.label = label;
    }

    public String getLabel() {
      return this.label;
    }

    public void setCount( int count ) {
      this.count = count;
    }

    public int getCount() {
      return this.count;
    }

    public void increment( int inc ) {
      this.count += inc;
    }

    @Override
    public int compareTo( CategoryHolder other ) {
      return count < other.getCount() ? -1 : count > other.getCount() ? 1 : 0;
    }
  }
}
