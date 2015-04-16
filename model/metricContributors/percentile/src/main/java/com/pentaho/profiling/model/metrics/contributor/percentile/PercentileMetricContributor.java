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
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricContributorUtils;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.NVL;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import com.pentaho.profiling.api.stats.Statistic;
import org.codehaus.jackson.node.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

  public static final ProfileFieldProperty PERCENTILE_FIRSTQUARTILE = MetricContributorUtils
    .createMetricProperty( KEY_PATH, PERCENTILE_FIRSTQUARTILE_LABEL, MetricContributorUtils.STATISTICS,
      Statistic.PERCENTILE + "_25" );
  public static final ProfileFieldProperty PERCENTILE_MEDIAN = MetricContributorUtils
    .createMetricProperty( KEY_PATH, PERCENTILE_MEDIAN_LABEL, MetricContributorUtils.STATISTICS,
      Statistic.PERCENTILE + "_50" );
  public static final ProfileFieldProperty PERCENTILE_THIRDQUARTILE = MetricContributorUtils
    .createMetricProperty( KEY_PATH, PERCENTILE_THIRDQUARTILE_LABEL, MetricContributorUtils.STATISTICS,
      Statistic.PERCENTILE + "_75" );
  private static final Logger LOGGER = LoggerFactory.getLogger( PercentileMetricContributor.class );

  private final NVL nvl;

  private double compression = 50.0;
  private String name = PercentileMetricContributor.class.getSimpleName();
  private List<PercentileDefinition> percentileDefinitions = initPercentileDefinitions();

  public PercentileMetricContributor() {
    this( new NVL() );
  }

  public PercentileMetricContributor( NVL nvl ) {
    this.nvl = nvl;
  }

  private List<PercentileDefinition> initPercentileDefinitions() {
    List<PercentileDefinition> result = new ArrayList<PercentileDefinition>();
    result.add( new PercentileDefinition( 0.25, PERCENTILE_FIRSTQUARTILE ) );
    result.add( new PercentileDefinition( 0.5, PERCENTILE_MEDIAN ) );
    result.add( new PercentileDefinition( 0.75, PERCENTILE_THIRDQUARTILE ) );
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

  @Override public void setDerived( DataSourceMetricManager metricsForFieldType ) {
    Long count = metricsForFieldType.<Number>getValueNoDefault( MetricContributorUtils.COUNT ).longValue();
    TDigestHolder tDigestHolder = getEstimator( metricsForFieldType );
    if ( count >= 5 ) {
      for ( PercentileDefinition percentileDefinition : percentileDefinitions ) {
        metricsForFieldType
          .setValue( tDigestHolder.quantile( percentileDefinition.getPercentile() ),
            percentileDefinition.pathToProperty() );
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

  @Override public void clear( DataSourceMetricManager dataSourceMetricManager ) {
    dataSourceMetricManager.clear( CLEAR_PATH );
  }

  private TDigestHolder getEstimator( DataSourceMetricManager dataSourceMetricManager ) {
    Object estimatorObject = dataSourceMetricManager
      .getValueNoDefault( MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_estimator" );
    if ( estimatorObject instanceof TDigestHolder ) {
      return (TDigestHolder) estimatorObject;
    } else if ( estimatorObject instanceof Map ) {
      Object byteObject = ( (Map) estimatorObject ).get( "bytes" );
      if ( byteObject instanceof String ) {
        try {
          byteObject = new TextNode( (String) byteObject ).getBinaryValue();
        } catch ( IOException e ) {
          LOGGER.error( e.getMessage(), e );
        }
      }
      TDigestHolder tDigestHolder = new TDigestHolder();
      tDigestHolder.setBytes( (byte[]) byteObject );
      return tDigestHolder;
    } else {
      String className = estimatorObject == null ? "null" : estimatorObject.getClass().getCanonicalName();
      LOGGER.warn( Statistic.PERCENTILE + "_estimator" + " was of type " + className );
    }
    return null;
  }

  @Override
  public void process( DataSourceMetricManager metricsForFieldType, DataSourceFieldValue dataSourceFieldValue )
    throws ProfileActionException {
    double value = ( (Number) dataSourceFieldValue.getFieldValue() ).doubleValue();

    TDigestHolder tDigestHolder =
      metricsForFieldType.<TDigestHolder>getValueNoDefault( MetricContributorUtils.STATISTICS,
        Statistic.PERCENTILE + "_estimator" );

    if ( tDigestHolder == null ) {
      tDigestHolder = new TDigestHolder( new TDigest( compression ) );
      metricsForFieldType
        .setValue( tDigestHolder, MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_estimator" );
    }

    tDigestHolder.add( value );
  }

  @Override public void merge( DataSourceMetricManager into, DataSourceMetricManager from )
    throws MetricMergeException {
    TDigestHolder originalHolder = getEstimator( into );
    TDigestHolder secondHolder = getEstimator( from );
    if ( originalHolder == null ) {
      originalHolder = secondHolder;
    } else if ( secondHolder == null ) {
      LOGGER.debug( "First field had estimator but second field didn't." );
    } else {
      originalHolder.add( secondHolder );
    }
    into.setValue( originalHolder, MetricContributorUtils.STATISTICS, Statistic.PERCENTILE + "_estimator" );
  }

  public List<ProfileFieldProperty> profileFieldProperties() {
    List<ProfileFieldProperty> result = new ArrayList<ProfileFieldProperty>( percentileDefinitions.size() );
    for ( PercentileDefinition percentileDefinition : percentileDefinitions ) {
      result.add( percentileDefinition.getProfileFieldProperty() );
    }
    return result;
  }

  //OperatorWrap isn't helpful for autogenerated methods
  //CHECKSTYLE:OperatorWrap:OFF
  @Override
  public boolean equals( Object o ) {
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
    if ( percentileDefinitions != null ? !percentileDefinitions.equals( that.percentileDefinitions ) :
      that.percentileDefinitions != null ) {
      return false;
    }

    return !( nvl != null ? !nvl.equals( that.nvl ) : that.nvl != null );
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = nvl != null ? nvl.hashCode() : 0;
    temp = Double.doubleToLongBits( compression );
    result = 31 * result + (int) ( temp ^ ( temp >>> 32 ) );
    result = 31 * result + ( percentileDefinitions != null ? percentileDefinitions.hashCode() : 0 );
    return result;
  }

  @Override public String toString() {
    return "PercentileMetricContributor{" +
      "nvl=" + nvl +
      ", compression=" + compression +
      ", percentileDefinitions=" + percentileDefinitions +
      '}';
  }
  //CHECKSTYLE:OperatorWrap:ON
}
