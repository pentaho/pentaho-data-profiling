/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2014 by Pentaho : http://www.pentaho.com
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
