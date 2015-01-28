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

import com.clearspring.analytics.stream.quantile.TDigest;
import com.pentaho.metrics.api.ComplexMetricEstimatorHolder;
import com.pentaho.metrics.api.MetricContributorUtils;
import com.pentaho.metrics.api.TDigestComplexMetricEstimatorHolder;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mhall on 27/01/15.
 */
public class PercentileMetricContributor extends AbstractMetricContributor {

  /**
   * Default compression for TDigest quantile estimators
   */
  public static final double Q_COMPRESSION = 50.0;

  public static final String KEY_PATH = MessageUtils.getId( KEY, PercentileMetricContributor.class );

  public static final String PERCENTILE_FIRSTQUARTILE_LABEL = "25thPercentile";
  public static final String PERCENTILE_MEDIAN_LABEL = "Median";
  public static final String PERCENTILE_THIRDQUARTILE_LABEL = "75thPercentile";

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

  public static void updateType( DataSourceMetricManager metricsForFieldType, double value ) throws IOException {
    Long count = metricsForFieldType.getValueNoDefault( MetricContributorUtils.COUNT );

    ComplexMetricEstimatorHolder
        estimatorHolder =
        metricsForFieldType
            .getComplexMetricEstimatorHolder( MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_estimator" );

    if ( estimatorHolder == null ) {
      // look for a serialized form in the regular collection
      byte[]
          serializedEstimator =
          metricsForFieldType
              .getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_estimator" );

      if ( serializedEstimator == null ) {
        TDigest digest = new TDigest( Q_COMPRESSION );
        estimatorHolder = new TDigestComplexMetricEstimatorHolder( digest );
      } else {
        estimatorHolder = new TDigestComplexMetricEstimatorHolder( serializedEstimator );
      }
      metricsForFieldType.setComplexMetricEstimatorHolder( estimatorHolder, MetricContributorUtils.STATISTICS,
          Statistic.PERCENTILE + "_estimator" );
    }

    TDigest digest = estimatorHolder.getEstimator();
    digest.add( value );

    if ( count >= 5 ) {
      metricsForFieldType
          .setValue( digest.quantile( 0.25 ), MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_25" );
      metricsForFieldType
          .setValue( digest.quantile( 0.5 ), MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_50" );
      metricsForFieldType
          .setValue( digest.quantile( 0.75 ), MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_75" );
    }
  }

  @Override protected Map<String, List<String[]>> getClearMap() {
    Map<String, List<String[]>> result = new HashMap<String, List<String[]>>();
    List<String[]>
        paths =
        Arrays.<String[]>asList( new String[] { MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_25" },
            new String[] { MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_50" },
            new String[] { MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_75" } );

    result.put( Integer.class.getCanonicalName(), paths );
    result.put( Long.class.getCanonicalName(), paths );
    result.put( Float.class.getCanonicalName(), paths );
    result.put( Double.class.getCanonicalName(), paths );

    return result;
  }

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
    try {
      updateType( metricManager, value );
    } catch ( IOException e ) {
      throw new ProfileActionException( new ProfileStatusMessage( KEY_PATH, "Updating TDigest estimator failed", null ),
          e );
    }
  }

  public List<ProfileFieldProperty> getProfileFieldProperties() {
    return Arrays.asList( PERCENTILE_FIRSTQUARTILE, PERCENTILE_MEDIAN, PERCENTILE_THIRDQUARTILE );
  }
}
