/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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

package org.pentaho.model.metrics.contributor.metricManager.impl;

import org.pentaho.model.metrics.contributor.Constants;
import org.pentaho.model.metrics.contributor.metricManager.impl.metrics.NumericHolder;
import org.pentaho.profiling.api.MessageUtils;
import org.pentaho.profiling.api.MutableProfileFieldValueType;
import org.pentaho.profiling.api.ProfileFieldProperty;
import org.pentaho.profiling.api.ProfileFieldValueType;
import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.metrics.MetricContributorUtils;
import org.pentaho.profiling.api.metrics.MetricManagerContributor;
import org.pentaho.profiling.api.metrics.MetricMergeException;
import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.pentaho.profiling.api.stats.Statistic;

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

  @Override public boolean equals( Object obj ) {
    return obj != null && obj.getClass().equals( NumericMetricContributor.class );
  }
}
