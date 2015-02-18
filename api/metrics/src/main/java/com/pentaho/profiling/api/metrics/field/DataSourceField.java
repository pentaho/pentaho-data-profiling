/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2015 Pentaho Corporation (Pentaho). All rights reserved.
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

package com.pentaho.profiling.api.metrics.field;

import com.pentaho.profiling.api.ProfilingField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class representing a field being processed from a data source. Keeps track of types seen for the field and their
 * associated metrics
 * <p/>
 * Created by mhall on 23/01/15.
 */
public class DataSourceField {
  public static final String LOGICAL_NAME = "logicalName";
  public static final String PHYSICAL_NAME = "physicalName";
  public static final String TYPE = "type";
  public static final String TYPE_NAME = "typeName";

  /**
   * values to base this DataSourceField on (typically sourced from a ProfilingField)
   */
  private Map<String, Object> values;

  /**
   * Map of metric managers, keyed by data value type name
   */
  private Map<String, DataSourceMetricManager> metricsByTypeMap;

  /**
   * Construct a new empty DataSourceField
   */
  public DataSourceField() {
    this( new HashMap<String, Object>() );
  }

  /**
   * Construct a DataSourceField from a map of values. metric information associated with different types is extracted
   * (if present).
   *
   * @param values the values to examine for type/metric info
   */
  public DataSourceField( Map<String, Object> values ) {
    this.values = values;
    metricsByTypeMap = new HashMap<String, DataSourceMetricManager>();

    List<Map<String, Object>> typeMaps = (List<Map<String, Object>>) values.get( TYPE );
    if ( typeMaps != null ) {
      for ( Map<String, Object> typeMap : typeMaps ) {
        metricsByTypeMap.put( (String) typeMap.get( TYPE_NAME ), new DataSourceMetricManager( typeMap ) );
      }
    } else {
      // new list of types
      values.put( TYPE, new ArrayList<Map<String, Object>>() );
    }
  }

  /**
   * Construct a DataSourceField from information stored in a ProfilingField (in particular, the metric information for
   * each known type)
   *
   * @param profilingField the ProfilingField to construct from
   */
  public DataSourceField( ProfilingField profilingField ) {
    this( profilingField.getValues() );
  }

  public static Map<String, List<DataSourceField>> typeToFieldMap( List<ProfilingField> profilingFields ) {
    Map<String, List<DataSourceField>> result = new HashMap<String, List<DataSourceField>>();
    for ( ProfilingField profilingField : profilingFields ) {
      DataSourceField dataSourceField = new DataSourceField( profilingField );
      for ( String type : dataSourceField.metricsByTypeMap.keySet() ) {
        List<DataSourceField> fields = result.get( type );
        if ( fields == null ) {
          fields = new ArrayList<DataSourceField>();
          result.put( type, fields );
        }
        fields.add( dataSourceField );
      }
    }
    return result;
  }

  /**
   * Get a list of all the metric managers currently in use by this DataSourceField
   *
   * @return a list of all the metric managers
   */
  public List<DataSourceMetricManager> getMetricManagers() {
    return new ArrayList<DataSourceMetricManager>( metricsByTypeMap.values() );
  }

  public Set<String> getMetricManagerTypes() {
    return new HashSet<String>( metricsByTypeMap.keySet() );
  }

  /**
   * Get the metric manager associated with the named type. Returns null if the named type has not been seen in this
   * field.
   *
   * @param typeName the name of the type to get the associated manager for
   * @return the manager for the type, or null if the type has not been seen by this field
   */
  public DataSourceMetricManager getMetricManagerForType( String typeName ) {
    return getMetricManagerForType( typeName, false );
  }

  /**
   * Get a ProfilingField encapsulating the current state of profiling types seen for this field.
   *
   * @return a ProfilingField
   */
  public ProfilingField getProfilingField() {
    return new ProfilingField( values );
  }

  /**
   * Get the name of this field
   *
   * @return the name of this field
   */
  public String getLogicalName() {
    return (String) values.get( LOGICAL_NAME );
  }

  /**
   * Set the name of this field
   *
   * @param name the name of this field
   */
  public void setLogicalName( String name ) {
    values.put( LOGICAL_NAME, name );
  }

  /**
   * Get the path of this field
   *
   * @return the path of this field, or null if the field is not in a hierarchy.
   */
  public String getPhysicalName() {
    // return (String) values.get( PHYSICAL_NAME );
    return getFieldProperty( PHYSICAL_NAME );
  }

  /**
   * Set the path for this field
   *
   * @param path the path for this field
   */
  public void setPhysicalName( String path ) {
    // values.put( PHYSICAL_NAME, path );
    setFieldProperty( PHYSICAL_NAME, path );
  }

  /**
   * Set a property of this field
   *
   * @param key   the key of the property
   * @param value the value of the property
   * @param <T>   the type of the property value
   */
  public <T> void setFieldProperty( String key, T value ) {
    values.put( key, value );
  }

  /**
   * Get a property of this field
   *
   * @param key the key of the property
   * @param <T> the type of the property value
   * @return the property value, or null if the property is not set
   */
  public <T> T getFieldProperty( String key ) {
    return (T) values.get( key );
  }

  /**
   * Get the metric manager associated with the named type.
   *
   * @param typeName the name of the type to get the associated manager for
   * @param create   true if a manager should be created in the case where the named type has not yet been seen
   * @return the metric manager for tha named type
   */
  public DataSourceMetricManager getMetricManagerForType( String typeName, boolean create ) {
    if ( metricsByTypeMap.containsKey( typeName ) ) {
      return metricsByTypeMap.get( typeName );
    }

    if ( create ) {
      Map<String, Object> typeMap = new HashMap<String, Object>();

      ( (List<Map<String, Object>>) values.get( TYPE ) ).add( typeMap );
      DataSourceMetricManager metricManager = new DataSourceMetricManager( typeMap );
      // store the type being managed by the metric manager
      typeMap.put( TYPE_NAME, typeName );
      metricsByTypeMap.put( typeName, metricManager );

      return metricManager;
    }

    return null;
  }
}
