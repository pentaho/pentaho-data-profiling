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

package org.pentaho.profiling.api.util;

import org.pentaho.profiling.api.PublicCloneable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bryan on 4/30/15.
 */
public class PublicCloneableUtil {
  public static <T extends PublicCloneable> void copyMap( Map<String, T> map, Map<String, T> into ) {
    if ( map == null ) {
      return;
    }
    for ( Map.Entry<String, T> stringTEntry : map.entrySet() ) {
      T value = stringTEntry.getValue();
      into.put( stringTEntry.getKey(), value == null ? null : (T) value.clone() );
    }
  }

  public static <T extends PublicCloneable> List<T> copyList( List<T> list ) {
    if ( list == null ) {
      return null;
    }
    List<T> result = new ArrayList<T>( list.size() );
    for ( T t : list ) {
      result.add( (T) t.clone() );
    }
    return result;
  }
}
