/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
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

import com.pentaho.profiling.api.ProfileFieldDefinition;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.ProfilingField;
import com.pentaho.profiling.api.stats.Statistic;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * Created by bryan on 9/5/14.
 */
@Provider
@Produces( { /*MediaType.APPLICATION_XML,*/ MediaType.APPLICATION_JSON } )
public class ProfilingFieldMessageBodyWriter implements MessageBodyWriter<ProfileStatus> {

  @Override public boolean isWriteable( Class<?> type, Type genericType, Annotation[] annotations,
                                        MediaType mediaType ) {
    return ProfileStatus.class.isAssignableFrom( type ) && ( MediaType.APPLICATION_JSON_TYPE.equals( mediaType )
      /*|| MediaType.APPLICATION_XML_TYPE.equals( mediaType )*/ );
  }

  @Override public long getSize( ProfileStatus profileStatus, Class<?> type, Type genericType,
                                 Annotation[] annotations, MediaType mediaType ) {
    return -1;
  }

  @Override public void writeTo( ProfileStatus profileStatus, Class<?> type, Type genericType,
                                 Annotation[] annotations, MediaType mediaType,
                                 MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream )
    throws IOException, WebApplicationException {
    if ( MediaType.APPLICATION_JSON_TYPE.equals( mediaType ) ) {
      JSONObject resourceBundleJsonObject = new JSONObject();
      resourceBundleJsonObject.put( "profileStatus", getJsonObject( profileStatus ) );
      OutputStreamWriter outputStreamWriter = null;
      try {
        outputStreamWriter = new OutputStreamWriter( entityStream );
        resourceBundleJsonObject.writeJSONString( outputStreamWriter );
      } finally {
        if ( outputStreamWriter != null ) {
          outputStreamWriter.flush();
        }
      }
    } /* else if ( MediaType.APPLICATION_XML_TYPE.equals( mediaType ) ) {
      try {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Node propertiesNode = document.createElement( "properties" );
        document.appendChild( propertiesNode );
        for ( String key : Collections.list( resourceBundle.getKeys() ) ) {
          Node propertyNode = document.createElement( "property" );
          propertiesNode.appendChild( propertyNode );

          Node keyNode = document.createElement( "key" );
          keyNode.setTextContent( key );
          propertyNode.appendChild( keyNode );

          Node valueNode = document.createElement( "value" );
          valueNode.setTextContent( resourceBundle.getString( key ) );
          propertyNode.appendChild( valueNode );
        }
        Result output = new StreamResult( entityStream );
        Source input = new DOMSource( document );
        try {
          Transformer transformer = TransformerFactory.newInstance().newTransformer();
          transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
          transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "2" );
          transformer.transform( input, output );
        } catch ( TransformerException e ) {
          throw new IOException( e );
        }
      } catch ( ParserConfigurationException e ) {
        throw new WebApplicationException( e );
      }
    }*/
  }

  private JSONObject getJsonObject( ProfileStatus profileStatus ) {
    JSONObject result = new JSONObject();
    result.put( "id", profileStatus.getId() );
    if ( profileStatus.getDataSourceReference() != null ) {
      JSONObject dataSourceReference = new JSONObject();
      dataSourceReference.put( "id", profileStatus.getDataSourceReference().getId() );
      dataSourceReference.put( "dataSourceProvider", profileStatus.getDataSourceReference().getDataSourceProvider() );
      result.put( "dataSourceReference", dataSourceReference );
    }
    if ( profileStatus.getFields() != null ) {
      JSONArray fieldsArray = new JSONArray();
      for ( ProfilingField field : profileStatus.getFields() ) {
        fieldsArray.add( profileFieldToMap( field ) );
      }
      result.put( "fields", fieldsArray );
    }
    result.put( "totalEntities", profileStatus.getTotalEntities() );
    result.put( "currentOperation", profileStatus.getCurrentOperation() );
    result.put( "currentOperationPath", profileStatus.getCurrentOperationPath() );
    if ( profileStatus.getCurrentOperationVariables() != null ) {
      result.put( "currentOperationVariables", collectionToJsonArray( profileStatus.getCurrentOperationVariables() ) );
    }
    if ( profileStatus.getProfileFieldDefinition() != null ) {
      result.put( "profileFieldDefinition", getJsonObject( profileStatus.getProfileFieldDefinition() ) );
    }
    return result;
  }

  private JSONArray getJsonObject( ProfileFieldDefinition profileFieldDefinition ) {
    JSONArray result = new JSONArray();
    if ( profileFieldDefinition.getProfileFieldProperties() != null ) {
      for ( ProfileFieldProperty profileFieldProperty : profileFieldDefinition.getProfileFieldProperties() ) {
        result.add( getJsonObject( profileFieldProperty ) );
      }
    }
    return result;
  }

  private JSONObject getJsonObject( ProfileFieldProperty profileFieldProperty ) {
    JSONObject result = new JSONObject();
    result.put( "namePath", profileFieldProperty.getNamePath() );
    result.put( "nameKey", profileFieldProperty.getNameKey() );
    JSONArray jsonArray = new JSONArray();
    for ( Object pathElement : profileFieldProperty.getPathToProperty() ) {
      jsonArray.add( pathElement );
    }
    result.put( "pathToProperty", jsonArray );
    return result;
  }

  private JSONObject profileFieldToMap( ProfilingField profilingField ) {
    return profileFieldToMapHelper( profilingField.getValues() );
  }

  private JSONObject profileFieldToMapHelper( Map<String, Object> map ) {
    JSONObject result = new JSONObject();
    for ( Map.Entry<String, Object> entry : map.entrySet() ) {
      result.put( entry.getKey(), objectToJsonMapObject( entry.getValue() ) );
    }
    return result;
  }

  private JSONArray collectionToJsonArray( Collection<? extends Object> collection ) {
    JSONArray result = new JSONArray();
    for ( Object object : collection ) {
      result.add( objectToJsonMapObject( object ) );
    }
    return result;
  }

  private Object objectToJsonMapObject( Object object ) {
    if ( object instanceof Map ) {
      return profileFieldToMapHelper( (Map<String, Object>) object );
    } else if ( object instanceof Collection ) {
      return collectionToJsonArray( (Collection<Object>) object );
    } else if ( object instanceof Statistic ) {
      return ( (Statistic) object ).getValue();
    } else {
      return object;
    }
  }
}
