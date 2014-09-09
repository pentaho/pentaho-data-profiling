/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
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

import com.pentaho.profiling.api.ProfileCreationException;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.ProfilingService;
import com.pentaho.profiling.api.datasource.DataSourceReference;
import com.pentaho.profiling.api.measure.MeasureMetadata;
import com.pentaho.profiling.api.measure.RequestedMeasure;
import com.pentaho.profiling.api.operations.ProfileOperation;

import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by bryan on 7/31/14.
 */
@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
@Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
@WebService
public class ProfilingServiceWebserviceImpl implements ProfilingService {
  private ProfilingService delegate;

  public ProfilingService getDelegate() {
    return delegate;
  }

  public void setDelegate( ProfilingService delegate ) {
    this.delegate = delegate;
  }

  @POST
  @Path( "/" )
  @Override
  public ProfileStatus create( DataSourceReference dataSourceReference ) throws ProfileCreationException {
    return delegate.create( dataSourceReference );
  }

  @GET
  @Path( "/measures/supported/{profileId}" )
  @Override
  public List<MeasureMetadata> getSupportedMeasures( @PathParam( "profileId" ) String profileId ) {
    return delegate.getSupportedMeasures( profileId );
  }

  @PUT
  @Path( "/measures/requested/{profileId}" )
  @Override
  public void setRequestedMeasures( @PathParam( "profileId" ) String profileId, List<RequestedMeasure> measures ) {
    delegate.setRequestedMeasures( profileId, measures );
  }

  @GET
  @Path( "/" )
  @Override
  public List<ProfileStatus> getActiveProfiles() {
    return delegate.getActiveProfiles();
  }

  @GET
  @Path( "/{profileId}" )
  @Override
  public ProfileStatus getProfileUpdate( @PathParam( "profileId" ) String profileId ) {
    return delegate.getProfileUpdate( profileId );
  }

  @PUT
  @Path( "/stop" )
  public void stopCurrentOperation( ProfileIdWrapper profileIdWrapper ) {
    stopCurrentOperation( profileIdWrapper.getProfileId() );
  }

  @Override public void stopCurrentOperation( String profileId ) {
    delegate.stopCurrentOperation( profileId );
  }

  @Override public void startOperation( String profileId, String operationId ) {
    delegate.startOperation( profileId, operationId );
  }

  @GET
  @Path( "/operations/{profileId}" )
  @Override public List<ProfileOperation> getOperations( @PathParam( "profileId" ) String profileId ) {
    return delegate.getOperations( profileId );
  }

  @PUT
  @Path( "/start" )
  public void startOperation( ProfileOperationWrapper profileOperationWrapper ) {
    startOperation( profileOperationWrapper.getProfileId(), profileOperationWrapper.getOperationId() );
  }
}
