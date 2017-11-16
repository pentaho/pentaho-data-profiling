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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pentaho.profiling.api.doc.rest.Body;
import org.pentaho.profiling.api.doc.rest.DocEndpoint;
import org.pentaho.profiling.api.doc.rest.DocEntry;
import org.pentaho.profiling.api.doc.rest.DocParameter;
import org.pentaho.profiling.api.doc.rest.ErrorCode;
import org.pentaho.profiling.api.doc.rest.Example;
import org.pentaho.profiling.api.doc.rest.SuccessResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bryan on 4/3/15.
 */
@Produces( { MediaType.APPLICATION_JSON } )
@Consumes( { MediaType.APPLICATION_JSON } )
@WebService
public class DocService {
  public static final Logger LOGGER = LoggerFactory.getLogger( DocService.class );
  public static final String JAVAX_WS_RS = "javax.ws.rs.";
  public static final String POST = "POST";
  public static final String GET = "GET";
  public static final String PUT = "PUT";
  public static final String DELETE = "DELETE";
  public static final String EXAMPLE = "Example";
  private final List<DocEndpoint> docEndpoints;

  public DocService( List<DocEndpoint> docEndpoints ) {
    this.docEndpoints = docEndpoints;
  }

  @GET
  @Path( "/" )
  public List<EndpointDocumentation> getEndpointDocumentation( @Context HttpServletRequest httpServletRequest ) {
    String basePathString = httpServletRequest.getRequestURL().toString();
    int cxfIndex = basePathString.indexOf( "/doc" );
    if ( cxfIndex != -1 ) {
      basePathString = basePathString.substring( 0, cxfIndex );
    }
    List<EndpointDocumentation> result = new ArrayList<EndpointDocumentation>();
    ObjectMapper objectMapper = new ObjectMapper();
    for ( DocEndpoint docEndpoint : docEndpoints ) {
      Object endpointBean = docEndpoint.getEndpointBean();
      Map<String, DocEntry> docEntries = new HashMap<String, DocEntry>();
      Class<?> endpointBeanClass = endpointBean.getClass();
      try {
        docEntries = objectMapper
          .readValue( endpointBeanClass.getClassLoader().getResourceAsStream( "META-INF/js/restDoc.js" ),
            new TypeReference<Map<String, DocEntry>>() {
            } );
      } catch ( IOException e ) {
        LOGGER.warn( "Unable to load restDoc for endpoint", e );
      }
      for ( Method method : endpointBeanClass.getMethods() ) {
        Path annotation = method.getAnnotation( Path.class );
        if ( annotation != null ) {
          DocEntry docEntry = getDocEntry( endpointBeanClass, method, docEntries );
          EndpointDocumentation endpointDocumentation = new EndpointDocumentation();
          endpointDocumentation.setName( method.getName() );
          setPath( basePathString, docEndpoint, annotation, endpointDocumentation );
          setMethod( method, endpointDocumentation );
          setDescription( endpointDocumentation, docEntry );
          setParameters( method, endpointDocumentation, docEntry );
          setExample( endpointBean, endpointBeanClass, method, endpointDocumentation, docEndpoint );
          setResponseCodes( method, endpointDocumentation );
          setResponse( method, endpointDocumentation, docEntry );
          result.add( endpointDocumentation );
        }
      }
    }
    Collections.sort( result, new Comparator<EndpointDocumentation>() {
      @Override public int compare( EndpointDocumentation o1, EndpointDocumentation o2 ) {
        int pathCompare = o1.getPath().compareTo( o2.getPath() );
        if ( pathCompare == 0 ) {
          return o1.getName().compareTo( o2.getName() );
        }
        return pathCompare;
      }
    } );
    return result;
  }

  private void setPath( String basePathString, DocEndpoint docEndpoint, Path annotation,
                        EndpointDocumentation endpointDocumentation ) {
    String path = basePathString + docEndpoint.getAddress();
    String pathAnnotationValue = annotation.value();
    if ( !"/".equals( pathAnnotationValue ) ) {
      path += pathAnnotationValue;
    }
    endpointDocumentation.setPath( path );
  }

  private DocEntry getDocEntry( Class<?> endpointBeanClass, Method method, Map<String, DocEntry> docEntries ) {
    StringBuilder docKey = new StringBuilder( endpointBeanClass.getCanonicalName() );
    docKey.append( "." );
    docKey.append( method.getName() );
    docKey.append( "(" );
    Class<?>[] parameterTypes = method.getParameterTypes();
    if ( parameterTypes != null ) {
      for ( Class<?> parameterType : parameterTypes ) {
        docKey.append( parameterType.getCanonicalName() );
        docKey.append( ", " );
      }
      if ( parameterTypes.length > 0 ) {
        docKey.setLength( docKey.length() - 2 );
      }
    }
    docKey.append( ")" );
    return docEntries.get( docKey.toString() );
  }

  private void setMethod( Method method, EndpointDocumentation endpointDocumentation ) {
    if ( method.getAnnotation( POST.class ) != null ) {
      endpointDocumentation.setMethod( POST );
    } else if ( method.getAnnotation( GET.class ) != null ) {
      endpointDocumentation.setMethod( GET );
    } else if ( method.getAnnotation( PUT.class ) != null ) {
      endpointDocumentation.setMethod( PUT );
    } else if ( method.getAnnotation( DELETE.class ) != null ) {
      endpointDocumentation.setMethod( DELETE );
    }
  }

  private void setDescription( EndpointDocumentation endpointDocumentation, DocEntry docEntry ) {
    if ( docEntry != null ) {
      endpointDocumentation.setDescription( docEntry.getMessage() );
    }
  }

  private void setResponse( Method method, EndpointDocumentation endpointDocumentation, DocEntry docEntry ) {
    Class<?> returnType = method.getReturnType();
    if ( returnType != void.class && returnType != Void.class ) {
      EndpointResponse endpointResponse = new EndpointResponse();
      endpointResponse.setType( returnType.getCanonicalName() );
      if ( docEntry != null ) {
        endpointResponse.setDescription( docEntry.getReturnDescription() );
      }
      endpointDocumentation.setEndpointResponse( endpointResponse );
    }
  }

  private void setParameters( Method method, EndpointDocumentation endpointDocumentation, DocEntry docEntry ) {
    Class<?>[] parameterTypes = method.getParameterTypes();
    if ( parameterTypes != null ) {
      Annotation[][] parameterAnnotations = method.getParameterAnnotations();
      List<EndpointParameter> pathParameters = new ArrayList<EndpointParameter>();
      List<EndpointParameter> queryParameters = new ArrayList<EndpointParameter>();
      int index = 0;
      boolean canHaveBody = canHaveBody( method );
      boolean foundBodyParameter = false;
      for ( Class<?> parameterType : parameterTypes ) {
        DocParameter docParameter = null;
        Annotation[] parameterAnnotationsForParam = parameterAnnotations[ index ];
        if ( docEntry != null ) {
          docParameter = docEntry.getParameters().get( index );
        }
        if ( canHaveBody && !foundBodyParameter ) {
          // First parameter with no jax annotations is body
          boolean foundJaxAnnotation = false;
          if ( parameterAnnotationsForParam != null ) {
            for ( Annotation annotation : parameterAnnotationsForParam ) {
              if ( annotation.annotationType().getCanonicalName().startsWith( JAVAX_WS_RS ) ) {
                foundJaxAnnotation = true;
                break;
              }
            }
          }
          if ( !foundJaxAnnotation ) {
            foundBodyParameter = true;
            EndpointParameter endpointParameter = new EndpointParameter();
            if ( docParameter == null ) {
              endpointParameter.setName( "arg" + index );
            } else {
              endpointParameter.setName( docParameter.getName() );
              endpointParameter.setDescription( docParameter.getComment() );
            }
            endpointParameter.setType( parameterType.getCanonicalName() );
            endpointDocumentation.setEndpointBodyParameter( endpointParameter );
          }
        }
        for ( Annotation annotation : parameterAnnotationsForParam ) {
          EndpointParameter endpointParameter = new EndpointParameter();
          if ( PathParam.class.isAssignableFrom( annotation.annotationType() ) ) {
            endpointParameter.setName( ( (PathParam) annotation ).value() );
            pathParameters.add( endpointParameter );
          } else if ( QueryParam.class.isAssignableFrom( annotation.annotationType() ) ) {
            endpointParameter.setName( ( (QueryParam) annotation ).value() );
            queryParameters.add( endpointParameter );
          }
          endpointParameter.setType( parameterType.getSimpleName() );
          if ( docParameter != null ) {
            endpointParameter.setDescription( docParameter.getComment() );
          }
        }
        index++;
      }
      Body bodyAnnotation = method.getAnnotation( Body.class );
      if ( bodyAnnotation != null ) {
        endpointDocumentation.setEndpointBodyParameter(
          new EndpointParameter( bodyAnnotation.name(), bodyAnnotation.type(), bodyAnnotation.description() ) );
      }
      endpointDocumentation.setEndpointPathParameters( pathParameters );
      endpointDocumentation.setEndpointQueryParameters( queryParameters );
    }
  }

  private EndpointExample processExample( EndpointDocumentation endpointDocumentation, Example example,
                                          DocEndpoint docEndpoint ) {
    EndpointExample endpointExample = new EndpointExample();
    String url = endpointDocumentation.getPath();
    Map<String, String> pathParameters = example.getPathParameters();
    if ( pathParameters != null ) {
      for ( Map.Entry<String, String> pathParameter : pathParameters.entrySet() ) {
        url = url.replace( "{" + pathParameter.getKey() + "}", pathParameter.getValue() );
      }
    }
    Map<String, String> queryParameters = example.getQueryParameters();
    if ( queryParameters != null ) {
      StringBuilder queryUrl = new StringBuilder( "?" );
      for ( Map.Entry<String, String> queryParameter : queryParameters.entrySet() ) {
        queryUrl.append( queryParameter.getKey() );
        queryUrl.append( "=" );
        queryUrl.append( queryParameter.getValue() );
        queryUrl.append( "&" );
      }
      if ( queryUrl.length() > 1 ) {
        queryUrl.setLength( queryUrl.length() - 1 );
        url += queryUrl.toString();
      }
    }
    endpointExample.setUrl( url );
    Object body = example.getBody();
    ObjectMapper objectMapper = docEndpoint.getObjectMapper();
    if ( body != null ) {
      try {
        endpointExample.setBody( objectMapper.writeValueAsString( body ) );
      } catch ( IOException e ) {
        LOGGER.warn( "Error writing " + body + " as string. Endpoint " + endpointDocumentation.getPath()
          + " will be missing example body", e );
      }
    }
    Object response = example.getResponse();
    if ( response != null ) {
      try {
        endpointExample.setExampleReturn( objectMapper.writeValueAsString( response ) );
      } catch ( IOException e ) {
        LOGGER.warn( "Error writing " + body + " as string. Endpoint " + endpointDocumentation.getPath()
          + " will be missing example response", e );
      }
    }
    return endpointExample;
  }

  private void setExample( Object endpointBean, Class<?> endpointBeanClass, Method method,
                           EndpointDocumentation endpointDocumentation, DocEndpoint docEndpoint ) {
    List<EndpointExample> endpointExamples = new ArrayList<EndpointExample>();
    try {
      Method exampleMethod = endpointBeanClass.getMethod( method.getName() + EXAMPLE );
      Object exampleMethodResult = exampleMethod.invoke( endpointBean );
      if ( List.class.isInstance( exampleMethodResult ) ) {
        for ( Object exampleObj : ( (List) exampleMethodResult ) ) {
          if ( exampleObj instanceof Example ) {
            Example example = (Example) exampleObj;
            endpointExamples.add( processExample( endpointDocumentation, example, docEndpoint ) );
          } else {
            LOGGER.warn( "Expecting " + exampleMethod + " to return result of type List<Example> or Example" );
          }
        }
      } else if ( Example.class.isInstance( exampleMethodResult ) ) {
        endpointExamples.add( processExample( endpointDocumentation, (Example) exampleMethodResult, docEndpoint ) );
      } else {
        LOGGER.warn( "Expecting " + exampleMethod + " to return result of type List<Example> or Example" );
      }
    } catch ( Exception e ) {
      LOGGER.warn(
        "Error with method for " + method.toString() + ", endpoint " + endpointDocumentation.getPath()
          + " will not have an example", e );
    }
    endpointDocumentation.setEndpointExamples( endpointExamples );
  }

  private void setResponseCodes( Method method, EndpointDocumentation endpointDocumentation ) {
    SuccessResponseCode successResponseCode = method.getAnnotation( SuccessResponseCode.class );
    if ( successResponseCode == null ) {
      LOGGER.warn( endpointDocumentation.getPath() + "(" + method + ") has no success response code" );
    } else {
      endpointDocumentation.setSuccessResponseCode( successResponseCode.value() );
    }
    List<EndpointReturnCode> endpointReturnCodes = new ArrayList<EndpointReturnCode>();
    for ( Annotation annotation : method.getAnnotations() ) {
      if ( ErrorCode.class.isAssignableFrom( annotation.annotationType() ) ) {
        EndpointReturnCode endpointReturnCode = new EndpointReturnCode();
        ErrorCode errorCode = (ErrorCode) annotation;
        endpointReturnCode.setStatus( errorCode.code() );
        endpointReturnCode.setReason( errorCode.reason() );
        endpointReturnCodes.add( endpointReturnCode );
      }
    }
    endpointDocumentation.setErrorCodes( endpointReturnCodes );
  }

  private boolean canHaveBody( Method method ) {
    return method.getAnnotation( POST.class ) != null || method.getAnnotation( PUT.class ) != null;
  }
}
