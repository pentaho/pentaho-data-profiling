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

import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;

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
