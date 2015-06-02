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

package org.pentaho.profiling.api.mapper;

import org.pentaho.profiling.api.StreamingProfile;
import org.pentaho.profiling.api.configuration.DataSourceMetadata;
import org.pentaho.profiling.api.metrics.MetricContributors;

/**
 * Defines a mapper.  A class implementing this interface must be exported as a service in your blueprint. (You should
 * be able to use org.pentaho.profiling.api.mapper.MapperDefinitionImpl)
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
