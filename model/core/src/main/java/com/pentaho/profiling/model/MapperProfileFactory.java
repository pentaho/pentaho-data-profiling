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

import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.Profile;
import com.pentaho.profiling.api.ProfileFactory;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.configuration.DataSourceMetadata;
import com.pentaho.profiling.api.configuration.ProfileConfiguration;
import com.pentaho.profiling.api.mapper.Mapper;
import com.pentaho.profiling.api.mapper.MapperDefinition;
import com.pentaho.profiling.api.metrics.MetricContributors;
import com.pentaho.profiling.api.metrics.MetricContributorsFactory;

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
