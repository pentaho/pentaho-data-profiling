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
import java.util.List;
import java.util.Map;

/**
 * Class that manages a list of DataSourceFields and provides access to them,
 * and the DataSourceMetricManagers they encapsulate, in various ways
 * <p/>
 * Created by mhall on 23/01/15.
 */
public class DataSourceFieldManager {

  /**
   * The list of data source fields
   */
  private List<DataSourceField> dataSourceFields;

  /**
   * Have a dedicated lookup for path -> DataSourceField since
   * MetricContributors hit this in every call to processFields()
   */
  private Map<String, DataSourceField> pathToDataSourceFieldMap;

  public DataSourceFieldManager() {
    this( new ArrayList<ProfilingField>() );
  }

  /**
   * Construct a DataSourceFieldManager from a list of ProfilingFields
   *
   * @param profilingFields the list of ProfilingFields to initialize from
   */
  public DataSourceFieldManager( List<ProfilingField> profilingFields ) {
    dataSourceFields = new ArrayList<DataSourceField>();
    pathToDataSourceFieldMap = new HashMap<String, DataSourceField>();
    for ( ProfilingField f : profilingFields ) {
      DataSourceField dsf = new DataSourceField( f );
      dataSourceFields.add( dsf );
      pathToDataSourceFieldMap.put( dsf.getPhysicalName(), dsf );
    }
  }

  /**
   * Get a list of ProfilingFields for the DataSourceFields managed by this
   * instance
   *
   * @return a list of ProfilingField
   */
  public List<ProfilingField> getProfilingFields() {
    List<ProfilingField> result = new ArrayList<ProfilingField>();
    for ( DataSourceField dsf : dataSourceFields ) {
      result.add( dsf.getProfilingField() );
    }
    return result;
  }

  /**
   * Get a map of DataSouceMetricManagers for the given type, taken from all
   * DataSourceFields managed by this instance. Map is keyed by path.
   *
   * @param typeName the type to get DataSourceMetricManagers for
   * @return a map of DataSourceMetricManager keyed by path
   */
  public Map<String, DataSourceMetricManager> getPathToMetricManagerForTypeMap( String typeName ) {
    Map<String, DataSourceMetricManager> result = new HashMap<String, DataSourceMetricManager>();
    for ( DataSourceField dsf : dataSourceFields ) {
      DataSourceMetricManager mm = dsf.getMetricManagerForType( typeName );
      if ( mm != null ) {
        result.put( dsf.getPhysicalName(), mm );
      }
    }
    return result;
  }

  /**
   * Get a map of DataSourceFields keyed by path
   *
   * @return a map of DataSourceFields keyd by path
   */
  public Map<String, DataSourceField> getPathToDataSourceFieldMap() {
    /*
     * Map<String, DataSourceField> result = new HashMap<String,
     * DataSourceField>(); for (DataSourceField dsf : dataSourceFields) {
     * result.put(dsf.getPhysicalName(), dsf); }
     * 
     * return result;
     */
    return pathToDataSourceFieldMap;
  }

  /**
   * Get a map of DataSourceFields for the given field property key. The map is
   * keyed by the corresponding field property value from each DataSourceField
   *
   * @param fieldPropertyKey the field property key name
   * @return a map of DataSourceFeilds keyed by field property value
   */
  public Map<String, DataSourceField> getFieldPropertyToDataSourceFieldMap( String fieldPropertyKey ) {
    Map<String, DataSourceField> result = new HashMap<String, DataSourceField>();
    for ( DataSourceField dsf : dataSourceFields ) {
      result.put( dsf.getFieldProperty( fieldPropertyKey ).toString(), dsf );
    }

    return result;
  }

  /**
   * Get a map of DataSourceMetricManagers for the given field type, taken from
   * all DataSourceFields managed by this instance. The map is keyed by the
   * property field value corresponding to the supplied field property key.
   *
   * @param fieldPropertyKey the field property key
   * @param typeName         the field type
   * @return a map of DataSourceMetricManagers keyed by field property value
   */
  public Map<String, DataSourceMetricManager> getFieldPropertyToMetricManagerForTypeMap( String fieldPropertyKey,
      String typeName ) {
    Map<String, DataSourceMetricManager> result = new HashMap<String, DataSourceMetricManager>();
    for ( DataSourceField dsf : dataSourceFields ) {
      DataSourceMetricManager mm = dsf.getMetricManagerForType( typeName );
      if ( mm != null ) {
        result.put( dsf.getFieldProperty( fieldPropertyKey ).toString(), mm );
      }
    }

    return result;
  }

  /**
   * Add a DataSourceField to this manager
   *
   * @param dataSourceField the DataSourceField to add
   */
  public void addDataSourceField( DataSourceField dataSourceField ) {
    dataSourceFields.add( dataSourceField );
    pathToDataSourceFieldMap.put( dataSourceField.getPhysicalName(), dataSourceField );
  }

  /**
   * Get the DataSourceFields managed by this manager
   *
   * @return a list of DataSourceFields
   */
  public List<DataSourceField> getDataSourceFields() {
    return new ArrayList<DataSourceField>( dataSourceFields );
  }
}
