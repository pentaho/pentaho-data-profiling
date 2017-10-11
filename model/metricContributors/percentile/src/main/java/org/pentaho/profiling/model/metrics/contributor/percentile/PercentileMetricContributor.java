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

package org.pentaho.profiling.model.metrics.contributor.percentile;

import com.clearspring.analytics.stream.quantile.TDigest;
import org.pentaho.profiling.api.MessageUtils;
import org.pentaho.profiling.api.MutableProfileFieldValueType;
import org.pentaho.profiling.api.ProfileFieldProperty;
import org.pentaho.profiling.api.ProfileFieldValueType;
import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.metrics.MetricManagerContributor;
import org.pentaho.profiling.api.metrics.MetricMergeException;
import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
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
