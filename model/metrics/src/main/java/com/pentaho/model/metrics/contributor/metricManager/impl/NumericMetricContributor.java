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
import com.pentaho.model.metrics.contributor.metricManager.impl.metrics.NumericHolder;
import com.pentaho.profiling.api.MessageUtils;
import com.pentaho.profiling.api.MutableProfileFieldValueType;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileFieldValueType;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricContributorUtils;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
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

  public static final String SIMPLE_NAME = NumericMetricContributor.class.getSimpleName();

  public static final String KEY_PATH =
    MessageUtils.getId( Constants.KEY, NumericMetricContributor.class );
  public static final ProfileFieldProperty MIN = MetricContributorUtils
    .createMetricProperty( KEY_PATH, "NumericMetricContributor.Min", SIMPLE_NAME,
      Statistic.MIN );
  public static final ProfileFieldProperty MAX = MetricContributorUtils
    .createMetricProperty( KEY_PATH, "NumericMetricContributor.Max", SIMPLE_NAME,
      Statistic.MAX );
  public static final ProfileFieldProperty MEAN = MetricContributorUtils
    .createMetricProperty( KEY_PATH, "NumericMetricContributor.Mean", SIMPLE_NAME,
      Statistic.MEAN );
  public static final ProfileFieldProperty STD_DEV = MetricContributorUtils
    .createMetricProperty( KEY_PATH, "NumericMetricContributor.StandardDeviation", SIMPLE_NAME,
      Statistic.STANDARD_DEVIATION );
  public static final ProfileFieldProperty SUM = MetricContributorUtils
    .createMetricProperty( KEY_PATH, "NumericMetricContributor.Sum", SIMPLE_NAME,
      Statistic.SUM );
  public static final ProfileFieldProperty SUM_OF_SQ = MetricContributorUtils
    .createMetricProperty( KEY_PATH, "NumericMetricContributor.SumOfSquares", SIMPLE_NAME,
      "sumOfSquars" );
  public static final ProfileFieldProperty VARIANCE = MetricContributorUtils
    .createMetricProperty( KEY_PATH, "NumericMetricContributor.Variance", SIMPLE_NAME,
      "variance" );

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

  private NumericHolder getOrCreateNumericHolder( MutableProfileFieldValueType mutableProfileFieldValueType ) {
    NumericHolder result = (NumericHolder) mutableProfileFieldValueType.getValueTypeMetrics( SIMPLE_NAME );
    if ( result == null ) {
      result = new NumericHolder();
      mutableProfileFieldValueType.setValueTypeMetrics( SIMPLE_NAME, result );
    }
    return result;
  }

  @Override public void setDerived( MutableProfileFieldValueType mutableProfileFieldValueType )
    throws ProfileActionException {
    getOrCreateNumericHolder( mutableProfileFieldValueType ).setDerived( mutableProfileFieldValueType.getCount() );
  }

  @Override public Set<String> supportedTypes() {
    return getTypesStatic();
  }

  @Override
  public void process( MutableProfileFieldValueType mutableProfileFieldValueType,
                       DataSourceFieldValue dataSourceFieldValue )
    throws ProfileActionException {
    processValue( mutableProfileFieldValueType, (Number) dataSourceFieldValue.getFieldValue() );
  }

  public void processValue( MutableProfileFieldValueType mutableProfileFieldValueType, Number numberValue ) {
    getOrCreateNumericHolder( mutableProfileFieldValueType ).offer( numberValue );
  }

  @Override public void merge( MutableProfileFieldValueType into, ProfileFieldValueType from )
    throws MetricMergeException {
    NumericHolder numericHolder = (NumericHolder) from.getValueTypeMetrics( SIMPLE_NAME );
    if ( numericHolder != null ) {
      getOrCreateNumericHolder( into ).offer( numericHolder );
    }
  }

  public List<ProfileFieldProperty> profileFieldProperties() {
    return getProfileFieldPropertiesStatic();
  }
}
