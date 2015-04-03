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

import com.pentaho.profiling.api.classes.HasClasses;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.jsontype.NamedType;
import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.codehaus.jackson.map.jsontype.impl.ClassNameIdResolver;
import org.codehaus.jackson.map.jsontype.impl.StdTypeResolverBuilder;
import org.codehaus.jackson.type.JavaType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bryan on 3/27/15.
 */
public class ObjectMapperFactory {
  private final List<HasClasses> hasClassesList = new ArrayList<HasClasses>();
  private final ClassLoader defaultClassLoader;
  private volatile Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();

  public ObjectMapperFactory( ClassLoader defaultClassLoader ) {
    this.defaultClassLoader = defaultClassLoader;
  }

  public ObjectMapperFactory( Class defaultClassLoaderClass ) {
    this( defaultClassLoaderClass.getClassLoader() );
  }

  public void hasClassesAdded( HasClasses hasClasses, Map properties ) {
    synchronized ( hasClassesList ) {
      hasClassesList.add( hasClasses );
      updateClassMap();
    }
  }

  public void hasClassesRemoved( HasClasses hasClasses, Map properties ) {
    synchronized ( hasClassesList ) {
      int numFound = 0;
      while ( hasClassesList.remove( hasClasses ) ) {
        numFound++;
      }
      updateClassMap();
    }
  }

  private void updateClassMap() {
    final Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
    for ( HasClasses hasClasses : hasClassesList ) {
      for ( Class clazz : hasClasses.getClasses() ) {
        classMap.put( clazz.getCanonicalName(), clazz );
      }
    }
    this.classMap = classMap;
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

            private JavaType loadWithClassLoader( String id, ClassLoader classLoader, boolean suppressException ) {
              ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
              try {
                Thread.currentThread().setContextClassLoader( classLoader );
                return super.typeFromId( id );
              } catch ( RuntimeException ex ) {
                if ( suppressException ) {
                  return null;
                } else {
                  throw ex;
                }
              } finally {
                Thread.currentThread().setContextClassLoader( contextClassLoader );
              }
            }

            @Override public JavaType typeFromId( String id ) {
              JavaType result = loadWithClassLoader( id, defaultClassLoader, true );
              if ( result == null ) {
                Class<?> clazz = classMap.get( id );
                if ( clazz != null ) {
                  result = loadWithClassLoader( id, clazz.getClassLoader(), true );
                }
              }
              if ( result == null ) {
                result = loadWithClassLoader( id, _baseType.getRawClass().getClassLoader(), false );
              }
              return result;
            }
          };
        }
      };
    typer = typer.init( JsonTypeInfo.Id.CLASS, null );
    typer = typer.inclusion( JsonTypeInfo.As.PROPERTY );
    typer = typer.typeProperty( "javaClass" );
    return objectMapper.setDefaultTyping( typer );
  }

  public JacksonJaxbJsonProvider createProvider() {
    return new JacksonJaxbJsonProvider( createMapper(), JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS );
  }
}
