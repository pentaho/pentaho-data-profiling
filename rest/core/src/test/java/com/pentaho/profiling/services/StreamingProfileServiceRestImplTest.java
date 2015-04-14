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
import com.pentaho.profiling.api.doc.rest.Example;
import com.pentaho.profiling.api.json.ObjectMapperFactory;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.sample.SampleProviderManager;
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
    objectMapperFactory = new ObjectMapperFactory( getClass().getClassLoader() );
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
      objectMapperFactory.createMapper().writeValue( byteArrayOutputStream, body );
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
