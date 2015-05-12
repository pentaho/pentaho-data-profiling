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

package com.pentaho.model.metrics.contributor.metricManager.impl;

import com.pentaho.model.metrics.contributor.metricManager.impl.metrics.DateHolder;
import com.pentaho.profiling.api.MutableProfileFieldValueType;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileFieldValueType;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mhall on 27/01/15.
 */
public class DateMetricContributor extends BaseMetricManagerContributor implements MetricManagerContributor {
  public static final String SIMPLE_NAME = DateMetricContributor.class.getSimpleName();

  @Override public Set<String> supportedTypes() {
    return new HashSet<String>( Arrays.asList( Date.class.getCanonicalName() ) );
  }

  private DateHolder getOrCreateDateHolder( MutableProfileFieldValueType mutableProfileFieldValueType ) {
    DateHolder result = (DateHolder) mutableProfileFieldValueType.getValueTypeMetrics( SIMPLE_NAME );
    if ( result == null ) {
      result = new DateHolder();
      mutableProfileFieldValueType.setValueTypeMetrics( SIMPLE_NAME, result );
    }
    return result;
  }

  @Override
  public void process( MutableProfileFieldValueType mutableProfileFieldValueType,
                       DataSourceFieldValue dataSourceFieldValue )
    throws ProfileActionException {
    getOrCreateDateHolder( mutableProfileFieldValueType ).offer( (Date) dataSourceFieldValue.getFieldValue() );
  }

  @Override public void merge( MutableProfileFieldValueType into, ProfileFieldValueType from )
    throws MetricMergeException {
    DateHolder fromHolder = (DateHolder) from.getValueTypeMetrics( SIMPLE_NAME );
    if ( fromHolder != null ) {
      DateHolder dateHolder = getOrCreateDateHolder( into );
      dateHolder.offer( fromHolder.getMin() );
      dateHolder.offer( fromHolder.getMax() );
    }
  }

  @Override public List<ProfileFieldProperty> profileFieldProperties() {
    return Arrays.asList( NumericMetricContributor.MIN, NumericMetricContributor.MAX );
  }
}
