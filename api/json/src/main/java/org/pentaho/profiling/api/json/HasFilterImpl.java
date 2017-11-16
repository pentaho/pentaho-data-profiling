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
