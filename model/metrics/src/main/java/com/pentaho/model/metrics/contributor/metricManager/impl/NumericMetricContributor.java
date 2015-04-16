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

package com.pentaho.model.metrics.contributor.metricManager.impl;

import com.pentaho.model.metrics.contributor.Constants;
import com.pentaho.model.metrics.contributor.metricManager.NVLOperations;
import com.pentaho.profiling.api.MessageUtils;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricContributorUtils;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.NVL;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import com.pentaho.profiling.api.stats.Statistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Metric contributor that computes a standard set of numeric metrics - sum, mean, min, max, sum of squares, variance
 * and standard deviation
 * <p/>
 * Created by mhall on 23/01/15.
 */
public class NumericMetricContributor extends BaseMetricManagerContributor implements MetricManagerContributor {
  public static final String[] MIN_PATH = new String[] { MetricContributorUtils.STATISTICS, Statistic.MIN };
  public static final String[] MAX_PATH = new String[] { MetricContributorUtils.STATISTICS, Statistic.MAX };
  public static final List<String[]> CLEAR_PATHS =
    new ArrayList<String[]>( Arrays.<String[]>asList( new String[] { MetricContributorUtils.STATISTICS, Statistic.MIN },
      new String[] { MetricContributorUtils.STATISTICS, Statistic.MAX },
      new String[] { MetricContributorUtils.STATISTICS, Statistic.SUM },
      new String[] { MetricContributorUtils.STATISTICS, Statistic.SUM_OF_SQUARES },
      new String[] { MetricContributorUtils.STATISTICS, Statistic.MEAN },
      new String[] { MetricContributorUtils.STATISTICS, Statistic.VARIANCE },
      new String[] { MetricContributorUtils.STATISTICS, Statistic.STANDARD_DEVIATION } ) );

  public static final String KEY_PATH =
    MessageUtils.getId( Constants.KEY, NumericMetricContributor.class );

  public static final ProfileFieldProperty MIN = MetricContributorUtils
    .createMetricProperty( KEY_PATH, "NumericMetricContributor.Min", MetricContributorUtils.STATISTICS,
      Statistic.MIN );
  public static final ProfileFieldProperty MAX = MetricContributorUtils
    .createMetricProperty( KEY_PATH, "NumericMetricContributor.Max", MetricContributorUtils.STATISTICS,
      Statistic.MAX );
  public static final ProfileFieldProperty MEAN = MetricContributorUtils
    .createMetricProperty( KEY_PATH, "NumericMetricContributor.Mean", MetricContributorUtils.STATISTICS,
      Statistic.MEAN );
  public static final ProfileFieldProperty STD_DEV = MetricContributorUtils
    .createMetricProperty( KEY_PATH, "NumericMetricContributor.StandardDeviation", MetricContributorUtils.STATISTICS,
      Statistic.STANDARD_DEVIATION );
  public static final ProfileFieldProperty SUM = MetricContributorUtils
    .createMetricProperty( KEY_PATH, "NumericMetricContributor.Sum", MetricContributorUtils.STATISTICS,
      Statistic.SUM );
  public static final ProfileFieldProperty SUM_OF_SQ = MetricContributorUtils
    .createMetricProperty( KEY_PATH, "NumericMetricContributor.SumOfSquares", MetricContributorUtils.STATISTICS,
      Statistic.SUM_OF_SQUARES );
  public static final ProfileFieldProperty VARIANCE = MetricContributorUtils
    .createMetricProperty( KEY_PATH, "NumericMetricContributor.Variance", MetricContributorUtils.STATISTICS,
      Statistic.VARIANCE );

  private final NVL nvl;

  public NumericMetricContributor() {
    this( new NVL() );
  }

  public NumericMetricContributor( NVL nvl ) {
    this.nvl = nvl;
  }

  /**
   * Get a list of field properties for the metrics computed by this metric contributor
   *
   * @return a list of field properties
   */
  public static List<ProfileFieldProperty> getProfileFieldPropertiesStatic() {
    return Arrays.asList( MIN, MAX, MEAN, STD_DEV, SUM, SUM_OF_SQ, VARIANCE );
  }

  public static Set<String> getTypesStatic() {
    return new HashSet<String>( Arrays
      .asList( Integer.class.getCanonicalName(), Long.class.getCanonicalName(), Float.class.getCanonicalName(),
        Double.class.getCanonicalName() ) );
  }

  @Override public void setDerived( DataSourceMetricManager dataSourceMetricManager ) throws ProfileActionException {
    Number countNumber = dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.COUNT );
    long count = countNumber.longValue();

    Number newSumStat = dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.SUM );

    Number newSumSqStat = dataSourceMetricManager.getValueNoDefault( MetricContributorUtils.STATISTICS,
      Statistic.SUM_OF_SQUARES );
    // derived
    double newSumStatDouble = newSumStat.doubleValue();
    dataSourceMetricManager.setValue( newSumStatDouble / count, MetricContributorUtils.STATISTICS, Statistic.MEAN );

    if ( count > 1 ) {
      Double variance = newSumSqStat.doubleValue() - ( newSumStatDouble * newSumStatDouble ) / count;
      variance = variance / ( count - 1L );

      Double stdDev = variance;
      if ( !Double.isNaN( stdDev ) ) {
        stdDev = Math.sqrt( stdDev );
      }

      dataSourceMetricManager.setValue( variance, MetricContributorUtils.STATISTICS, Statistic.VARIANCE );
      dataSourceMetricManager.setValue( stdDev, MetricContributorUtils.STATISTICS, Statistic.STANDARD_DEVIATION );
    }
  }

  @Override public Set<String> supportedTypes() {
    return getTypesStatic();
  }

  @Override
  public void process( DataSourceMetricManager metricsForFieldType, DataSourceFieldValue dataSourceFieldValue )
    throws ProfileActionException {
    processValue( metricsForFieldType, (Number) dataSourceFieldValue.getFieldValue() );
  }

  public void processValue( DataSourceMetricManager metricsForFieldType, Number numberValue ) {
    double value = numberValue.doubleValue();
    nvl.performAndSet( NVLOperations.DOUBLE_MIN, metricsForFieldType, value, MetricContributorUtils.STATISTICS,
      Statistic.MIN );

    nvl.performAndSet( NVLOperations.DOUBLE_MAX, metricsForFieldType, value, MetricContributorUtils.STATISTICS,
      Statistic.MAX );

    nvl.performAndSet( NVLOperations.DOUBLE_SUM, metricsForFieldType, value,
      MetricContributorUtils.STATISTICS, Statistic.SUM );

    nvl.performAndSet( NVLOperations.DOUBLE_SUM, metricsForFieldType, value * value,
      MetricContributorUtils.STATISTICS, Statistic.SUM_OF_SQUARES );
  }

  @Override public void merge( DataSourceMetricManager into, DataSourceMetricManager from )
    throws MetricMergeException {
    nvl.performAndSet( NVLOperations.DOUBLE_MIN, into, from, MetricContributorUtils.STATISTICS,
      Statistic.MIN );
    nvl.performAndSet( NVLOperations.DOUBLE_MAX, into, from, MetricContributorUtils.STATISTICS,
      Statistic.MAX );
    Number newSumStat = nvl.performAndSet( NVLOperations.DOUBLE_SUM, into, from, MetricContributorUtils.STATISTICS,
      Statistic.SUM );
    Number newSumSqStat =
      nvl.performAndSet( NVLOperations.DOUBLE_SUM, into, from, MetricContributorUtils.STATISTICS,
        Statistic.SUM_OF_SQUARES );
  }

  @Override public void clear( DataSourceMetricManager dataSourceMetricManager ) {
    dataSourceMetricManager.clear( CLEAR_PATHS );
  }

  public List<ProfileFieldProperty> profileFieldProperties() {
    return getProfileFieldPropertiesStatic();
  }

  @Override public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    NumericMetricContributor that = (NumericMetricContributor) o;

    return !( nvl != null ? !nvl.equals( that.nvl ) : that.nvl != null );

  }

  @Override public int hashCode() {
    return nvl != null ? nvl.hashCode() : 0;
  }

  @Override public String toString() {
    return "NumericMetricContributor{}";
  }
}
