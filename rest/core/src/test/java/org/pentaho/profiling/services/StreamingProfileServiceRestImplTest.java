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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.pentaho.profiling.api.StreamingProfile;
import org.pentaho.profiling.api.StreamingProfileService;
import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.doc.rest.Example;
import org.pentaho.profiling.api.json.ObjectMapperFactory;
import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.pentaho.profiling.api.sample.SampleProviderManager;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 4/14/15.
 */
public class StreamingProfileServiceRestImplTest {
  private StreamingProfileService streamingProfileService;
  private ObjectMapperFactory objectMapperFactory;
  private SampleProviderManager sampleProviderManager;
  private StreamingProfileServiceRestImpl streamingProfileServiceRest;

  @Before
  public void setup() {
    streamingProfileService = mock( StreamingProfileService.class );
    objectMapperFactory = new ObjectMapperFactory();
    sampleProviderManager = mock( SampleProviderManager.class );
    streamingProfileServiceRest =
      new StreamingProfileServiceRestImpl( streamingProfileService, objectMapperFactory, sampleProviderManager );
  }

  @Test
  public void testGetStreamingProfileFound() {
    StreamingProfile streamingProfile = mock( StreamingProfile.class );
    String profileId = "test-id";
    when( streamingProfileService.getStreamingProfile( profileId ) ).thenReturn( streamingProfile );
    assertEquals( streamingProfile, streamingProfileServiceRest.getStreamingProfile( profileId ) );
  }

  @Test( expected = WebApplicationException.class )
  public void testGetStreamingProfileNotFound() {
    try {
      streamingProfileServiceRest.getStreamingProfile( "id" );
    } catch ( WebApplicationException e ) {
      assertEquals( 404, e.getResponse().getStatus() );
      throw e;
    }
  }

  @Test
  public void testGetStreamingProfileExample() {
    String id = "test-id";
    StreamingProfile streamingProfile = mock( StreamingProfile.class );
    when( streamingProfile.getId() ).thenReturn( id );
    when( sampleProviderManager.provide( StreamingProfile.class ) ).thenReturn( Arrays.asList( streamingProfile ) );
    List<Example> streamingProfileExample = streamingProfileServiceRest.getStreamingProfileExample();
    assertEquals( 1, streamingProfileExample.size() );
    Example example = streamingProfileExample.get( 0 );
    assertEquals( 0, example.getQueryParameters().size() );
    Map<String, String> pathParameters = example.getPathParameters();
    assertEquals( 1, pathParameters.size() );
    assertEquals( id, pathParameters.get( "profileId" ) );
    assertEquals( streamingProfile, example.getResponse() );
    assertNull( example.getBody() );
  }

  @Test( expected = WebApplicationException.class )
  public void testProcessRecord() throws ProfileActionException {
    String id = "test-id";
    StreamingRecordWrapper streamingRecordWrapper = mock( StreamingRecordWrapper.class );
    List<DataSourceFieldValue> dataSourceFieldValues = mock( List.class );
    when( streamingRecordWrapper.getProfileId() ).thenReturn( id );
    when( streamingRecordWrapper.getDataSourceFieldValues() ).thenReturn( dataSourceFieldValues );
    doThrow( new ProfileActionException( null, null ) ).when( streamingProfileService ).processRecord( id,
      dataSourceFieldValues );
    streamingProfileServiceRest.processRecord( streamingRecordWrapper );
  }

  @Test
  public void testProcessRecordExample() {
    List<Example> examples = streamingProfileServiceRest.processRecordExample();
    assertTrue( examples.size() >= 1 );
    for ( Example example : examples ) {
      assertNull( example.getPathParameters() );
      assertNull( example.getQueryParameters() );
      assertTrue( example.getBody() instanceof StreamingRecordWrapper );
      assertNull( example.getResponse() );
    }
  }

  @Test
  public void testProcessRecords() throws IOException, ProfileActionException {
    String id = "test-id";
    StreamingProfile streamingProfile = mock( StreamingProfile.class );
    when( streamingProfileService.getStreamingProfile( id ) ).thenReturn( streamingProfile );
    List<Example> examples = streamingProfileServiceRest.processRecordsExample();
    assertTrue( examples.size() >= 1 );
    for ( Example example : examples ) {
      assertEquals( 0, example.getQueryParameters().size() );
      assertEquals( 1, example.getPathParameters().size() );
      Object body = example.getBody();
      assertTrue( body instanceof List );
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      ObjectMapper mapper = objectMapperFactory.createMapper();
      mapper.writeValue( byteArrayOutputStream, body );
      HttpServletRequest httpServletRequest = mock( HttpServletRequest.class );
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( byteArrayOutputStream.toByteArray() );
      when( httpServletRequest.getInputStream() ).thenReturn( new ByteArrayServletInputStream( byteArrayInputStream ) );
      streamingProfileServiceRest.processRecords( id, httpServletRequest );
      List<List<DataSourceFieldValue>> records = (List<List<DataSourceFieldValue>>) body;
      for ( List<DataSourceFieldValue> record : records ) {
        verify( streamingProfile ).processRecord( record );
      }
    }
  }

  private static class ByteArrayServletInputStream extends ServletInputStream {
    private final ByteArrayInputStream byteArrayInputStream;

    private ByteArrayServletInputStream( ByteArrayInputStream byteArrayInputStream ) {
      this.byteArrayInputStream = byteArrayInputStream;
    }

    @Override public int read() throws IOException {
      return byteArrayInputStream.read();
    }
  }
}
