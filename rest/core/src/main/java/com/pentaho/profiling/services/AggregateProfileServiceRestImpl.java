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

import com.pentaho.profiling.api.AggregateProfile;
import com.pentaho.profiling.api.AggregateProfileService;
import com.pentaho.profiling.api.doc.rest.Example;
import com.pentaho.profiling.api.doc.rest.SuccessResponseCode;
import com.pentaho.profiling.api.sample.SampleProviderManager;

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
import java.util.UUID;

/**
 * Created by bryan on 3/5/15.
 */
@Produces( { MediaType.APPLICATION_JSON } )
@Consumes( { MediaType.APPLICATION_JSON } )
@WebService
public class AggregateProfileServiceRestImpl implements AggregateProfileService {
  private final AggregateProfileService delegate;
  private final SampleProviderManager sampleProviderManager;

  public AggregateProfileServiceRestImpl( AggregateProfileService delegate,
                                          SampleProviderManager sampleProviderManager ) {
    this.delegate = delegate;
    this.sampleProviderManager = sampleProviderManager;
  }

  /**
   * Returns a list of all aggregate profiles
   *
   * @return a list of all aggregate profiles
   */
  @GET
  @Path( "/" )
  @SuccessResponseCode( 200 )
  @Override public List<AggregateProfile> getAggregateProfiles() {
    return delegate.getAggregateProfiles();
  }

  public Example getAggregateProfilesExample() {
    Example example = new Example();
    List<AggregateProfile> response = new ArrayList<AggregateProfile>();
    response.addAll( sampleProviderManager.provide( AggregateProfile.class ) );
    example.setResponse( response );
    return example;
  }

  /**
   * Returns the aggregate profile with the given id
   *
   * @param profileId the profileId
   * @return the aggregate profile with the given id
   */
  @GET
  @Path( "/{profileId}" )
  @SuccessResponseCode( 200 )
  @Override public AggregateProfile getAggregateProfile( @PathParam( "profileId" ) String profileId ) {
    return delegate.getAggregateProfile( profileId );
  }

  public List<Example> getAggregateProfileExample() {
    List<Example> examples = new ArrayList<Example>();
    for ( AggregateProfile aggregateProfile : sampleProviderManager.provide( AggregateProfile.class ) ) {
      Example example = new Example();
      example.getPathParameters().put( "profileId", aggregateProfile.getId() );
      example.setResponse( aggregateProfile );
      examples.add( example );
    }
    return examples;
  }

  @Override public void addChild( String profileId, String childProfileId ) {
    delegate.addChild( profileId, childProfileId );
  }

  /**
   * Adds a child profile to the aggregate
   *
   * @param aggregateAddChildWrapper wrapper command arguments
   */
  @POST
  @Path( "/add" )
  @SuccessResponseCode( 204 )
  public void addChild( AggregateAddChildWrapper aggregateAddChildWrapper ) {
    this.addChild( aggregateAddChildWrapper.getProfileId(), aggregateAddChildWrapper.getChildProfileId() );
  }

  public Example addChildExample() {
    return new Example( null, null,
      new AggregateAddChildWrapper( UUID.randomUUID().toString(), UUID.randomUUID().toString() ), null );
  }
}
