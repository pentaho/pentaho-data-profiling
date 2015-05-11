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

package com.pentaho.profiling.api.json;

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
          return _idType == JsonTypeInfo.Id.NONE ? null :
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
        }

        @Override public TypeDeserializer buildTypeDeserializer( DeserializationConfig config, JavaType baseType,
                                                                 Collection<NamedType> subtypes,
                                                                 BeanProperty property ) {
          return _idType == JsonTypeInfo.Id.NONE ? null :
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
