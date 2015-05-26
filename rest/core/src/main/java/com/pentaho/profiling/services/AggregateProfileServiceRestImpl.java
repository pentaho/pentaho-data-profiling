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

package com.pentaho.profiling.services;

import com.pentaho.profiling.api.AggregateProfile;
import com.pentaho.profiling.api.AggregateProfileService;
import com.pentaho.profiling.api.Profile;
import com.pentaho.profiling.api.doc.rest.ErrorCode;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
   * Returns a list of all top level aggregate profiles.
   *
   * @return A list of all top level aggregate profiles.
   */
  @GET
  @Path( "/" )
  @SuccessResponseCode( 200 )
  public List<AggregateProfileDTO> getAggregateProfileDTOs() {
    List<AggregateProfile> aggregateProfiles = getAggregateProfiles();
    Set<String> childIds = new HashSet<String>();
    for ( AggregateProfile aggregateProfile : aggregateProfiles ) {
      addChildIds( childIds, aggregateProfile );
    }
    List<AggregateProfile> topLevelProfiles = new ArrayList<AggregateProfile>();
    for ( AggregateProfile aggregateProfile : aggregateProfiles ) {
      if ( !childIds.contains( aggregateProfile.getId() ) ) {
        topLevelProfiles.add( aggregateProfile );
      }
    }
    return AggregateProfileDTO.forProfiles( topLevelProfiles );
  }

  private void addChildIds( Set<String> childIds, AggregateProfile aggregateProfile ) {
    List<Profile> childProfiles = aggregateProfile.getChildProfiles();
    if ( childProfiles == null ) {
      return;
    }
    for ( Profile childProfile : childProfiles ) {
      childIds.add( childProfile.getId() );
      if ( childProfile instanceof AggregateProfile ) {
        addChildIds( childIds, (AggregateProfile) childProfile );
      }
    }
  }

  @Override public List<AggregateProfile> getAggregateProfiles() {
    return delegate.getAggregateProfiles();
  }

  public Example getAggregateProfileDTOsExample() {
    Example example = new Example();
    List<AggregateProfileDTO> response = new ArrayList<AggregateProfileDTO>(
      AggregateProfileDTO.forProfiles( sampleProviderManager.provide( AggregateProfile.class ) ) );
    example.setResponse( response );
    return example;
  }

  /**
   * Returns the aggregate profile with the given id.
   *
   * @param profileId The profile id.
   * @return The aggregate profile with the given id.
   */
  @GET
  @Path( "/{profileId}" )
  @SuccessResponseCode( 200 )
  @ErrorCode( code = 204, reason = "The given profile id is not part of an aggregate profile." )
  public AggregateProfileDTO getAggregateProfileDTO( @PathParam( "profileId" ) String profileId ) {
    AggregateProfile aggregateProfile = getAggregateProfile( profileId );
    if ( aggregateProfile == null ) {
      return null;
    }
    return new AggregateProfileDTO( aggregateProfile );
  }

  @Override public AggregateProfile getAggregateProfile( String profileId ) {
    return delegate.getAggregateProfile( profileId );
  }

  public List<Example> getAggregateProfileDTOExample() {
    List<Example> examples = new ArrayList<Example>();
    for ( AggregateProfile aggregateProfile : sampleProviderManager.provide( AggregateProfile.class ) ) {
      Example example = new Example();
      example.getPathParameters().put( "profileId", aggregateProfile.getId() );
      example.setResponse( new AggregateProfileDTO( aggregateProfile ) );
      examples.add( example );
    }
    return examples;
  }

  @Override public void addChild( String profileId, String childProfileId ) {
    delegate.addChild( profileId, childProfileId );
  }

  /**
   * Adds a child profile to the aggregate.
   *
   * @param aggregateAddChildWrapper Wrapper command arguments.
   */
  @POST
  @Path( "/add" )
  @SuccessResponseCode( 204 )
  public void addChild( AggregateAddChildWrapper aggregateAddChildWrapper ) {
    addChild( aggregateAddChildWrapper.getProfileId(), aggregateAddChildWrapper.getChildProfileId() );
  }

  public Example addChildExample() {
    return new Example( null, null,
      new AggregateAddChildWrapper( UUID.randomUUID().toString(), UUID.randomUUID().toString() ), null );
  }
}
