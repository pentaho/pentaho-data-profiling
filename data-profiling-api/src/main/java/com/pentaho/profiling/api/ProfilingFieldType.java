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

package com.pentaho.profiling.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.pentaho.profiling.api.stats.Statistic;

/**
 * Encapsulates type information for a profiling field
 * 
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 */
public class ProfilingFieldType {

  public static enum Type {
    NUMBER( "number" ) {
      @Override
      public List<String> statisticsForType() {
        return Arrays.asList( Statistic.Metric.COUNT.toString(), Statistic.Metric.MIN.toString(),
            Statistic.Metric.MAX.toString(), Statistic.Metric.SUM.toString(), Statistic.Metric.SUMSQ.toString(),
            Statistic.Metric.VARIANCE.toString(), Statistic.Metric.STDDEV.toString(),
            Statistic.Metric.MEDIAN.toString(), Statistic.Metric.TH_PERCENTILE.toString(),
            Statistic.Metric.SKEWNESS.toString(), Statistic.Metric.KURTOSIS.toString() );
      }
    },
    STRING( "string" ) {
      @Override
      public List<String> statisticsForType() {
        return Arrays.asList( Statistic.Metric.COUNT.toString(), Statistic.Metric.STRING_LENGTH.toString(),
            Statistic.Metric.MIN_WORDS.toString(), Statistic.Metric.MAX_WORDS.toString(),
            Statistic.Metric.FREQUENCY_DISTRIBUTION.toString() );
      }
    },
    DATE( "date" ) {
      @Override
      public List<String> statisticsForType() {
        return Arrays.asList( Statistic.Metric.COUNT.toString(), Statistic.Metric.MIN.toString(),
            Statistic.Metric.MAX.toString() );
      }
    },
    BOOLEAN( "boolean" ) {
      @Override
      public List<String> statisticsForType() {
        return Arrays.asList( Statistic.Metric.COUNT.toString(), Statistic.Metric.FREQUENCY_DISTRIBUTION.toString() );
      }
    },
    // catch-all for anything that is not a number, string, date or boolean
    DEFAULT( "default" ) {
      @Override
      public List<String> statisticsForType() {
        return Arrays.asList( Statistic.Metric.COUNT.toString() );
      }
    };

    String tName;

    /**
     * Constructor
     * 
     * @param name
     *          the name of this type
     */
    Type( String name ) {
      tName = name;
    }

    @Override
    public String toString() {
      return tName;
    }

    /**
     * Get a list of the names of the statistics that can be computed for this type
     * 
     * @return a list of the names of the statistics that can be computed for this type
     */
    public abstract List<String> statisticsForType();
  }

  /** The type name */
  protected String typeName = "unknown";

  /** The type (or closest equivalent) */
  protected Type type = Type.DEFAULT;

  /** The statistics for this type */
  protected Map<String, Statistic> stats = new LinkedHashMap<String, Statistic>();

  /**
   * Constructor
   */
  public ProfilingFieldType() {
  }

  /**
   * Constructor
   * 
   * @param typeName
   *          the name of the type to create
   */
  public ProfilingFieldType( String typeName ) {
    this.typeName = typeName;
    type = typeForName( typeName );
  }

  /**
   * Get the Type corresponding to the supplied typeName. Returns Type.DEFAULT for any unknown type
   * 
   * @param name
   *          the name of the type
   * @return the corresponding Type object
   */
  public static Type typeForName( String name ) {

    Type theType = Type.DEFAULT;
    for ( Type t : Type.values() ) {
      if ( t.toString().equalsIgnoreCase( name ) ) {
        theType = t;
        break;
      }
    }

    return theType;
  }

  /**
   * Get a list of the names of applicable statistics for this type
   * 
   * @return a list of the names of applicable statistics for this type
   */
  public List<String> applicableStatistics() {
    return type.statisticsForType();
  }

  /**
   * Set the name of the type
   * 
   * @param typeName
   *          the name of the type
   */
  public void setTypeName( String typeName ) {
    this.typeName = typeName;
  }

  /**
   * Get the name of the type
   * 
   * @return the name of the type
   */
  public String getTypeName() {
    return this.typeName;
  }

  /**
   * Set the count (num occurrences) of this type
   * 
   * @param count
   *          the number of occurrences
   */
  public void setCount( long count ) {

    Statistic countStat = new Statistic( Statistic.Metric.COUNT.toString(), count );

    stats.put( countStat.getName(), countStat );
  }

  /**
   * Get the count (num occurrences) of this type
   * 
   * @return the number of occurrences
   */
  public long getCount() {
    return stats.get( Statistic.Metric.COUNT.toString() ) == null ? 0L : ( (Number) stats.get(
        Statistic.Metric.COUNT.toString() ).getValue() ).longValue();
  }

  /**
   * Adds a statistic to the map. Overwrites any existing statistic with the same name
   * 
   * @param stat
   *          the statistic to add
   */
  public void addStatistic( Statistic stat ) {
    stats.put( stat.getName(), stat );
  }

  /**
   * Get a named statistic for this type. Returns null if the statistic has not been seen for this type
   * 
   * @param statName
   *          the name of the statistic to return
   * @return the statistic or null if it hasn't been seen for this type
   */
  public Statistic getStatistic( String statName ) {
    return stats.get( statName );
  }

  /**
   * Get an immutable iterator over the statistics for this type
   * 
   * @return an iterator over the statistics associated with this type
   */
  public Iterator<Statistic> iterator() {
    List<Statistic> s = new ArrayList<Statistic>( stats.values() );
    return Collections.unmodifiableCollection( s ).iterator();
  }
}
