/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
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

package com.pentaho.profiling.api;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bryan on 7/31/14.
 *
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 */
@XmlRootElement
public class ProfilingField {
  private Map<String, Object> values;

  public ProfilingField() {
    this( new HashMap<String, Object>() );
  }

  public ProfilingField( Map<String, Object> values ) {
    this.values = copyMap( values );
  }

  public Map<String, Object> getValues() {
    return copyMap( values );
  }

  @SuppressWarnings( "unchecked" )
  private Object copyObject( Object value ) {
    if ( value == null ) {
      return null;
    } else if ( value instanceof Map ) {
      return copyMap( (Map<String, Object>) value );
    } else if ( value instanceof Collection ) {
      return copyCollection( (Collection<Object>) value );
    } else {
      return value;
    }
  }

  private Map<String, Object> copyMap( Map<String, Object> map ) {
    Map<String, Object> result = new HashMap<String, Object>( map.size() );
    for ( Map.Entry<String, Object> entry : map.entrySet() ) {
      result.put( entry.getKey(), copyObject( entry.getValue() ) );
    }
    return result;
  }

  @SuppressWarnings( "unchecked" )
  private Collection<Object> copyCollection( Collection<Object> collection ) {
    try {
      Collection<Object> result = collection.getClass().newInstance();
      for ( Object value : collection ) {
        result.add( copyObject( value ) );
      }
      return result;
    } catch ( Exception e ) {
      throw new RuntimeException( e );
    }
  }
}
