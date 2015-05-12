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
