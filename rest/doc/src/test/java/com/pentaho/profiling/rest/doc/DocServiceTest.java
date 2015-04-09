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

package com.pentaho.profiling.rest.doc;

import com.pentaho.profiling.api.doc.rest.DocEndpoint;
import com.pentaho.profiling.api.doc.rest.DocEndpointImpl;
import com.pentaho.profiling.api.doc.rest.ErrorCode;
import com.pentaho.profiling.api.doc.rest.Example;
import com.pentaho.profiling.api.doc.rest.SuccessResponseCode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 4/9/15.
 */
public class DocServiceTest {
  @Test
  public void testDocService() {
    List<DocEndpoint> docEndpoints = new ArrayList<DocEndpoint>();
    ObjectMapper objectMapper = new ObjectMapper();
    DocEndpoint docEndpoint = new DocEndpointImpl( "/test", new TestEndpoint(), objectMapper );
    docEndpoints.add( docEndpoint );

    HttpServletRequest httpServletRequest = mock( HttpServletRequest.class );
    when( httpServletRequest.getRequestURL() ).thenReturn( new StringBuffer( "http://localhost:8181/cxf/doc" ) );
    DocService docService = new DocService( docEndpoints );
    List<EndpointDocumentation> result = docService.getEndpointDocumentation( httpServletRequest );
    assertEquals( 3, result.size() );
    int index = 0;

    EndpointDocumentation postDoc = result.get( index++ );
    assertEquals( DocService.POST, postDoc.getMethod() );
    assertEquals( "testPost", postDoc.getName() );
    List<EndpointExample> postDocEndpointExamples = postDoc.getEndpointExamples();
    assertEquals( 1, postDocEndpointExamples.size() );
    EndpointExample postDocEndpointExample = postDocEndpointExamples.get( 0 );
    assertEquals( "http://localhost:8181/cxf/test/post", postDocEndpointExample.getUrl() );
    assertEquals( "\"test-body\"", postDocEndpointExample.getBody() );
    assertNull( postDocEndpointExample.getExampleReturn() );
    EndpointParameter endpointBodyParameter = postDoc.getEndpointBodyParameter();
    assertEquals( "arg0", endpointBodyParameter.getName() );
    assertEquals( String.class.getCanonicalName(), endpointBodyParameter.getType() );
    assertNull( endpointBodyParameter.getDescription() );

    EndpointDocumentation deleteDoc = result.get( index++ );
    assertEquals( DocService.DELETE, deleteDoc.getMethod() );
    assertEquals( "testDelete", deleteDoc.getName() );
    List<EndpointParameter> deleteDocEndpointPathParameters = deleteDoc.getEndpointPathParameters();
    assertEquals( 1, deleteDocEndpointPathParameters.size() );
    EndpointParameter deleteDocEndpointPathParameter = deleteDocEndpointPathParameters.get( 0 );
    assertEquals( "path", deleteDocEndpointPathParameter.getName() );
    assertEquals( String.class.getSimpleName(), deleteDocEndpointPathParameter.getType() );
    assertNull( deleteDocEndpointPathParameter.getDescription() );

    EndpointDocumentation getDoc = result.get( index++ );
    assertEquals( DocService.GET, getDoc.getMethod() );
    assertEquals( "testGet", getDoc.getName() );
    List<EndpointParameter> getDocEndpointQueryParameters = getDoc.getEndpointQueryParameters();
    assertEquals( 1, getDocEndpointQueryParameters.size() );
    EndpointParameter getDocEndpointQueryParameter = getDocEndpointQueryParameters.get( 0 );
    assertEquals( "query", getDocEndpointQueryParameter.getName() );
    assertEquals( String.class.getSimpleName(), getDocEndpointQueryParameter.getType() );
    assertNull( getDocEndpointQueryParameter.getDescription() );
  }

  public static class TestEndpoint {

    @GET
    @Path( "/{path}" )
    @SuccessResponseCode( 200 )
    @ErrorCode( code = 400, reason = "test-reason" )
    public String testGet( @PathParam( "path" ) String path, @QueryParam( "query" ) String query ) {
      return null;
    }

    public List<Example> testGetExample() {
      List<Example> result = new ArrayList<Example>();
      Example example1 = new Example();
      example1.getPathParameters().put( "path", "test-path" );
      example1.getQueryParameters().put( "query", "test-query" );
      example1.setResponse( "test-response" );
      result.add( example1 );
      return result;
    }

    @DELETE
    @Path( "/{path}" )
    public void testDelete( @PathParam( "path" ) String path ) {

    }

    public Example testDeleteExample() {
      Example example = new Example();
      example.getPathParameters().put( "path", "test-path" );
      return example;
    }

    @POST
    @Path( "/post" )
    public void testPost( String body ) {

    }

    public Example testPostExample() {
      return new Example( null, null, "test-body", null );
    }
  }
}
