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

import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by bryan on 3/5/15.
 */
@Produces( { MediaType.APPLICATION_JSON } )
@Consumes( { MediaType.APPLICATION_JSON } )
@WebService
public class AggregateProfileServiceRestImpl implements AggregateProfileService {
  private final AggregateProfileService delegate;

  public AggregateProfileServiceRestImpl( AggregateProfileService delegate ) {
    this.delegate = delegate;
  }

  @GET
  @Path( "/" )
  @Override public List<AggregateProfile> getAggregateProfiles() {
    return delegate.getAggregateProfiles();
  }

  @Override public void addChild( String profileId, String childProfileId ) {
    delegate.addChild( profileId, childProfileId );
  }

  @POST
  @Path( "/add" )
  public void addChild( AggregateAddChildWrapper aggregateAddChildWrapper ) {
    this.addChild( aggregateAddChildWrapper.getProfileId(), aggregateAddChildWrapper.getChildProfileId() );
  }
}
