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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pentaho.profiling.api.StreamingProfile;
import org.pentaho.profiling.api.StreamingProfileService;
import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.doc.rest.Body;
import org.pentaho.profiling.api.doc.rest.ErrorCode;
import org.pentaho.profiling.api.doc.rest.Example;
import org.pentaho.profiling.api.doc.rest.SuccessResponseCode;
import org.pentaho.profiling.api.json.ObjectMapperFactory;
import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.pentaho.profiling.api.sample.SampleProviderManager;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by bryan on 3/26/15.
 */
@Produces( { MediaType.APPLICATION_JSON } )
@Consumes( { MediaType.APPLICATION_JSON } )
@WebService
public class StreamingProfileServiceRestImpl implements StreamingProfileService {
  public static final String JAVA_UTIL_LIST = "java.util.List";
  private static final Logger LOGGER = LoggerFactory.getLogger( StreamingProfileServiceRestImpl.class );
  private final StreamingProfileService delegate;
  private final ObjectMapper objectMapper;
  private final SampleProviderManager sampleProviderManager;

  public StreamingProfileServiceRestImpl( StreamingProfileService delegate, ObjectMapperFactory objectMapperFactory,
                                          SampleProviderManager sampleProviderManager ) {
    this.delegate = delegate;
    this.sampleProviderManager = sampleProviderManager;
    this.objectMapper = objectMapperFactory.createMapper();
  }

  /**
   * Returns the streaming profile for the given profile id.
   *
   * @param profileId The profile id.
   * @return The streaming profile for the given profile id.
   */
  @GET
  @Path( "/{profileId}" )
  @SuccessResponseCode( 200 )
  @ErrorCode( code = 404, reason = "Streaming profile with given id doesn't exist." )
  @Override public StreamingProfile getStreamingProfile( @PathParam( "profileId" ) String profileId ) {
    StreamingProfile streamingProfile = delegate.getStreamingProfile( profileId );
    if ( streamingProfile == null ) {
      throw new WebApplicationException( 404 );
    }
    return streamingProfile;
  }

  public List<Example> getStreamingProfileExample() {
    List<Example> examples = new ArrayList<Example>();
    for ( StreamingProfile streamingProfile : sampleProviderManager.provide( StreamingProfile.class ) ) {
      Example example = new Example();
      example.getPathParameters().put( "profileId", streamingProfile.getId() );
      example.setResponse( streamingProfile );
      examples.add( example );
    }
    return examples;
  }

  @Override public void processRecord( String profileId, List<DataSourceFieldValue> dataSourceFieldValues )
    throws ProfileActionException {
    delegate.processRecord( profileId, dataSourceFieldValues );
  }

  /**
   * Sends the record into the streaming profile to be processed.
   *
   * @param streamingRecordWrapper The record.
   * @throws ProfileActionException
   */
  @POST
  @Path( "/processRecord" )
  @SuccessResponseCode( 204 )
  @ErrorCode( code = 500, reason = "Error processing the record." )
  public void processRecord( StreamingRecordWrapper streamingRecordWrapper ) {
    try {
      processRecord( streamingRecordWrapper.getProfileId(), streamingRecordWrapper.getDataSourceFieldValues() );
    } catch ( ProfileActionException e ) {
      throw new WebApplicationException( 500 );
    }
  }

  public List<Example> processRecordExample() {
    List<Example> examples = new ArrayList<Example>();
    DataSourceFieldValue name = new DataSourceFieldValue( "Bob" );
    name.setLogicalName( "name" );
    name.setPhysicalName( "person.name" );
    DataSourceFieldValue age = new DataSourceFieldValue( 25 );
    age.setLogicalName( "age" );
    age.setPhysicalName( "person.age" );
    StreamingRecordWrapper streamingRecordWrapper =
      new StreamingRecordWrapper( UUID.randomUUID().toString(), new ArrayList<DataSourceFieldValue>(
        Arrays.<DataSourceFieldValue>asList( name, age ) ) );
    examples.add( new Example( null, null, streamingRecordWrapper, null ) );
    return examples;
  }

  /**
   * Processes a list of records.
   *
   * @param profileId          The profile id to send the records into.
   * @param httpServletRequest
   * @throws ProfileActionException
   * @throws IOException
   */
  @POST
  @Path( "/processRecords/{profileId}" )
  @SuccessResponseCode( 204 )
  @Body( name = "records", type = JAVA_UTIL_LIST, description = "The list of records." )
  public void processRecords( @PathParam( "profileId" ) String profileId,
                              @Context HttpServletRequest httpServletRequest )
    throws ProfileActionException, IOException {
    StreamingProfile streamingProfile = delegate.getStreamingProfile( profileId );
    if ( streamingProfile == null ) {
      LOGGER.warn( "Attempted to process records for nonexistent profile: " + profileId );
    }
    JsonFactory jsonFactory = objectMapper.getJsonFactory();
    JsonParser jsonParser = jsonFactory.createJsonParser( httpServletRequest.getInputStream() );
    if ( jsonParser.nextToken() != JsonToken.START_ARRAY ) {
      throw new IOException( "Expected request to start with array open" );
    }
    while ( jsonParser.nextToken() != JsonToken.END_ARRAY ) {
      List<DataSourceFieldValue> record =
        objectMapper.readValue( jsonParser, new TypeReference<List<DataSourceFieldValue>>() {
        } );
      streamingProfile.processRecord( record );
    }
  }

  public List<Example> processRecordsExample() {
    List<Example> examples = new ArrayList<Example>();
    List<DataSourceFieldValue> record = new ArrayList<DataSourceFieldValue>();
    DataSourceFieldValue name = new DataSourceFieldValue( "Bob" );
    name.setLogicalName( "name" );
    name.setPhysicalName( "person.name" );
    record.add( name );
    DataSourceFieldValue age = new DataSourceFieldValue( 25 );
    age.setLogicalName( "age" );
    age.setPhysicalName( "person.age" );
    record.add( age );
    List<List<DataSourceFieldValue>> records = new ArrayList<List<DataSourceFieldValue>>();
    records.add( record );
    record = new ArrayList<DataSourceFieldValue>();
    name = new DataSourceFieldValue( "Jane" );
    name.setLogicalName( "name" );
    name.setPhysicalName( "person.name" );
    record.add( name );
    age = new DataSourceFieldValue( 45 );
    age.setLogicalName( "age" );
    age.setPhysicalName( "person.age" );
    record.add( age );
    records.add( record );
    Example example = new Example();
    example.setBody( records );
    example.getPathParameters().put( "profileId", UUID.randomUUID().toString() );
    examples.add( example );
    return examples;
  }
}
