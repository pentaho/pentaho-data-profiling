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

import org.codehaus.jackson.map.ser.BeanPropertyFilter;
import org.codehaus.jackson.map.ser.FilterProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bryan on 4/29/15.
 */
public class HasFilterProvider extends FilterProvider {
  private static final HasFilterProvider instance = new HasFilterProvider();
  private final Map<Class<?>, BeanPropertyFilter> classesWithFilters =
    new ConcurrentHashMap<Class<?>, BeanPropertyFilter>();

  private HasFilterProvider() {

  }

  public static HasFilterProvider getInstance() {
    return instance;
  }

  public void hasFilterAdded( HasFilter hasFilter, Map properties ) {
    if ( hasFilter != null ) {
      classesWithFilters.put( hasFilter.getClazz(), hasFilter.getFilter() );
    }
  }

  public void hasFilterRemoved( HasFilter hasFilter, Map properties ) {
    if ( hasFilter != null ) {
      classesWithFilters.remove( hasFilter.getClazz() );
    }
  }

  @Override public BeanPropertyFilter findFilter( Object filterId ) {
    return classesWithFilters.get( filterId );
  }
}
