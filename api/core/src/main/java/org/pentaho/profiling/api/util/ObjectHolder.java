/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
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

package org.pentaho.profiling.api.util;

/**
 * Created by bryan on 9/16/14.
 */
public class ObjectHolder<T> {
  private T object;

  public ObjectHolder() {
    this( null );
  }

  public ObjectHolder( T object ) {
    this.object = object;
  }

  public T getObject() {
    return object;
  }

  public void setObject( T object ) {
    this.object = object;
  }
}
