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

package com.pentaho.profiling.services.metrics.bundle;

import com.pentaho.profiling.api.metrics.MetricContributor;
import com.pentaho.profiling.api.metrics.MetricContributors;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.bundle.MetricContributorBundle;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bryan on 3/12/15.
 */
public class MetricContributorsReaderImpl {
  private static final Logger LOGGER = LoggerFactory.getLogger( MetricContributorsReaderImpl.class );
  private final ObjectMapper treeObjectMapper;
  private final ObjectMapper resolveObjectMapper;
  private final List<MetricContributorBundle> metricContributorBundles;

  public MetricContributorsReaderImpl( List<MetricContributorBundle> metricContributorBundles ) {
    this.metricContributorBundles = metricContributorBundles;
    treeObjectMapper = new ObjectMapper();
    resolveObjectMapper = new ObjectMapper();
    resolveObjectMapper.enableDefaultTyping( ObjectMapper.DefaultTyping.NON_FINAL );
  }

  public Class<?> getClass( String name ) throws ClassNotFoundException {
    for ( MetricContributorBundle metricContributorBundle : metricContributorBundles ) {
      for ( Class<?> metricClass : metricContributorBundle.getMetricContributorClasses() ) {
        String canonicalName = metricClass.getCanonicalName();
        if ( canonicalName.equals( name ) ) {
          return metricClass;
        }
      }
    }
    return null;
  }

  private <T> T readWithClassLoader( JsonNode jsonNode, Class<T> readType ) throws Exception {
    String typeName = jsonNode.get( 0 ).asText();
    Class<?> clazz = getClass( typeName );
    ClassLoader previous = Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader( clazz.getClassLoader() );
      return resolveObjectMapper.readValue( jsonNode, readType );
    } finally {
      Thread.currentThread().setContextClassLoader( previous );
    }
  }

  private String getTypeName( JsonNode jsonNode ) {
    JsonNode typeNode = jsonNode.get( 0 );
    if ( typeNode != null ) {
      return typeNode.asText();
    }
    return null;
  }

  public MetricContributors read( InputStream inputStream ) {
    List<MetricContributor> metricContributorList = new ArrayList<MetricContributor>();
    List<MetricManagerContributor> metricManagerContributorList = new ArrayList<MetricManagerContributor>();
    try {
      JsonNode rootNode = treeObjectMapper.readTree( inputStream );
      if ( rootNode != null ) {
        rootNode = rootNode.get( 1 );
        JsonNode metricContributors = rootNode.get( "metricContributors" );
        if ( metricContributors != null ) {
          for ( JsonNode metricContributorNode : metricContributors.get( 1 ) ) {
            try {
              metricContributorList.add( readWithClassLoader( metricContributorNode, MetricContributor.class ) );
            } catch ( Exception e ) {
              LOGGER.error(
                "Unable to read " + getTypeName( metricContributorNode ) + ", metric contributor won't be available",
                e );
            }
          }
        }
        JsonNode metricManagerContributors = rootNode.get( "metricManagerContributors" );
        if ( metricManagerContributors != null ) {
          for ( JsonNode metricManagerContributorNode : metricManagerContributors.get( 1 ) ) {
            try {
              metricManagerContributorList
                .add( readWithClassLoader( metricManagerContributorNode, MetricManagerContributor.class ) );
            } catch ( Exception e ) {
              LOGGER.error( "Unable to read " + getTypeName( metricManagerContributorNode )
                + ", metric contributor won't be available", e );
            }
          }
        }
      }
    } catch ( Exception e ) {
      LOGGER.error( "Unhandled error while parsing metric contributors", e );
    } finally {
      try {
        inputStream.close();
      } catch ( IOException e ) {
        // Ignore
      }
    }
    return new MetricContributors( metricContributorList, metricManagerContributorList );
  }
}
