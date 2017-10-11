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

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.deser.std.PrimitiveArrayDeserializers;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.map.jsontype.NamedType;
import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.codehaus.jackson.map.jsontype.impl.AsPropertyTypeDeserializer;
import org.codehaus.jackson.map.jsontype.impl.AsPropertyTypeSerializer;
import org.codehaus.jackson.map.jsontype.impl.ClassNameIdResolver;
import org.codehaus.jackson.map.jsontype.impl.StdTypeResolverBuilder;
import org.codehaus.jackson.map.ser.BeanSerializerFactory;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bryan on 3/27/15.
 */
public class ObjectMapperFactory {
  private static final String ARRAY_LIST = ArrayList.class.getCanonicalName();
  private static final String HASH_SET = HashSet.class.getCanonicalName();
  private final ClassLoader classLoader;
  private final FilterProvider filterProvider;

  public ObjectMapperFactory( ClassLoader classLoader ) {
    this( classLoader, HasFilterProvider.getInstance() );
  }

  public ObjectMapperFactory( ClassLoader classLoader, FilterProvider filterProvider ) {
    this.classLoader = classLoader;
    this.filterProvider = filterProvider;
  }

  public ObjectMapper createMapper() {
    return createMapper( true );
  }

  public ObjectMapper createMapper( boolean applyFilter ) {
    ObjectMapper objectMapper = new ObjectMapper();
    StdTypeResolverBuilder typer =
      new ObjectMapper.DefaultTypeResolverBuilder( ObjectMapper.DefaultTyping.NON_FINAL ) {

        @Override
        protected TypeIdResolver idResolver( MapperConfig<?> config, JavaType baseType,
                                             Collection<NamedType> subtypes,
                                             boolean forSer, boolean forDeser ) {
          return new ClassNameIdResolver( baseType, config.getTypeFactory() ) {

            @Override public JavaType typeFromId( String id ) {
              ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
              try {
                Thread.currentThread().setContextClassLoader( classLoader );
                return super.typeFromId( id );
              } catch ( RuntimeException ex ) {
                throw new RuntimeException(
                  "Unable to find classloader for baseType: " + _baseType + " and id: " + id, ex );
              } finally {
                Thread.currentThread().setContextClassLoader( contextClassLoader );
              }
            }
          };
        }

        @Override public TypeSerializer buildTypeSerializer( SerializationConfig config, JavaType baseType,
                                                             Collection<NamedType> subtypes, BeanProperty property ) {
          AsPropertyTypeSerializer propertyTypeSerializer =
            new AsPropertyTypeSerializer( idResolver( config, baseType, subtypes, false, true ), property,
              _typeProperty ) {
              @Override public void writeTypePrefixForArray( Object value, JsonGenerator jgen )
                throws IOException {
                jgen.writeStartArray();
              }

              @Override public void writeTypePrefixForArray( Object value, JsonGenerator jgen, Class<?> type )
                throws IOException {
                jgen.writeStartArray();
              }

              @Override public void writeTypeSuffixForArray( Object value, JsonGenerator jgen )
                throws IOException {
                jgen.writeEndArray();
              }

              @Override public void writeTypePrefixForScalar( Object value, JsonGenerator jgen )
                throws IOException {
                // noop
              }

              @Override public void writeTypePrefixForScalar( Object value, JsonGenerator jgen, Class<?> type )
                throws IOException {
                // noop
              }

              @Override public void writeTypeSuffixForScalar( Object value, JsonGenerator jgen )
                throws IOException {
                // noop
              }
            };
          return _idType == JsonTypeInfo.Id.NONE ? null : propertyTypeSerializer;
        }

        @Override public TypeDeserializer buildTypeDeserializer( DeserializationConfig config, JavaType baseType,
                                                                 Collection<NamedType> subtypes,
                                                                 BeanProperty property ) {
          AsPropertyTypeDeserializer asPropertyTypeDeserializer =
            new AsPropertyTypeDeserializer( baseType, idResolver( config, baseType, subtypes, false, true ), property,
              _defaultImpl, _typeProperty ) {
              private final JsonDeserializer<Object> byteArrayDeser =
                PrimitiveArrayDeserializers.getAll().get( TypeFactory
                  .defaultInstance().constructType( byte.class ) );
              private final Map<Class, String> scalarNames =
                buildNames( int.class, Integer.class, long.class, Long.class, double.class, Double.class, float.class,
                  Float.class, String.class, char.class, Character.class );

              private Map<Class, String> buildNames( Class... clazzes ) {
                Map<Class, String> result = new HashMap<Class, String>();
                for ( Class clazz : clazzes ) {
                  result.put( clazz, clazz.getCanonicalName() );
                }
                return result;
              }

              @Override public Object deserializeTypedFromArray( JsonParser jp, DeserializationContext ctxt )
                throws IOException {
                if ( List.class == _baseType.getRawClass() ) {
                  return _findDeserializer( ctxt, ARRAY_LIST ).deserialize( jp, ctxt );
                } else if ( Set.class == _baseType.getRawClass() ) {
                  return _findDeserializer( ctxt, HASH_SET ).deserialize( jp, ctxt );
                } else if ( byte[].class == _baseType.getRawClass() ) {
                  return byteArrayDeser.deserialize( jp, ctxt );
                }
                return super.deserializeTypedFromArray( jp, ctxt );
              }

              @Override public Object deserializeTypedFromScalar( JsonParser jp, DeserializationContext ctxt )
                throws IOException {
                Class<?> rawClass = _baseType.getRawClass();
                String id = scalarNames.get( rawClass );
                if ( id == null && rawClass.isEnum() ) {
                  id = rawClass.getCanonicalName();
                  scalarNames.put( rawClass, id );
                }
                if ( id != null ) {
                  return _findDeserializer( ctxt, id ).deserialize( jp, ctxt );
                }
                return super.deserializeTypedFromScalar( jp, ctxt );
              }
            };
          return _idType == JsonTypeInfo.Id.NONE ? null : asPropertyTypeDeserializer;
        }
      };
    typer = typer.init( JsonTypeInfo.Id.CLASS, null );
    typer = typer.inclusion( JsonTypeInfo.As.PROPERTY );
    typer = typer.typeProperty( "javaClass" );
    if ( applyFilter && filterProvider != null ) {
      objectMapper.setFilters( filterProvider );
      objectMapper.setAnnotationIntrospector( new JacksonAnnotationIntrospector() {
        @Override public Object findFilterId( AnnotatedClass ac ) {
          return ac.getAnnotated();
        }
      } );
      objectMapper.setSerializerFactory( BeanSerializerFactory.instance );
    }
    objectMapper.configure( SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false );
    objectMapper.configure( DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false );
    return objectMapper.setDefaultTyping( typer );
  }

  public JacksonJaxbJsonProvider createProvider() {
    return createProvider( true );
  }

  public JacksonJaxbJsonProvider createProvider( boolean applyFilter ) {
    return new JacksonJaxbJsonProvider( createMapper( applyFilter ), JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS );
  }
}
