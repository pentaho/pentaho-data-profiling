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

package com.pentaho.profiling.model;

import com.pentaho.profiling.api.json.ObjectMapperFactory;
import com.pentaho.profiling.api.metrics.MetricContributor;
import com.pentaho.profiling.api.metrics.MetricContributorService;
import com.pentaho.profiling.api.metrics.MetricContributors;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.bundle.MetricContributorBundle;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bryan on 3/11/15.
 */
public class MetricContributorServiceImpl implements MetricContributorService {
  private static final Logger LOGGER = LoggerFactory.getLogger( MetricContributorServiceImpl.class );
  private final List<MetricContributorBundle> metricContributorBundles;
  private final String jsonFile;
  private final ObjectMapper objectMapper;

  public MetricContributorServiceImpl( List<MetricContributorBundle> metricContributorBundles,
                                       ObjectMapperFactory objectMapperFactory ) {
    this.metricContributorBundles = metricContributorBundles;
    this.objectMapper = objectMapperFactory.createMapper();
    String karafHome = System.getProperty( "karaf.home" );
    if ( karafHome != null ) {
      jsonFile = new File( karafHome + "/etc/metricContributors.json" ).getAbsolutePath();
    } else {
      jsonFile = null;
    }
  }

  private MetricContributors getFullMetricContributors() {
    List<MetricContributor> metricContributorList = new ArrayList<MetricContributor>();
    List<MetricManagerContributor> metricManagerContributorList = new ArrayList<MetricManagerContributor>();
    for ( MetricContributorBundle metricContributorBundle : metricContributorBundles ) {
      for ( Class<?> clazz : metricContributorBundle.getClasses() ) {
        try {
          if ( MetricContributor.class.isAssignableFrom( clazz ) ) {
            metricContributorList.add( (MetricContributor) clazz.newInstance() );
          } else if ( MetricManagerContributor.class.isAssignableFrom( clazz ) ) {
            metricManagerContributorList.add( (MetricManagerContributor) clazz.newInstance() );
          } else {
            LOGGER.warn( "Unable to add metric contributor " + clazz.getCanonicalName() + ": not a subtype of "
              + MetricContributor.class.getCanonicalName() + " or " + MetricManagerContributor.class
              .getCanonicalName() );
          }
        } catch ( Exception e ) {
          LOGGER.warn( "Unable to add metric contributor " + clazz.getCanonicalName(), e );
        }
      }
    }

    return new MetricContributors( metricContributorList, metricManagerContributorList );
  }

  private Map<String, MetricContributors> getMetricContributorMap() {
    // First try to read from file
    File metricContributorsJson = null;
    if ( jsonFile != null ) {
      metricContributorsJson = new File( jsonFile );
    }
    MetricContributors fullMetricContributors = getFullMetricContributors();
    if ( metricContributorsJson.exists() ) {
      FileInputStream fileInputStream = null;
      try {
        fileInputStream = new FileInputStream( metricContributorsJson );
        Map<String, MetricContributors> metricContributorMap = objectMapper.readValue( fileInputStream, Map.class );
        metricContributorMap.put( MetricContributorService.FULL_CONFIGURATION, fullMetricContributors );
        return metricContributorMap;
      } catch ( Exception e ) {
        LOGGER.error( "Unable to read saved metric contributor json, falling back to default", e );
      } finally {
        if ( fileInputStream != null ) {
          try {
            fileInputStream.close();
          } catch ( IOException e ) {
            //Ignore
          }
        }
      }
    }
    Map<String, MetricContributors> result = new HashMap<String, MetricContributors>();
    result.put( MetricContributorService.FULL_CONFIGURATION, fullMetricContributors );
    result.put( MetricContributorService.DEFAULT_CONFIGURATION, fullMetricContributors );
    return result;
  }

  @Override public synchronized MetricContributors getDefaultMetricContributors( String configuration ) {
    Map<String, MetricContributors> metricContributorMap = getMetricContributorMap();
    MetricContributors metricContributors = metricContributorMap.get( configuration );
    if ( metricContributors == null ) {
      metricContributors = metricContributorMap.get( MetricContributorService.DEFAULT_CONFIGURATION );
    }
    return metricContributors;
  }

  @Override
  public synchronized void setDefaultMetricContributors( String configuration, MetricContributors metricContributors ) {
    Map<String, MetricContributors> metricContributorMap = getMetricContributorMap();
    metricContributorMap.put( configuration, metricContributors );
    metricContributorMap.remove( MetricContributorService.FULL_CONFIGURATION );
    File metricContributorsJson = null;
    if ( jsonFile != null ) {
      metricContributorsJson = new File( jsonFile );
      File parentFile = metricContributorsJson.getParentFile();
      if ( parentFile.exists() ) {
        FileOutputStream fileOutputStream = null;
        try {
          fileOutputStream = new FileOutputStream( metricContributorsJson );
          objectMapper.writerWithDefaultPrettyPrinter().writeValue( fileOutputStream, metricContributorMap );
        } catch ( Exception e ) {
          LOGGER.error( "Error while persisting metric contributor defaults", e );
        } finally {
          if ( fileOutputStream != null ) {
            try {
              fileOutputStream.close();
            } catch ( IOException e ) {
              // Ignore
            }
          }
        }
      } else {
        LOGGER.warn( "Etc folder: " + parentFile + " doesn't exist, not persisting metric contributor defaults" );
      }
    }
  }
}
