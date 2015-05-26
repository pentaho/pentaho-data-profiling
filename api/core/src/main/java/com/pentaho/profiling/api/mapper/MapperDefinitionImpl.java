/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
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
