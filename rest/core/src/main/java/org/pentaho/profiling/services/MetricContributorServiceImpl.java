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

package org.pentaho.profiling.services;

import org.pentaho.profiling.api.doc.rest.Example;
import org.pentaho.profiling.api.doc.rest.SuccessResponseCode;
import org.pentaho.profiling.api.metrics.MetricContributorService;
import org.pentaho.profiling.api.metrics.MetricContributors;

import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bryan on 3/11/15.
 */
@Produces( { MediaType.APPLICATION_JSON } )
@Consumes( { MediaType.APPLICATION_JSON } )
@WebService
public class MetricContributorServiceImpl implements MetricContributorService {
  public static final String CONFIGURATION = "configuration";
  private final MetricContributorService delegate;

  public MetricContributorServiceImpl( MetricContributorService delegate ) {
    this.delegate = delegate;
  }

  /**
   * Returns all metric contributor configurations.
   *
   * @return All metric contributor configurations.
   */
  @GET
  @Path( "/" )
  @SuccessResponseCode( 200 )
  @Override public Map<String, MetricContributors> getAllConfigurations() {
    return delegate.getAllConfigurations();
  }

  public Example getAllConfigurationsExample() {
    return new Example( null, null, null, delegate.getAllConfigurations() );
  }

  /**
   * Gets the metric contributors for the given configuration.  The "default" configuration will be returned if none is
   * specified or the specified configuration doesn't exist.  The "full" configuration consists of all known metric
   * contributors.
   *
   * @param configuration The configuration to return.
   * @return The configuration's metric contributors.
   */
  @GET
  @Path( "/{configuration}" )
  @SuccessResponseCode( 200 )
  @Override public MetricContributors getDefaultMetricContributors(
    @PathParam( CONFIGURATION ) String configuration ) {
    return delegate.getDefaultMetricContributors( configuration );
  }

  public List<Example> getDefaultMetricContributorsExample() {
    List<Example> result = new ArrayList<Example>();
    Example example = new Example();
    example.getPathParameters().put( CONFIGURATION, MetricContributorService.DEFAULT_CONFIGURATION );
    example.setResponse( getDefaultMetricContributors( MetricContributorService.DEFAULT_CONFIGURATION ) );
    result.add( example );
    example = new Example();
    example.getPathParameters().put( CONFIGURATION, MetricContributorService.FULL_CONFIGURATION );
    example.setResponse( getDefaultMetricContributors( MetricContributorService.FULL_CONFIGURATION ) );
    result.add( example );
    return result;
  }

  /**
   * Sets the configuration's metric contributors.  These metric contributors will be used if specified in the
   * ProfileConfiguration.
   *
   * @param configuration      The configuration to set.
   * @param metricContributors The metric contributors to set.
   */
  @POST
  @Path( "/{configuration}" )
  @SuccessResponseCode( 204 )
  @Override public void setDefaultMetricContributors( @PathParam( CONFIGURATION ) String configuration,
                                                      MetricContributors metricContributors ) {
    delegate.setDefaultMetricContributors( configuration, metricContributors );
  }

  public List<Example> setDefaultMetricContributorsExample() {
    List<Example> result = new ArrayList<Example>();
    Example example = new Example();
    example.getPathParameters().put( CONFIGURATION, MetricContributorService.DEFAULT_CONFIGURATION );
    example.setBody( getDefaultMetricContributors( MetricContributorService.DEFAULT_CONFIGURATION ) );
    result.add( example );
    return result;
  }
}
