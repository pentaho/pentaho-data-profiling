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

import com.clearspring.analytics.stream.cardinality.CardinalityMergeException;
import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import com.clearspring.analytics.stream.cardinality.ICardinality;
import com.pentaho.profiling.api.metrics.MetricContributorUtils;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.model.metrics.contributor.Constants;
import com.pentaho.profiling.api.MessageUtils;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileStatusMessage;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.stats.Statistic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mhall on 27/01/15.
 */
public class CardinalityMetricContributor implements MetricManagerContributor {
  /**
   * Default precision for the "normal" mode of HyperLogLogPlus
   */
  public static final int P_PRECISION = 12;
  /**
   * Default precision for the "sparse" mode of HyperLogLogPlus
   */
  public static final int SP_PRECISION = 16;
  public static final String[] CARDINALITY_PATH =
    new String[] { MetricContributorUtils.STATISTICS, Statistic.CARDINALITY };
  public static final String[] CARDINALITY_PATH_ESTIMATOR =
    new String[] { MetricContributorUtils.STATISTICS, Statistic.CARDINALITY + "_estimator" };
  public static final List<String[]> CLEAR_PATHS =
      new ArrayList<String[]>( Arrays.asList( CARDINALITY_PATH, CARDINALITY_PATH_ESTIMATOR ) );
  public static final String KEY_PATH =
      MessageUtils.getId( Constants.KEY, CardinalityMetricContributor.class );
  public static final String CARDINALITY_LABEL = "Cardinality";
  public static final ProfileFieldProperty CARDINALITY =
      MetricContributorUtils.createMetricProperty( KEY_PATH, CARDINALITY_LABEL, CARDINALITY_PATH );
  private static final Logger LOGGER = LoggerFactory.getLogger( CardinalityMetricContributor.class );
  private static final Set<Class<?>> supportedTypes =
      Collections.unmodifiableSet( new HashSet<Class<?>>( Arrays.asList( Integer.class, Long.class,
      Float.class, Double.class, String.class, Boolean.class, Date.class, byte[].class ) ) );

  @Override public Set<String> getTypes() {
    Set<String> result = new HashSet<String>();
    for ( Class<?> clazz : supportedTypes ) {
      result.add( clazz.getCanonicalName() );
    }
    return result;
  }

  public HyperLogLogPlus createHyperLogLogPlus() {
    return new HyperLogLogPlus( P_PRECISION, SP_PRECISION );
  }

  @Override
  public void process( DataSourceMetricManager dataSourceMetricManager, DataSourceFieldValue dataSourceFieldValue )
    throws ProfileActionException {
    Object value = dataSourceFieldValue.getFieldValue();
    try {
      ICardinality hllp = dataSourceMetricManager.getValueNoDefault( CARDINALITY_PATH_ESTIMATOR );
      if ( hllp == null ) {
        hllp = createHyperLogLogPlus();
        dataSourceMetricManager.setValue( hllp, CARDINALITY_PATH_ESTIMATOR );
      }
      hllp.offer( value );
      dataSourceMetricManager.setValue( hllp.cardinality(), CARDINALITY_PATH );
    } catch ( Exception e ) {
      throw new ProfileActionException( new ProfileStatusMessage( KEY_PATH, "Updating HyperLogLogPlus failed", null ),
        e );
    }
  }

  @Override public void merge( DataSourceMetricManager into, DataSourceMetricManager from )
    throws MetricMergeException {
    ICardinality originalEstimator = into.getValueNoDefault( CARDINALITY_PATH_ESTIMATOR );
    ICardinality secondEstimator = from.getValueNoDefault( CARDINALITY_PATH_ESTIMATOR );
    if ( originalEstimator == null ) {
      originalEstimator = secondEstimator;
    } else if ( secondEstimator == null ) {
      LOGGER.debug( "First field had estimator but second field didn't." );
    } else {
      try {
        originalEstimator = originalEstimator.merge( secondEstimator );
      } catch ( CardinalityMergeException e ) {
        throw new MetricMergeException( e.getMessage(), e );
      }
    }
    into.setValue( originalEstimator, CARDINALITY_PATH_ESTIMATOR );
    if ( originalEstimator != null ) {
      into.setValue( originalEstimator.cardinality(), CARDINALITY_PATH );
    }
  }

  @Override public void clear( DataSourceMetricManager dataSourceMetricManager ) {
    dataSourceMetricManager.clear( CLEAR_PATHS );
  }

  public List<ProfileFieldProperty> getProfileFieldProperties() {
    return Arrays.asList( CARDINALITY );
  }
}
