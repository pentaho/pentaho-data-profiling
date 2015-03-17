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

import com.pentaho.profiling.api.Profile;
import com.pentaho.profiling.api.ProfileCreateRequest;
import com.pentaho.profiling.api.ProfileCreationException;
import com.pentaho.profiling.api.ProfileFactory;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusReadOperation;
import com.pentaho.profiling.api.ProfileStatusReader;
import com.pentaho.profiling.api.ProfilingService;
import com.pentaho.profiling.api.datasource.DataSourceReference;
import com.pentaho.profiling.api.metrics.mapper.MetricContributorsObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bryan on 7/31/14.
 */
@Produces( { MediaType.APPLICATION_JSON } )
@Consumes( { MediaType.APPLICATION_JSON } )
@WebService
public class ProfilingServiceWebserviceImpl implements ProfilingService {
  private static final Logger LOGGER = LoggerFactory.getLogger( ProfilingServiceWebserviceImpl.class );
  private final ProfilingService delegate;
  private final MetricContributorsObjectMapperFactory metricContributorsObjectMapperFactory;

  public ProfilingServiceWebserviceImpl( ProfilingService delegate,
                                         MetricContributorsObjectMapperFactory metricContributorsObjectMapperFactory ) {
    this.delegate = delegate;
    this.metricContributorsObjectMapperFactory = metricContributorsObjectMapperFactory;
  }

  @POST
  @Path( "/" )
  public ProfileStatus createWebservice( String profileCreateRequestString ) throws ProfileCreationException {
    try {
      ProfileCreateRequest profileCreateRequest =
        metricContributorsObjectMapperFactory.createObjectMapper()
          .readValue( profileCreateRequestString, ProfileCreateRequest.class );
      return create( profileCreateRequest ).read( new ProfileStatusReadOperation<ProfileStatus>() {
        @Override public ProfileStatus read( ProfileStatus profileStatus ) {
          return profileStatus;
        }
      } );
    } catch ( Exception e ) {
      LOGGER.error( e.getMessage(), e );
      if ( e instanceof ProfileCreationException ) {
        throw (ProfileCreationException) e;
      }
      throw new ProfileCreationException( e );
    }
  }

  @Override public ProfileFactory getProfileFactory( DataSourceReference dataSourceReference ) {
    return delegate.getProfileFactory( dataSourceReference );
  }

  @POST
  @Path( "/accepts" )
  @Override public boolean accepts( DataSourceReference dataSourceReference ) {
    return delegate.accepts( dataSourceReference );
  }

  @Override
  public ProfileStatusManager create( ProfileCreateRequest profileCreateRequest ) throws ProfileCreationException {
    return delegate.create( profileCreateRequest );
  }

  @GET
  @Path( "/" )
  public List<ProfileStatus> getActiveProfilesWebservice() {
    List<ProfileStatusReader> profileStatusReaders = getActiveProfiles();
    List<ProfileStatus> result = new ArrayList<ProfileStatus>( profileStatusReaders.size() );
    for ( ProfileStatusReader profileStatusManager : profileStatusReaders ) {
      result.add( profileStatusManager.read( new ProfileStatusReadOperation<ProfileStatus>() {
        @Override public ProfileStatus read( ProfileStatus profileStatus ) {
          return profileStatus;
        }
      } ) );
    }
    return result;
  }

  @Override
  public List<ProfileStatusReader> getActiveProfiles() {
    return delegate.getActiveProfiles();
  }

  @Override public Profile getProfile( String profileId ) {
    return delegate.getProfile( profileId );
  }

  @GET
  @Path( "/{profileId}" )
  public ProfileStatus getProfileUpdateWebservice( @PathParam( "profileId" ) String profileId ) {
    return getProfileUpdate( profileId ).read( new ProfileStatusReadOperation<ProfileStatus>() {
      @Override public ProfileStatus read( ProfileStatus profileStatus ) {
        return profileStatus;
      }
    } );
  }

  @Override
  public ProfileStatusReader getProfileUpdate( String profileId ) {
    return delegate.getProfileUpdate( profileId );
  }

  @PUT
  @Path( "/stop" )
  public void stopCurrentOperation( ProfileIdWrapper profileIdWrapper ) {
    stop( profileIdWrapper.getProfileId() );
  }

  @Override public void stop( String profileId ) {
    delegate.stop( profileId );
  }

  @GET
  @Path( "/isRunning/{profileId}" )
  @Override public boolean isRunning( String profileId ) {
    return delegate.isRunning( profileId );
  }

  @PUT
  @Path( "/discard" )
  public void discardProfile( ProfileIdWrapper profileIdWrapper ) {
    discardProfile( profileIdWrapper.getProfileId() );
  }


  @Override public void discardProfile( String profileId ) {
    delegate.discardProfile( profileId );
  }
}
