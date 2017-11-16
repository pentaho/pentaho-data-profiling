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

package org.pentaho.profiling.api.json;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/**
 * Created by bryan on 3/27/15.
 */
public class ObjectMapperFactory {
  private final FilterProvider filterProvider;

  public ObjectMapperFactory() {
    this( HasFilterProvider.getInstance() );
  }

  public ObjectMapperFactory( FilterProvider filterProvider ) {
    this.filterProvider = filterProvider;
  }

  public ObjectMapper createMapper() {
    return createMapper( true );
  }

  public ObjectMapper createMapper( boolean applyFilter ) {
    ObjectMapper objectMapper = new ObjectMapper();

    if ( applyFilter && filterProvider != null ) {
      objectMapper.setFilters( filterProvider );
      objectMapper.setAnnotationIntrospector( new JacksonAnnotationIntrospector() {
        @Override public Object findFilterId( Annotated ac ) {
          return ac.getAnnotated();
        }
      } );
      objectMapper.setSerializerFactory( BeanSerializerFactory.instance );
    }
    objectMapper.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false );
    objectMapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
    return objectMapper;
  }

  public JacksonJaxbJsonProvider createProvider() {
    return createProvider( true );
  }

  public JacksonJaxbJsonProvider createProvider( boolean applyFilter ) {
    return new JacksonJaxbJsonProvider( createMapper( applyFilter ), JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS );
  }
}
