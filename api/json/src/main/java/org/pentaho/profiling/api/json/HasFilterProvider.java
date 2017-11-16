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


import com.fasterxml.jackson.databind.ser.BeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.FilterProvider;

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

  @Override public BeanPropertyFilter findFilter(Object filterId ) {
    return classesWithFilters.get( filterId );
  }
}
