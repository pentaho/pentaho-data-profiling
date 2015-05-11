package com.pentaho.profiling.api.json;

import org.codehaus.jackson.map.ser.BeanPropertyFilter;

/**
 * Created by bryan on 4/29/15.
 */
public interface HasFilter {
  Class<?> getClazz();
  BeanPropertyFilter getFilter();
}
