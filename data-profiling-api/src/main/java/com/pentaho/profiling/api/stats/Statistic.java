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

/**
 * Base class for statistics
 * 
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 */
public class Statistic {

  /** The name of this statistic */
  protected String name = "unset";

  /** The value of this statistic */
  protected Object value;

  public static enum Metric {

    COUNT( "Count" ), MIN( "Min" ), MAX( "Max" ), SUM( "Sum" ), SUMSQ( "Sum of squares" ), MEAN( "Mean" ), VARIANCE(
        "Variance" ), STDDEV( "Standard deviation" ), MEDIAN( "Median" ), TH_PERCENTILE( "th percentile" ), SKEWNESS(
        "Skewness" ), KURTOSIS( "Kurtosis" ), FREQUENCY_DISTRIBUTION( "Frequency distribution" ), STRING_LENGTH(
        "String length" ), MAX_WORDS( "Max words" ), MIN_WORDS( "Min words" );

    String sName;

    Metric( String name ) {
      sName = name;
    }

    @Override
    public String toString() {
      return sName;
    }
  }

  /**
   * Constructor
   */
  public Statistic() {

  }

  /**
   * Constructor
   * 
   * @param name
   *          the name of this statistic
   * @param value
   *          the value for this statistic
   */
  public Statistic( String name, Object value ) {
    this.name = name;
    this.value = value;
  }

  /**
   * Get the name of this statistic
   * 
   * @return the name of this statistic
   */
  public String getName() {
    return this.name;
  }

  /**
   * Set the name of this statistic
   * 
   * @param name
   *          the name of this statistic
   */
  public void setName( String name ) {
    this.name = name;
  }

  /**
   * Get the value of this statistic
   * 
   * @return the value of this statistic
   */
  public Object getValue() {
    return this.value;
  }

  /**
   * Set the value of this statistic
   * 
   * @param value
   *          the value of this statistic
   */
  public void setValue( Object value ) {
    this.value = value;
  }
}
