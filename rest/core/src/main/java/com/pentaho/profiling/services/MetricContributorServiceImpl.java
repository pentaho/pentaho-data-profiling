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

package com.pentaho.profiling.services;

import com.pentaho.profiling.api.doc.rest.Example;
import com.pentaho.profiling.api.doc.rest.SuccessResponseCode;
import com.pentaho.profiling.api.metrics.MetricContributorService;
import com.pentaho.profiling.api.metrics.MetricContributors;

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
