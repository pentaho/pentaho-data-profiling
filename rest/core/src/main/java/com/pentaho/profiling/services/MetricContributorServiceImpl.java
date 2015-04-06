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
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by bryan on 3/11/15.
 */
@Produces( { MediaType.APPLICATION_JSON } )
@Consumes( { MediaType.APPLICATION_JSON } )
@WebService
public class MetricContributorServiceImpl implements MetricContributorService {
  private final MetricContributorService delegate;

  public MetricContributorServiceImpl( MetricContributorService delegate ) {
    this.delegate = delegate;
  }

  /**
   * Gets the system's current default metric contributors.  These metric contributors will be used if none are
   * specified in the CreateProfileRequest
   *
   * @return the system's current default metric contributors
   */
  @GET
  @Path( "/" )
  @SuccessResponseCode( 200 )
  @Override public MetricContributors getDefaultMetricContributors() {
    return delegate.getDefaultMetricContributors();
  }

  public Example getDefaultMetricContributorsExample() {
    return new Example( null, null, null, getDefaultMetricContributors() );
  }

  /**
   * Sets the system's current default metric contributors.  These metric contributors will be used if none are
   * specified in the CreateProfileRequest
   *
   * @return the system's current default metric contributors
   */
  @POST
  @Path( "/" )
  @SuccessResponseCode( 204 )
  @Override public void setDefaultMetricContributors( MetricContributors metricContributors ) {
    delegate.setDefaultMetricContributors( metricContributors );
  }

  public Example setDefaultMetricContributorsExample() {
    return new Example( null, null, delegate.getDefaultMetricContributors(), null );
  }
}
