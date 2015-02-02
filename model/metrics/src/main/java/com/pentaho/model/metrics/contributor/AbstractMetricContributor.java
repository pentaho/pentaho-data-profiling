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

package com.pentaho.model.metrics.contributor;

import com.pentaho.metrics.api.MetricContributor;
import com.pentaho.metrics.api.field.DataSourceFieldManager;
import com.pentaho.metrics.api.field.DataSourceMetricManager;
import com.pentaho.profiling.api.MutableProfileStatus;

import java.util.List;
import java.util.Map;

/**
 * Base class for MetricContributors
 * <p/>
 * Created by mhall on 23/01/15.
 */
public abstract class AbstractMetricContributor implements MetricContributor {

  /**
   * base identifier
   */
  public static final String KEY = "model/metrics";

  protected abstract Map<String, List<String[]>> getClearMap();

  @Override public void clearProfileStatus( MutableProfileStatus mutableProfileStatus ) {
    DataSourceFieldManager
        dataSourceProfilingFieldManager =
        new DataSourceFieldManager( mutableProfileStatus.getFields() );
    for ( Map.Entry<String, List<String[]>> typeToKeysEntry : getClearMap().entrySet() ) {
      for ( DataSourceMetricManager dsf : dataSourceProfilingFieldManager
          .getPathToMetricManagerForTypeMap( typeToKeysEntry.getKey() ).values() ) {
        for ( String[] key : typeToKeysEntry.getValue() ) {
          dsf.setValue( null, key );
        }
      }
    }
  }
}
