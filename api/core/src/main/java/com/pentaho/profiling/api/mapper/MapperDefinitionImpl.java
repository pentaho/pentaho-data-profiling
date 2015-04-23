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

package com.pentaho.profiling.api.mapper;

import com.pentaho.profiling.api.StreamingProfile;
import com.pentaho.profiling.api.configuration.DataSourceMetadata;
import com.pentaho.profiling.api.metrics.MetricContributors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by bryan on 4/23/15.
 */
public class MapperDefinitionImpl implements MapperDefinition {
  private static final Logger LOGGER = LoggerFactory.getLogger( MapperDefinitionImpl.class );
  private final Class<? extends DataSourceMetadata> metadataClazz;
  private final Class<? extends Mapper> mapperClazz;
  private final MetricContributors metricContributors;

  public MapperDefinitionImpl( Class<? extends DataSourceMetadata> metadataClazz,
                               Class<? extends Mapper> mapperClazz, MetricContributors metricContributors ) {
    this.metadataClazz = metadataClazz;
    this.mapperClazz = mapperClazz;
    this.metricContributors = metricContributors;
  }

  @Override public boolean accepts( DataSourceMetadata dataSourceMetadata ) {
    return metadataClazz.isInstance( dataSourceMetadata );
  }

  @Override public Mapper create( DataSourceMetadata dataSourceMetadata, StreamingProfile streamingProfile ) {
    try {
      return mapperClazz.getConstructor( metadataClazz, StreamingProfile.class )
        .newInstance( dataSourceMetadata, streamingProfile );
    } catch ( NoSuchMethodException e ) {
      LOGGER.error( "Unable to construct mapper from definition: " + this, e );
    } catch ( InvocationTargetException e ) {
      LOGGER.error( "Unable to construct mapper from definition: " + this, e );
    } catch ( InstantiationException e ) {
      LOGGER.error( "Unable to construct mapper from definition: " + this, e );
    } catch ( IllegalAccessException e ) {
      LOGGER.error( "Unable to construct mapper from definition: " + this, e );
    }
    return null;
  }

  @Override public MetricContributors getMapperMetricContributors() {
    return metricContributors;
  }
}
