/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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

package org.pentaho.model.metrics.contributor.metricManager.impl;

import org.pentaho.model.metrics.contributor.metricManager.impl.metrics.DateHolder;
import org.pentaho.profiling.api.MutableProfileFieldValueType;
import org.pentaho.profiling.api.ProfileFieldProperty;
import org.pentaho.profiling.api.ProfileFieldValueType;
import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.metrics.MetricManagerContributor;
import org.pentaho.profiling.api.metrics.MetricMergeException;
import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;

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

  @Override public boolean equals( Object obj ) {
    return obj != null && obj.getClass().equals( DateMetricContributor.class );
  }

  @Override public int hashCode() {
    return DateMetricContributor.class.hashCode();
  }
}
