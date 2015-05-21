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

package com.pentaho.profiling.model.metrics.contributor.percentile;

import com.clearspring.analytics.stream.quantile.TDigest;
import com.pentaho.profiling.api.MessageUtils;
import com.pentaho.profiling.api.MutableProfileFieldValueType;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileFieldValueType;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mhall on 27/01/15.
 */
public class PercentileMetricContributor implements MetricManagerContributor {
  public static final String KEY = "profiling-metrics-contributors-percentile";
  public static final String KEY_PATH =
    MessageUtils.getId( KEY, PercentileMetricContributor.class );

  public static final String PERCENTILE_FIRSTQUARTILE_LABEL = "PercentileMetricContributor.25thPercentile";
  public static final String PERCENTILE_MEDIAN_LABEL = "PercentileMetricContributor.Median";
  public static final String PERCENTILE_THIRDQUARTILE_LABEL = "PercentileMetricContributor.75thPercentile";

  public static final String SIMPLE_NAME = PercentileMetricContributor.class.getSimpleName();
  private static final Logger LOGGER = LoggerFactory.getLogger( PercentileMetricContributor.class );
  private double compression = 50.0;
  private String name = PercentileMetricContributor.class.getSimpleName();
  private List<PercentileDefinition> percentileDefinitions = initPercentileDefinitions();

  private List<PercentileDefinition> initPercentileDefinitions() {
    List<PercentileDefinition> result = new ArrayList<PercentileDefinition>();
    result.add( new PercentileDefinition( KEY_PATH, PERCENTILE_FIRSTQUARTILE_LABEL, 0.25 ) );
    result.add( new PercentileDefinition( KEY_PATH, PERCENTILE_MEDIAN_LABEL, 0.5 ) );
    result.add( new PercentileDefinition( KEY_PATH, PERCENTILE_THIRDQUARTILE_LABEL, 0.75 ) );
    return result;
  }

  public List<PercentileDefinition> getPercentileDefinitions() {
    return percentileDefinitions;
  }

  public void setPercentileDefinitions( List<PercentileDefinition> percentileDefinitions ) {
    this.percentileDefinitions = percentileDefinitions;
  }

  public double getCompression() {
    return compression;
  }

  public void setCompression( double compression ) {
    this.compression = compression;
  }

  @Override public void setDerived( MutableProfileFieldValueType mutableProfileFieldValueType ) {
    if ( mutableProfileFieldValueType.getCount() >= 5 ) {
      PercentileMetrics percentileMetrics = getOrCreatePercentileMetrics( mutableProfileFieldValueType );
      for ( PercentileDefinition percentileDefinition : percentileDefinitions ) {
        double percentile = percentileDefinition.getPercentile();
        percentileMetrics.setPercentile( percentile );
      }
    }
  }

  @Override public String getName() {
    return name;
  }

  @Override public void setName( String name ) {
    this.name = name;
  }

  @Override public Set<String> supportedTypes() {
    return new HashSet<String>( Arrays
      .asList( Integer.class.getCanonicalName(), Long.class.getCanonicalName(), Float.class.getCanonicalName(),
        Double.class.getCanonicalName() ) );
  }

  private PercentileMetrics getOrCreatePercentileMetrics( MutableProfileFieldValueType mutableProfileFieldValueType ) {
    PercentileMetrics result = (PercentileMetrics) mutableProfileFieldValueType.getValueTypeMetrics( SIMPLE_NAME );
    if ( result == null ) {
      result = new PercentileMetrics( new TDigest( compression ) );
      mutableProfileFieldValueType.setValueTypeMetrics( SIMPLE_NAME, result );
    }
    return result;
  }

  @Override
  public void process( MutableProfileFieldValueType mutableProfileFieldValueType,
                       DataSourceFieldValue dataSourceFieldValue )
    throws ProfileActionException {
    getOrCreatePercentileMetrics( mutableProfileFieldValueType )
      .add( ( (Number) dataSourceFieldValue.getFieldValue() ).doubleValue() );
  }

  @Override public void merge( MutableProfileFieldValueType into, ProfileFieldValueType from )
    throws MetricMergeException {
    PercentileMetrics secondHolder = (PercentileMetrics) from.getValueTypeMetrics( SIMPLE_NAME );
    if ( secondHolder == null ) {
      LOGGER.debug( "Second field didn't have estimator." );
    } else {
      getOrCreatePercentileMetrics( into ).add( secondHolder );
    }
  }

  public List<ProfileFieldProperty> profileFieldProperties() {
    List<ProfileFieldProperty> result = new ArrayList<ProfileFieldProperty>( percentileDefinitions.size() );
    for ( PercentileDefinition percentileDefinition : percentileDefinitions ) {
      result.add( new ProfileFieldProperty( percentileDefinition.getNamePath(), percentileDefinition.getNameKey(),
        Arrays.asList( "types", SIMPLE_NAME, "standard", String.valueOf( percentileDefinition.getPercentile() ) ) ) );
    }
    return result;
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

    PercentileMetricContributor that = (PercentileMetricContributor) o;

    if ( Double.compare( that.compression, compression ) != 0 ) {
      return false;
    }
    if ( name != null ? !name.equals( that.name ) : that.name != null ) {
      return false;
    }
    return !( percentileDefinitions != null ? !percentileDefinitions.equals( that.percentileDefinitions ) :
      that.percentileDefinitions != null );

  }

  @Override public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits( compression );
    result = (int) ( temp ^ ( temp >>> 32 ) );
    result = 31 * result + ( name != null ? name.hashCode() : 0 );
    result = 31 * result + ( percentileDefinitions != null ? percentileDefinitions.hashCode() : 0 );
    return result;
  }

  @Override public String toString() {
    return "PercentileMetricContributor{" +
      "compression=" + compression +
      ", name='" + name + '\'' +
      ", percentileDefinitions=" + percentileDefinitions +
      '}';
  }
  //CHECKSTYLE:OperatorWrap:ON
}
