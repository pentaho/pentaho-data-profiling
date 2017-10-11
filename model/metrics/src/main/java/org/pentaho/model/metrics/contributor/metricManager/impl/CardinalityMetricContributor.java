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

import com.clearspring.analytics.stream.cardinality.CardinalityMergeException;
import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import org.pentaho.model.metrics.contributor.Constants;
import org.pentaho.model.metrics.contributor.metricManager.impl.metrics.HyperLogLogPlusHolder;
import org.pentaho.profiling.api.MessageUtils;
import org.pentaho.profiling.api.MutableProfileFieldValueType;
import org.pentaho.profiling.api.ProfileFieldProperty;
import org.pentaho.profiling.api.ProfileFieldValueType;
import org.pentaho.profiling.api.ProfileStatusMessage;
import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.metrics.MetricContributorUtils;
import org.pentaho.profiling.api.metrics.MetricManagerContributor;
import org.pentaho.profiling.api.metrics.MetricMergeException;
import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.pentaho.profiling.api.stats.Statistic;
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
