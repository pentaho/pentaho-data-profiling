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

package com.pentaho.profiling.api.metrics;

import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;

import java.util.List;
import java.util.Set;

/**
 * Interface to allow for easy implementation of metric contributors that only care about one field (and one type) at a
 * time
 */
public interface MetricManagerContributor {
  /**
   * Gets the name that will be shown to the user for the metric contributor
   *
   * @return the name that will be shown to the user for the metric contributor
   */
  public String getName();

  /**
   * Sets the name that will be shown to the user for the metric contributor
   *
   * @param name the name that will be shown to the user for the metric contributor
   */
  public void setName( String name );

  /**
   * Get the types that this contributor can process
   *
   * @return the types that this contributor can process
   */
  public Set<String> supportedTypes();

  /**
   * Integrate the DataSourceFieldValue into the metric calculated in the DataSourceMetricManager
   *
   * @param dataSourceMetricManager the DataSourceFieldValue
   * @param dataSourceFieldValue    the DataSourceMetricManager
   * @throws ProfileActionException
   */
  public void process( DataSourceMetricManager dataSourceMetricManager, DataSourceFieldValue dataSourceFieldValue )
    throws ProfileActionException;

  /**
   * Clears state created by this contributor
   *
   * @param dataSourceMetricManager the DataSourceMetricManager to clear
   */
  public void clear( DataSourceMetricManager dataSourceMetricManager );

  /**
   * Merges the DataSourceMetricManager from into the DataSourceMetricManager into (This operation modifies the into
   * argument)
   *
   * @param into the DataSourceMetricManager to merge into
   * @param from the DataSourceMetricManager to merge from
   * @throws MetricMergeException
   */
  public void merge( DataSourceMetricManager into, DataSourceMetricManager from ) throws MetricMergeException;

  /**
   * Sets the derived statistics on a field (useful when they are expensive as in HyperLogLogPlus
   *
   * @param dataSourceMetricManager
   * @throws ProfileActionException
   */
  public void setDerived( DataSourceMetricManager dataSourceMetricManager ) throws ProfileActionException;

  /**
   * Gets the ProfileFieldProperty objects that describe what the contributor is contributing
   *
   * @return the ProfileFieldProperty objects that describe what the contributor is contributing
   */
  public List<ProfileFieldProperty> profileFieldProperties();
}
