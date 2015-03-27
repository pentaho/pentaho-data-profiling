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

import com.pentaho.profiling.api.StreamingProfile;
import com.pentaho.profiling.api.StreamingProfileService;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

/**
 * Created by bryan on 3/26/15.
 */
@Produces( { MediaType.APPLICATION_JSON } )
@Consumes( { MediaType.APPLICATION_JSON } )
@WebService
public class StreamingProfileServiceRestImpl implements StreamingProfileService {
  private static final Logger LOGGER = LoggerFactory.getLogger( StreamingProfileServiceRestImpl.class );
  private final StreamingProfileService delegate;

  public StreamingProfileServiceRestImpl( StreamingProfileService delegate ) {
    this.delegate = delegate;
  }

  @GET
  @Path( "/{profileId}" )
  @Override public StreamingProfile getStreamingProfile( @PathParam( "profileId" ) String profileId ) {
    return delegate.getStreamingProfile( profileId );
  }

  @Override public void processRecord( String profileId, List<DataSourceFieldValue> dataSourceFieldValues )
    throws ProfileActionException {
    delegate.processRecord( profileId, dataSourceFieldValues );
  }

  @POST
  @Path( "/processRecord" )
  public void processRecord( StreamingRecordWrapper streamingRecordWrapper ) throws ProfileActionException {
    delegate.processRecord( streamingRecordWrapper.getProfileId(), streamingRecordWrapper.getDataSourceFieldValues() );
  }

  @POST
  @Path( "/processRecords/{profileId}" )
  public void processRecords( @PathParam( "profileId" ) String profileId,
                              @Context HttpServletRequest httpServletRequest )
    throws ProfileActionException, IOException {
    StreamingProfile streamingProfile = delegate.getStreamingProfile( profileId );
    if ( streamingProfile == null ) {
      LOGGER.warn( "Attempted to process records for nonexistent profile: " + profileId );
    }
    ObjectMapper objectMapper = new ObjectMapper();
    JsonFactory jsonFactory = objectMapper.getJsonFactory();
    JsonParser jsonParser = jsonFactory.createJsonParser( httpServletRequest.getInputStream() );
    if ( jsonParser.nextToken() != JsonToken.START_ARRAY ) {
      throw new IOException( "Expected requrest to start with array open" );
    }
    while ( jsonParser.nextToken() != JsonToken.END_ARRAY ) {
      List<DataSourceFieldValue> record =
        objectMapper.readValue( jsonParser, new TypeReference<List<DataSourceFieldValue>>() {
        } );
      streamingProfile.processRecord( record );
    }
  }
}
