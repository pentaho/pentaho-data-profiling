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

package com.pentaho.profiling.model;

import com.pentaho.profiling.api.metrics.bundle.MetricContributorBundle;
import com.pentaho.profiling.api.metrics.mapper.MetricContributorsObjectMapperFactory;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.jsontype.NamedType;
import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.codehaus.jackson.map.jsontype.impl.ClassNameIdResolver;
import org.codehaus.jackson.map.jsontype.impl.StdTypeResolverBuilder;
import org.codehaus.jackson.type.JavaType;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bryan on 3/12/15.
 */
public class MetricContributorsObjectMapperFactoryImpl implements MetricContributorsObjectMapperFactory {
  private final List<MetricContributorBundle> metricContributorBundles;

  public MetricContributorsObjectMapperFactoryImpl( List<MetricContributorBundle> metricContributorBundles ) {
    this.metricContributorBundles = metricContributorBundles;
  }

  @Override public ObjectMapper createObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    final Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
    for ( MetricContributorBundle metricContributorBundle : metricContributorBundles ) {
      for ( final Class clazz : metricContributorBundle.getMetricContributorClasses() ) {
        classMap.put( clazz.getCanonicalName(), clazz );
      }
    }
    StdTypeResolverBuilder typer =
      new ObjectMapper.DefaultTypeResolverBuilder( ObjectMapper.DefaultTyping.NON_FINAL ) {
        @Override
        protected TypeIdResolver idResolver( MapperConfig<?> config, JavaType baseType,
                                             Collection<NamedType> subtypes,
                                             boolean forSer, boolean forDeser ) {
          return new ClassNameIdResolver( baseType, config.getTypeFactory() ) {
            @Override public JavaType typeFromId( String id ) {
              Class<?> clazz = classMap.get( id );
              if ( clazz == null ) {
                clazz = _baseType.getRawClass();
              }
              ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
              try {
                Thread.currentThread().setContextClassLoader( clazz.getClassLoader() );
                return super.typeFromId( id );
              } finally {
                Thread.currentThread().setContextClassLoader( contextClassLoader );
              }
            }
          };
        }
      };
    typer = typer.init( JsonTypeInfo.Id.CLASS, null );
    typer = typer.inclusion( JsonTypeInfo.As.WRAPPER_ARRAY );
    objectMapper.setDefaultTyping( typer );
    return objectMapper;
  }
}
