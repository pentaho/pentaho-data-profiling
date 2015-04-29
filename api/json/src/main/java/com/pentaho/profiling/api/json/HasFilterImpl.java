package com.pentaho.profiling.api.json;

import org.codehaus.jackson.map.ser.BeanPropertyFilter;

/**
 * Created by bryan on 4/29/15.
 */
public class HasFilterImpl implements HasFilter {
  private final Class clazz;
  private final BeanPropertyFilter beanPropertyFilter;

  public HasFilterImpl( Class clazz, BeanPropertyFilter beanPropertyFilter ) {
    this.clazz = clazz;
    this.beanPropertyFilter = beanPropertyFilter;
  }

  @Override public Class getClazz() {
    return clazz;
  }

  @Override public BeanPropertyFilter getFilter() {
    return beanPropertyFilter;
  }
}
