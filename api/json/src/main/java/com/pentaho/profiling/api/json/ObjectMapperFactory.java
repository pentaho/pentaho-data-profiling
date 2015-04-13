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

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.jsontype.NamedType;
import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.codehaus.jackson.map.jsontype.impl.ClassNameIdResolver;
import org.codehaus.jackson.map.jsontype.impl.StdTypeResolverBuilder;
import org.codehaus.jackson.type.JavaType;

import java.util.Collection;

/**
 * Created by bryan on 3/27/15.
 */
public class ObjectMapperFactory {
  private volatile ClassLoader classLoader;

  /**
   * Facilitate unit testing
   *
   * @param classLoader
   */
  public ObjectMapperFactory( ClassLoader classLoader ) {
    this.classLoader = classLoader;
  }

  public ObjectMapper createMapper() {
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
      };
    typer = typer.init( JsonTypeInfo.Id.CLASS, null );
    typer = typer.inclusion( JsonTypeInfo.As.PROPERTY );
    typer = typer.typeProperty( "javaClass" );
    objectMapper.configure( SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false );
    objectMapper.configure( DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false );
    return objectMapper.setDefaultTyping( typer );
  }

  public JacksonJaxbJsonProvider createProvider() {
    return new JacksonJaxbJsonProvider( createMapper(), JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS );
  }
}
