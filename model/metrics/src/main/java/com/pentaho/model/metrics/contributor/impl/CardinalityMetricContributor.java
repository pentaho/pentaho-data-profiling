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

import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import com.pentaho.metrics.api.ComplexMetricEstimatorHolder;
import com.pentaho.metrics.api.HyperLogLogPlusComplexMetricEstimatorHolder;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mhall on 27/01/15.
 */
public class CardinalityMetricContributor extends AbstractMetricContributor {

  /**
   * Default precision for the "normal" mode of HyperLogLogPlus
   */
  public static final int P_PRECISION = 12;

  /**
   * Default precision for the "sparse" mode of HyperLogLogPlus
   */
  public static final int SP_PRECISION = 16;

  public static final String KEY_PATH = MessageUtils.getId( KEY, CardinalityMetricContributor.class );

  public static final String CARDINALITY_LABEL = "Cardinality";

  public static final ProfileFieldProperty
      CARDINALITY =
      MetricContributorUtils.createMetricProperty( KEY_PATH, CARDINALITY_LABEL, MetricContributorUtils.STATISTICS,
          Statistic.CARDINALITY );

  public static void updateType( DataSourceMetricManager metricsForFieldType, Object value ) throws IOException {
    ComplexMetricEstimatorHolder
        estimatorHolder =
        metricsForFieldType
            .getComplexMetricEstimatorHolder( MetricContributorUtils.STATISTICS, Statistic.CARDINALITY + "_estimator" );

    if ( estimatorHolder == null ) {
      // look for a serialized form in the regular collection
      byte[]
          serializedEstimator =
          metricsForFieldType
              .getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.CARDINALITY + "_estimator" );

      if ( serializedEstimator == null ) {
        HyperLogLogPlus hllp = new HyperLogLogPlus( P_PRECISION, SP_PRECISION );
        estimatorHolder = new HyperLogLogPlusComplexMetricEstimatorHolder( hllp );
      } else {
        estimatorHolder = new HyperLogLogPlusComplexMetricEstimatorHolder( serializedEstimator );
      }
      metricsForFieldType.setComplexMetricEstimatorHolder( estimatorHolder, MetricContributorUtils.STATISTICS,
          Statistic.CARDINALITY + "_estimator" );
    }

    HyperLogLogPlus hllp = estimatorHolder.getEstimator();
    hllp.offer( value );
    metricsForFieldType.setValue( hllp.cardinality(), MetricContributorUtils.STATISTICS, Statistic.CARDINALITY );
  }

  @Override protected Map<String, List<String[]>> getClearMap() {
    Map<String, List<String[]>> result = new HashMap<String, List<String[]>>();
    List<String[]>
        paths =
        Arrays.<String[]>asList( new String[] { MetricContributorUtils.STATISTICS, Statistic.CARDINALITY } );

    result.put( Integer.class.getCanonicalName(), paths );
    result.put( Long.class.getCanonicalName(), paths );
    result.put( Float.class.getCanonicalName(), paths );
    result.put( Double.class.getCanonicalName(), paths );
    result.put( String.class.getCanonicalName(), paths );
    result.put( Boolean.class.getCanonicalName(), paths );
    result.put( Date.class.getCanonicalName(), paths );
    result.put( byte[].class.getCanonicalName(), paths );

    return result;
  }

  @Override public void processField( DataSourceFieldManager manager, DataSourceFieldValue fieldValue )
      throws ProfileActionException {

    if ( !(Boolean) fieldValue.getFieldMetadata( DataSourceFieldValue.LEAF ) || fieldValue == null
        || fieldValue.getFieldValue() == null ) {
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

    Object value = fieldValue.getFieldValue();
    try {
      updateType( metricManager, value );
    } catch ( IOException e ) {
      throw new ProfileActionException( new ProfileStatusMessage( KEY_PATH, "Updating HyperLogLogPlus failed", null ),
          e );
    }
  }

  public List<ProfileFieldProperty> getProfileFieldProperties() {
    return Arrays.asList( CARDINALITY );
  }
}
