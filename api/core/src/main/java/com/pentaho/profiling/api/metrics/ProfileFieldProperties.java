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

import com.pentaho.profiling.api.MessageUtils;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.metrics.field.DataSourceField;
import com.pentaho.profiling.api.stats.Statistic;

import java.util.Arrays;

/**
 * Created by bryan on 2/9/15.
 */
public class ProfileFieldProperties {
  public static final String KEY = "profiling-metrics-api";
  public static final String KEY_PATH = MessageUtils.getId( KEY, ProfileFieldProperties.class );
  public static final ProfileFieldProperty LOGICAL_NAME = new ProfileFieldProperty( KEY_PATH,
    DataSourceField.LOGICAL_NAME, Arrays.asList( DataSourceField.LOGICAL_NAME ) );
  public static final ProfileFieldProperty PHYSICAL_NAME = new ProfileFieldProperty( KEY_PATH,
    DataSourceField.PHYSICAL_NAME, Arrays.asList( DataSourceField.PHYSICAL_NAME ) );
  public static final ProfileFieldProperty FIELD_TYPE =
    new ProfileFieldProperty( KEY_PATH, DataSourceField.TYPE_NAME,
      Arrays.asList( DataSourceField.TYPE, DataSourceField.TYPE_NAME ) );
  public static final ProfileFieldProperty COUNT_FIELD = new ProfileFieldProperty( KEY_PATH, Statistic.COUNT,
    Arrays.asList( DataSourceField.TYPE, Statistic.COUNT ) );
}
