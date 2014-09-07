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

import com.pentaho.profiling.api.stats.Statistic;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by bryan on 7/31/14.
 *
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 */
@XmlRootElement
public class ProfilingField {

  /*public static final String ARRAY = "ARRAY"; //$NON-NLS-1$
  public static final String DOCUMENT = "DOCUMENT"; //$NON-NLS-1$
  *//**
   * The full path of the field (including the terminal name part)
   *//*
  protected String path;
  *//**
   * True if this field is a leaf
   *//*
  protected boolean leaf;
  *//**
   * True if this field is a document
   *//*
  protected boolean document;
  *//**
   * True if this field is an array
   *//*
  protected boolean array;
  *//**
   * The known types for this field (and potentially their counts/stats)
   *//*
  protected Map<String, ProfilingFieldType> types = new TreeMap<String, ProfilingFieldType>();
  *//**
   * The name of the field
   *//*
  private String name;*/

  private Map<String, Object> values;

  public ProfilingField() {
    this( new HashMap<String, Object>(  ) );
  }

  public ProfilingField( Map<String, Object> values ) {
    this.values = values;
  }

  public Map<String, Object> getValues() {
    return values;
  }

  public void setValues( Map<String, Object> values ) {
    this.values = values;
  }

  public ProfilingField copy() {
    return new ProfilingField( copyMap( values ) );
  }

  private Object copyObject( Object value ) {
    if ( value == null ) {
      return null;
    } else if ( value instanceof Map ) {
      return copyMap( (Map<String, Object>) value );
    } else if ( value instanceof Collection ) {
      return copyCollection( (Collection<Object>) value );
    } else if ( value instanceof Statistic ) {
      Statistic result = null;
      try {
        result = (Statistic) value.getClass().newInstance();
      } catch ( Exception e ) {
        throw new RuntimeException( e );
      }
      result.setValue( ( (Statistic) value ).getValue() );
      return result;
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
