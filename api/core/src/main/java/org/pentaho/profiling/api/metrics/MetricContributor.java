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

import org.pentaho.profiling.api.MutableProfileStatus;
import org.pentaho.profiling.api.ProfileFieldProperty;
import org.pentaho.profiling.api.ProfileStatus;
import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;

import java.util.List;

/**
 * Interface to metric contributors
 * <p/>
 * Created by mhall on 23/01/15.
 */
public interface MetricContributor {

  /**
   * Process a field value
   *
   * @param manager the data source field manager for looking up paths, metrics for types etc.
   * @param values  the actual data value provided by a data source (along with any relevant metadata)
   * @throws ProfileActionException if a problem occurs
   */
  void processFields( MutableProfileStatus mutableProfileStatus, List<DataSourceFieldValue> values )
    throws ProfileActionException;

  /**
   * Sets the derived statistics on a field (useful when they are expensive as in HyperLogLogPlus
   *
   * @param dataSourceFieldManager
   * @throws ProfileActionException
   */
  void setDerived( MutableProfileStatus mutableProfileStatus ) throws ProfileActionException;

  /**
   * Merge a new manager's values in
   *
   * @param into the existing manager
   * @param from an update to merge in
   */
  void merge( MutableProfileStatus into, ProfileStatus from ) throws MetricMergeException;

  /**
   * Get a list of profile field properties for the metrics computed by this contributor
   *
   * @return a list of profile field properties
   */
  List<ProfileFieldProperty> getProfileFieldProperties();
}
