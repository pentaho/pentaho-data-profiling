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

/**
 * Defines a mapper.  A class implementing this interface must be exported as a service in your blueprint. (You should
 * be able to use com.pentaho.profiling.api.mapper.MapperDefinitionImpl)
 */
public interface MapperDefinition {
  /**
   * Returns true iff this mapper definition can handle the given DataSourceMetadata
   *
   * @param dataSourceMetadata the given DataSourceMetadata
   * @return true iff this mapper definition can handle the given DataSourceMetadata
   */
  boolean accepts( DataSourceMetadata dataSourceMetadata );

  /**
   * Creates the mapper which will read data from the datasource and send it into the streaming profile
   *
   * @param dataSourceMetadata the DataSourceMetadata
   * @param streamingProfile   the StreamingProfile
   * @return the mapper
   */
  Mapper create( DataSourceMetadata dataSourceMetadata, StreamingProfile streamingProfile );

  /**
   * Returns any custom Metric Contributors that are only relevant for this datasource (currently only Mongo needs this
   * to support some extra metadata for the Get Fields Impl)
   *
   * @return any custom Metric Contributors that are only relevant for this datasource
   */
  MetricContributors getMapperMetricContributors();
}
