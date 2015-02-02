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

package com.pentaho.model.metrics.contributor.impl;

import com.pentaho.metrics.api.MetricContributorUtils;
import com.pentaho.metrics.api.field.DataSourceField;
import com.pentaho.metrics.api.field.DataSourceFieldManager;
import com.pentaho.metrics.api.field.DataSourceFieldValue;
import com.pentaho.metrics.api.field.DataSourceMetricManager;
import com.pentaho.model.metrics.contributor.AbstractMetricContributor;
import com.pentaho.profiling.api.MessageUtils;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileStatusMessage;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.stats.Statistic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Metric contributor that computes a standard set of numeric metrics - sum, mean, min, max, sum of squares, variance
 * and standard deviation
 * <p/>
 * Created by mhall on 23/01/15.
 */
public class NumericMetricContributor extends AbstractMetricContributor {

  public static final String KEY_PATH = MessageUtils.getId( KEY, NumericMetricContributor.class );

  public static final ProfileFieldProperty
      MIN =
      MetricContributorUtils.createMetricProperty( KEY_PATH, "Min", MetricContributorUtils.STATISTICS, Statistic.MIN );
  public static final ProfileFieldProperty
      MAX =
      MetricContributorUtils.createMetricProperty( KEY_PATH, "Max", MetricContributorUtils.STATISTICS, Statistic.MAX );
  public static final ProfileFieldProperty
      MEAN =
      MetricContributorUtils
          .createMetricProperty( KEY_PATH, "Mean", MetricContributorUtils.STATISTICS, Statistic.MEAN );
  public static final ProfileFieldProperty
      STD_DEV =
      MetricContributorUtils.createMetricProperty( KEY_PATH, "StandardDeviation", MetricContributorUtils.STATISTICS,
          Statistic.STANDARD_DEVIATION );
  public static final ProfileFieldProperty
      SUM =
      MetricContributorUtils.createMetricProperty( KEY_PATH, "Sum", MetricContributorUtils.STATISTICS, Statistic.SUM );
  public static final ProfileFieldProperty
      SUM_OF_SQ =
      MetricContributorUtils.createMetricProperty( KEY_PATH, "SumOfSquares", MetricContributorUtils.STATISTICS,
          Statistic.SUM_OF_SQUARES );
  public static final ProfileFieldProperty
      VARIANCE =
      MetricContributorUtils
          .createMetricProperty( KEY_PATH, "Variance", MetricContributorUtils.STATISTICS, Statistic.VARIANCE );

  /**
   * Get a list of field properties for the metrics computed by this metric contributor
   *
   * @return a list of field properties
   */
  public static List<ProfileFieldProperty> getProfileFieldPropertiesStatic() {
    return Arrays.asList( MIN, MAX, MEAN, STD_DEV, SUM, SUM_OF_SQ, VARIANCE );
  }

  /**
   * Updates the metrics for a particular field type given a new data value
   *
   * @param metricsForFieldType the metric manager for the type to update
   * @param value               the field value to update with
   */
  public static void updateType( DataSourceMetricManager metricsForFieldType, double value ) {
    Double existingMinStat = metricsForFieldType.getValue( value, MetricContributorUtils.STATISTICS, Statistic.MIN );
    metricsForFieldType
        .setValue( Math.min( value, existingMinStat ), MetricContributorUtils.STATISTICS, Statistic.MIN );

    Double existingMaxStat = metricsForFieldType.getValue( value, MetricContributorUtils.STATISTICS, Statistic.MAX );
    metricsForFieldType
        .setValue( Math.max( value, existingMaxStat ), MetricContributorUtils.STATISTICS, Statistic.MAX );

    Double existingSumStat = metricsForFieldType.getValue( 0D, MetricContributorUtils.STATISTICS, Statistic.SUM );
    Double newSumStat = value + existingSumStat;
    metricsForFieldType.setValue( newSumStat, MetricContributorUtils.STATISTICS, Statistic.SUM );

    Double
        existingSumSqStat =
        metricsForFieldType.getValue( 0D, MetricContributorUtils.STATISTICS, Statistic.SUM_OF_SQUARES );
    Double newSumSqStat = ( value * value ) + existingSumSqStat;
    metricsForFieldType.setValue( newSumSqStat, MetricContributorUtils.STATISTICS, Statistic.SUM_OF_SQUARES );

    Long count = metricsForFieldType.getValueNoDefault( MetricContributorUtils.COUNT );

    // derived
    metricsForFieldType.setValue( newSumStat / count, MetricContributorUtils.STATISTICS, Statistic.MEAN );

    if ( count > 1 ) {
      Double variance = newSumSqStat - ( newSumStat * newSumStat ) / count;
      variance = variance / ( count - 1L );

      Double stdDev = variance;
      if ( !Double.isNaN( stdDev ) ) {
        stdDev = Math.sqrt( stdDev );
      }

      metricsForFieldType.setValue( variance, MetricContributorUtils.STATISTICS, Statistic.VARIANCE );
      metricsForFieldType.setValue( stdDev, MetricContributorUtils.STATISTICS, Statistic.STANDARD_DEVIATION );
    }
  }

  /**
   * Get a list of metrics computed by this contributor
   *
   * @return a list of metrics
   */
  public static List<String[]> getClearPaths() {
    List<String[]>
        paths =
        Arrays.<String[]>asList( new String[] { MetricContributorUtils.STATISTICS, Statistic.MIN },
            new String[] { MetricContributorUtils.STATISTICS, Statistic.MAX },
            new String[] { MetricContributorUtils.STATISTICS, Statistic.SUM },
            new String[] { MetricContributorUtils.STATISTICS, Statistic.SUM_OF_SQUARES },
            new String[] { MetricContributorUtils.STATISTICS, Statistic.MEAN },
            new String[] { MetricContributorUtils.STATISTICS, Statistic.VARIANCE },
            new String[] { MetricContributorUtils.STATISTICS, Statistic.STANDARD_DEVIATION } );

    return paths;
  }

  /**
   * Get a map (keyed by data type) of metrics
   *
   * @return a map (keyed by data type) of metrics
   */
  @Override protected Map<String, List<String[]>> getClearMap() {
    Map<String, List<String[]>> result = new HashMap<String, List<String[]>>();
    List<String[]> paths = getClearPaths();

    result.put( Integer.class.getCanonicalName(), paths );
    result.put( Long.class.getCanonicalName(), paths );
    result.put( Float.class.getCanonicalName(), paths );
    result.put( Double.class.getCanonicalName(), paths );

    return result;
  }

  /**
   * Process a field value
   *
   * @param manager    the data source field manager in use
   * @param fieldValue the value of the field, along with any metadata, to process
   * @throws ProfileActionException if a problem occurs
   */
  @Override public void processField( DataSourceFieldManager manager, DataSourceFieldValue fieldValue )
      throws ProfileActionException {

    if ( !(Boolean) fieldValue.getFieldMetadata( DataSourceFieldValue.LEAF ) || !( fieldValue
        .getFieldValue() instanceof Number ) ) {
      return;
    }
    String path = fieldValue.getFieldMetadata( DataSourceFieldValue.PATH );

    DataSourceField dataSourceField = manager.getPathToDataSourceFieldMap().get( path );

    if ( dataSourceField == null ) {
      // throw an exception here because the appropriate preprocessor
      // should have created/updated a DataSourceField
      throw new ProfileActionException(
          new ProfileStatusMessage( KEY_PATH, MetricContributorUtils.FIELD_NOT_FOUND + path, null ), null );
    }

    DataSourceMetricManager
        metricManager =
        dataSourceField.getMetricManagerForType( fieldValue.getFieldValue().getClass().getCanonicalName() );

    if ( metricManager == null ) {
      // throw an exception here because the appropriate preprocessor
      // should have created a type entry for the type of this field value
      throw new ProfileActionException( new ProfileStatusMessage( KEY_PATH,
          "Was expecting type " + fieldValue.getFieldValue().getClass().getCanonicalName() + " to exist.", null ),
          null );
    }

    double value = ( (Number) fieldValue.getFieldValue() ).doubleValue();
    updateType( metricManager, value );
  }

  public List<ProfileFieldProperty> getProfileFieldProperties() {
    return getProfileFieldPropertiesStatic();
  }
}
