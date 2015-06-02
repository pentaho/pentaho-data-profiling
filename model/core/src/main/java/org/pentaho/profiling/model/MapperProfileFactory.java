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

package org.pentaho.profiling.model;

import org.pentaho.profiling.api.MutableProfileStatus;
import org.pentaho.profiling.api.Profile;
import org.pentaho.profiling.api.ProfileFactory;
import org.pentaho.profiling.api.ProfileStatusManager;
import org.pentaho.profiling.api.ProfileStatusWriteOperation;
import org.pentaho.profiling.api.configuration.DataSourceMetadata;
import org.pentaho.profiling.api.configuration.ProfileConfiguration;
import org.pentaho.profiling.api.mapper.Mapper;
import org.pentaho.profiling.api.mapper.MapperDefinition;
import org.pentaho.profiling.api.metrics.MetricContributors;
import org.pentaho.profiling.api.metrics.MetricContributorsFactory;

import java.util.List;

/**
 * Created by bryan on 4/23/15.
 */
public class MapperProfileFactory implements ProfileFactory {
  private final List<MapperDefinition> mapperDefinitions;
  private final MetricContributorsFactory metricContributorsFactory;

  public MapperProfileFactory( List<MapperDefinition> mapperDefinitions,
                               MetricContributorsFactory metricContributorsFactory ) {
    this.mapperDefinitions = mapperDefinitions;
    this.metricContributorsFactory = metricContributorsFactory;
  }

  private MapperDefinition getMapperDefinition( DataSourceMetadata dataSourceMetadata ) {
    for ( MapperDefinition mapperDefinition : mapperDefinitions ) {
      if ( mapperDefinition.accepts( dataSourceMetadata ) ) {
        return mapperDefinition;
      }
    }
    return null;
  }

  @Override public boolean accepts( DataSourceMetadata dataSourceMetadata ) {
    return getMapperDefinition( dataSourceMetadata ) != null;
  }

  @Override
  public Profile create( final ProfileConfiguration profileConfiguration, ProfileStatusManager profileStatusManager ) {
    final DataSourceMetadata dataSourceMetadata = profileConfiguration.getDataSourceMetadata();
    MapperDefinition mapperDefinition = getMapperDefinition( dataSourceMetadata );
    MetricContributors metricContributors = new MetricContributors();
    metricContributors.add( mapperDefinition.getMapperMetricContributors() );
    metricContributors.add( profileConfiguration.getMetricContributors() );
    StreamingProfileImpl streamingProfile = new StreamingProfileImpl( profileStatusManager, metricContributorsFactory,
      metricContributors );
    Mapper mapper = mapperDefinition.create( dataSourceMetadata, streamingProfile );
    profileStatusManager.write( new ProfileStatusWriteOperation<Void>() {
      @Override public Void write( MutableProfileStatus profileStatus ) {
        profileStatus.setName( dataSourceMetadata.getLabel() );
        return null;
      }
    } );
    return new MapperProfileImpl( mapper, streamingProfile, profileStatusManager );
  }
}
