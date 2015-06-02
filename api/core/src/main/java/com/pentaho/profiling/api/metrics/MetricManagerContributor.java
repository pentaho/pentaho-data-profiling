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

package org.pentaho.profiling.api.metrics;

import org.pentaho.profiling.api.MutableProfileFieldValueType;
import org.pentaho.profiling.api.ProfileFieldProperty;
import org.pentaho.profiling.api.ProfileFieldValueType;
import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;

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
   * @param valueTypeMetricManager the DataSourceFieldValue
   * @param dataSourceFieldValue   the DataSourceMetricManager
   * @throws ProfileActionException
   */
  public void process( MutableProfileFieldValueType mutableProfileFieldValueType,
                       DataSourceFieldValue dataSourceFieldValue )
    throws ProfileActionException;

  /**
   * Merges the DataSourceMetricManager from into the DataSourceMetricManager into (This operation modifies the into
   * argument)
   *
   * @param into the DataSourceMetricManager to merge into
   * @param from the DataSourceMetricManager to merge from
   * @throws MetricMergeException
   */
  public void merge( MutableProfileFieldValueType into, ProfileFieldValueType from ) throws MetricMergeException;

  /**
   * Sets the derived statistics on a field (useful when they are expensive as in HyperLogLogPlus
   *
   * @param valueTypeMetricManager
   * @throws ProfileActionException
   */
  public void setDerived( MutableProfileFieldValueType mutableProfileFieldValueType ) throws ProfileActionException;

  /**
   * Gets the ProfileFieldProperty objects that describe what the contributor is contributing
   *
   * @return the ProfileFieldProperty objects that describe what the contributor is contributing
   */
  public List<ProfileFieldProperty> profileFieldProperties();
}
