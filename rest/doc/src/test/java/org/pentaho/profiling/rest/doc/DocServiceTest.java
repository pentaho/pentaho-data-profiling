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

package org.pentaho.profiling.rest.doc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.pentaho.profiling.api.doc.rest.DocEndpoint;
import org.pentaho.profiling.api.doc.rest.DocEndpointImpl;
import org.pentaho.profiling.api.doc.rest.ErrorCode;
import org.pentaho.profiling.api.doc.rest.Example;
import org.pentaho.profiling.api.doc.rest.SuccessResponseCode;

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
