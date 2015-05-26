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

package com.pentaho.model.metrics.contributor.metricManager.impl;

import com.pentaho.model.metrics.contributor.metricManager.impl.metrics.DateHolder;
import com.pentaho.profiling.api.MutableProfileFieldValueType;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by mhall on 27/01/15.
 */
public class DateMetricContributorTest {
  private DateMetricContributor dateMetricContributor;

  @Before
  public void setup() {
    dateMetricContributor = new DateMetricContributor();
  }

  @Test
  public void testProcessField() throws ProfileActionException, ParseException {
    MutableProfileFieldValueType mutableProfileFieldValueType =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( DateMetricContributor.SIMPLE_NAME );

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );

    Date date1 = simpleDateFormat.parse( "3/4/1005" );
    Date date2 = simpleDateFormat.parse( "4/5/1006" );
    Date date3 = simpleDateFormat.parse( "6/7/1008" );

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( date2 );
    dataSourceFieldValue.setPhysicalName( "a" );

    DateMetricContributor dateMetricContributor = new DateMetricContributor();
    dateMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    DateHolder dateHolder =
      (DateHolder) mutableProfileFieldValueType.getValueTypeMetrics( DateMetricContributor.SIMPLE_NAME );
    assertEquals( date2, dateHolder.getMin() );
    assertEquals( date2, dateHolder.getMax() );
    dataSourceFieldValue.setFieldValue( date1 );
    dateMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    assertEquals( date1, dateHolder.getMin() );
    assertEquals( date2, dateHolder.getMax() );
    dataSourceFieldValue.setFieldValue( date3 );
    dateMetricContributor.process( mutableProfileFieldValueType, dataSourceFieldValue );
    assertEquals( date1, dateHolder.getMin() );
    assertEquals( date3, dateHolder.getMax() );
  }

  @Test
  public void testMerge() throws MetricMergeException {
    MutableProfileFieldValueType into =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CategoricalMetricContributor.SIMPLE_NAME );
    MutableProfileFieldValueType from =
      MetricContributorTestUtils.createMockMutableProfileFieldValueType( CategoricalMetricContributor.SIMPLE_NAME );
    dateMetricContributor.merge( into, from );
    assertNull( into.getValueTypeMetrics( DateMetricContributor.SIMPLE_NAME ) );
  }

  @Test
  public void testGetTypes() {
    assertNotNull( dateMetricContributor.supportedTypes() );
  }

  @Test
  public void testGetProfileFieldProperties() {
    assertNotNull( dateMetricContributor.profileFieldProperties() );
  }
}
