package com.pentaho.profiling.api.util;

import com.pentaho.profiling.api.PublicCloneable;

import java.util.ArrayList;
import java.util.HashMap;
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
