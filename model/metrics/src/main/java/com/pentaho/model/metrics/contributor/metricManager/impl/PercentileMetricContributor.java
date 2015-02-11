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

import com.clearspring.analytics.stream.quantile.TDigest;
import com.pentaho.profiling.api.metrics.MetricContributorUtils;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.NVL;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import com.pentaho.model.metrics.contributor.Constants;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.model.metrics.contributor.metricManager.NVLOperations;
import com.pentaho.profiling.api.MessageUtils;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.stats.Statistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by mhall on 27/01/15.
 */
public class PercentileMetricContributor implements MetricManagerContributor {
  /**
   * Default compression for TDigest quantile estimators
   */
  public static final double Q_COMPRESSION = 50.0;

  public static final String KEY_PATH =
      MessageUtils.getId( Constants.KEY, PercentileMetricContributor.class );

  public static final String PERCENTILE_FIRSTQUARTILE_LABEL = "PercentileMetricContributor.25thPercentile";
  public static final String PERCENTILE_MEDIAN_LABEL = "PercentileMetricContributor.Median";
  public static final String PERCENTILE_THIRDQUARTILE_LABEL = "PercentileMetricContributor.75thPercentile";

  public static final String[] PERCENTILE_PATH_25 =
    new String[] { MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_25" };
  public static final String[] PERCENTILE_PATH_50 =
    new String[] { MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_50" };
  public static final String[] PERCENTILE_PATH_75 =
    new String[] { MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_75" };
  public static final String[] PERCENTILE_PATH_ESTIMATOR =
    new String[] { MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_estimator" };

  public static final List<String[]> CLEAR_PATH = new ArrayList<String[]>(
      Arrays.asList( PERCENTILE_PATH_25, PERCENTILE_PATH_50, PERCENTILE_PATH_75, PERCENTILE_PATH_ESTIMATOR ) );

  public static final ProfileFieldProperty
      PERCENTILE_FIRSTQUARTILE =
      MetricContributorUtils
      .createMetricProperty( KEY_PATH, PERCENTILE_FIRSTQUARTILE_LABEL, MetricContributorUtils.STATISTICS,
        Statistic.PERCENTILE + "_25" );
  public static final ProfileFieldProperty
      PERCENTILE_MEDIAN =
      MetricContributorUtils.createMetricProperty( KEY_PATH, PERCENTILE_MEDIAN_LABEL, MetricContributorUtils.STATISTICS,
      Statistic.PERCENTILE + "_50" );
  public static final ProfileFieldProperty
      PERCENTILE_THIRDQUARTILE =
      MetricContributorUtils
      .createMetricProperty( KEY_PATH, PERCENTILE_THIRDQUARTILE_LABEL, MetricContributorUtils.STATISTICS,
        Statistic.PERCENTILE + "_75" );

  private final NVL nvl;

  public PercentileMetricContributor() {
    this( new NVL() );
  }

  public PercentileMetricContributor( NVL nvl ) {
    this.nvl = nvl;
  }

  private static void setDerived( DataSourceMetricManager metricsForFieldType, TDigest digest ) {
    Long count = metricsForFieldType.getValueNoDefault( MetricContributorUtils.COUNT );
    if ( count >= 5 ) {
      metricsForFieldType
        .setValue( digest.quantile( 0.25 ), MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_25" );
      metricsForFieldType
        .setValue( digest.quantile( 0.5 ), MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_50" );
      metricsForFieldType
        .setValue( digest.quantile( 0.75 ), MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_75" );
    }
  }

  @Override public Set<String> getTypes() {
    return NumericMetricContributor.getTypesStatic();
  }

  @Override public void clear( DataSourceMetricManager dataSourceMetricManager ) {
    dataSourceMetricManager.clear( CLEAR_PATH );
  }

  @Override
  public void process( DataSourceMetricManager metricsForFieldType, DataSourceFieldValue dataSourceFieldValue )
    throws ProfileActionException {
    double value = ( (Number) dataSourceFieldValue.getFieldValue() ).doubleValue();

    TDigest digest = metricsForFieldType.getValueNoDefault( MetricContributorUtils.STATISTICS,
        Statistic.PERCENTILE + "_estimator" );

    if ( digest == null ) {
      digest = new TDigest( Q_COMPRESSION );
      metricsForFieldType.setValue( digest, MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_estimator" );
    }

    digest.add( value );
    setDerived( metricsForFieldType, digest );
  }

  @Override public void merge( DataSourceMetricManager into, DataSourceMetricManager from )
    throws MetricMergeException {
    TDigest tDigest = nvl.performAndSet( NVLOperations.TDIGEST_MERGE, into, from, MetricContributorUtils.STATISTICS,
        Statistic.PERCENTILE + "_estimator" );
    setDerived( into, tDigest );
  }

  public List<ProfileFieldProperty> getProfileFieldProperties() {
    return Arrays.asList( PERCENTILE_FIRSTQUARTILE, PERCENTILE_MEDIAN, PERCENTILE_THIRDQUARTILE );
  }
}
