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
import com.pentaho.model.metrics.contributor.Constants;
import com.pentaho.model.metrics.contributor.metricManager.impl.metrics.HyperLogLogPlusHolder;
import com.pentaho.profiling.api.MessageUtils;
import com.pentaho.profiling.api.MutableProfileFieldValueType;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileFieldValueType;
import com.pentaho.profiling.api.ProfileStatusMessage;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricContributorUtils;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.stats.Statistic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mhall on 27/01/15.
 */
public class CardinalityMetricContributor extends BaseMetricManagerContributor implements MetricManagerContributor {
  public static final String KEY_PATH =
    MessageUtils.getId( Constants.KEY, CardinalityMetricContributor.class );
  public static final String SIMPLE_NAME = CardinalityMetricContributor.class.getSimpleName();
  public static final ProfileFieldProperty CARDINALITY =
    MetricContributorUtils.createMetricProperty( KEY_PATH, SIMPLE_NAME, SIMPLE_NAME, Statistic.CARDINALITY );
  private static final Logger LOGGER = LoggerFactory.getLogger( CardinalityMetricContributor.class );
  private static final Set<Class<?>> supportedTypes =
    Collections.unmodifiableSet( new HashSet<Class<?>>( Arrays.asList( Integer.class, Long.class,
      Float.class, Double.class, String.class, Boolean.class, Date.class, byte[].class ) ) );
  /**
   * Default precision for the "normal" mode of HyperLogLogPlus
   */
  public int normalPrecision = 12;
  /**
   * Default precision for the "sparse" mode of HyperLogLogPlus
   */
  public int sparsePrecision = 16;

  @Override public Set<String> supportedTypes() {
    Set<String> result = new HashSet<String>();
    for ( Class<?> clazz : supportedTypes ) {
      result.add( clazz.getCanonicalName() );
    }
    return result;
  }

  private HyperLogLogPlusHolder getOrCreateHyperLogLogPlusHolder(
    MutableProfileFieldValueType mutableProfileFieldValueType ) {
    HyperLogLogPlusHolder result =
      (HyperLogLogPlusHolder) mutableProfileFieldValueType.getValueTypeMetrics( SIMPLE_NAME );
    if ( result == null ) {
      result = new HyperLogLogPlusHolder( new HyperLogLogPlus( normalPrecision, sparsePrecision ) );
      mutableProfileFieldValueType.setValueTypeMetrics( SIMPLE_NAME, result );
    }
    return result;
  }

  @Override
  public void process( MutableProfileFieldValueType mutableProfileFieldValueType,
                       DataSourceFieldValue dataSourceFieldValue )
    throws ProfileActionException {
    try {
      getOrCreateHyperLogLogPlusHolder( mutableProfileFieldValueType ).offer( dataSourceFieldValue.getFieldValue() );
    } catch ( Exception e ) {
      throw new ProfileActionException( new ProfileStatusMessage( KEY_PATH, "Updating HyperLogLogPlus failed", null ),
        e );
    }
  }

  @Override public void merge( MutableProfileFieldValueType into, ProfileFieldValueType from )
    throws MetricMergeException {
    HyperLogLogPlusHolder fromMetrics = (HyperLogLogPlusHolder) from.getValueTypeMetrics( SIMPLE_NAME );
    if ( fromMetrics == null ) {
      LOGGER.debug( "Second field didn't have estimator." );
    } else {
      try {
        into.setValueTypeMetrics( SIMPLE_NAME, getOrCreateHyperLogLogPlusHolder( into ).merge( fromMetrics ) );
      } catch ( CardinalityMergeException e ) {
        throw new MetricMergeException( e.getMessage(), e );
      }
    }
  }

  @Override public void setDerived( MutableProfileFieldValueType mutableProfileFieldValueType )
    throws ProfileActionException {
    getOrCreateHyperLogLogPlusHolder( mutableProfileFieldValueType ).calculateCardinality();
  }

  public List<ProfileFieldProperty> profileFieldProperties() {
    return Arrays.asList( CARDINALITY );
  }

  public int getNormalPrecision() {
    return normalPrecision;
  }

  public void setNormalPrecision( int normalPrecision ) {
    this.normalPrecision = normalPrecision;
  }

  public int getSparsePrecision() {
    return sparsePrecision;
  }

  public void setSparsePrecision( int sparsePrecision ) {
    this.sparsePrecision = sparsePrecision;
  }

  //OperatorWrap isn't helpful for autogenerated methods
  //CHECKSTYLE:OperatorWrap:OFF
  @Override public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    CardinalityMetricContributor that = (CardinalityMetricContributor) o;

    if ( normalPrecision != that.normalPrecision ) {
      return false;
    }
    return sparsePrecision == that.sparsePrecision;

  }

  @Override public int hashCode() {
    int result = normalPrecision;
    result = 31 * result + sparsePrecision;
    return result;
  }

  @Override public String toString() {
    return "CardinalityMetricContributor{" +
      "normalPrecision=" + normalPrecision +
      ", sparsePrecision=" + sparsePrecision +
      "} " + super.toString();
  }
  //CHECKSTYLE:OperatorWrap:ON
}
