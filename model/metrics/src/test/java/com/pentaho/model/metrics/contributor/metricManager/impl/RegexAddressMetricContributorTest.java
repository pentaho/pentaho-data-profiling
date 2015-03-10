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

import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import com.pentaho.profiling.api.action.ProfileActionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by mhall on 28/01/15.
 */
public class RegexAddressMetricContributorTest {

  @Test public void testProcessField() throws ProfileActionException {
    DataSourceMetricManager dataSourceMetricManager = new DataSourceMetricManager();
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( "A@B.net" );

    RegexAddressMetricContributor regexAddressMetricContributor = new RegexAddressMetricContributor();
    regexAddressMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( "fred" );
    regexAddressMetricContributor.process( dataSourceMetricManager, dataSourceFieldValue );
    assertEquals( Long.valueOf( 1L ),
      dataSourceMetricManager.getValueNoDefault( RegexAddressMetricContributor.EMAIL_ADDRESS_KEY ) );
  }

  @Test
  public void testMerge() throws MetricMergeException {
    DataSourceMetricManager into = new DataSourceMetricManager();
    DataSourceMetricManager from = new DataSourceMetricManager();
    into.setValue( 5L, RegexAddressMetricContributor.EMAIL_ADDRESS_KEY );
    from.setValue( 15L, RegexAddressMetricContributor.EMAIL_ADDRESS_KEY );
    new RegexAddressMetricContributor().merge( into, from );
    assertEquals( 20L, into.getValueNoDefault( RegexAddressMetricContributor.EMAIL_ADDRESS_KEY ) );
  }

  @Test
  public void testGetProfileFieldProperties() {
    assertNotNull( new RegexAddressMetricContributor().profileFieldProperties() );
  }

  @Test
  public void testGetTypes() {
    assertNotNull( new RegexAddressMetricContributor().supportedTypes() );
  }

  /*@Test
  public void testClear() {
    DataSourceMetricManager dataSourceMetricManager = mock( DataSourceMetricManager.class );
    new RegexAddressMetricContributor().clear( dataSourceMetricManager );
    verify( dataSourceMetricManager ).clear( RegexAddressMetricContributor.CLEAR_LIST );
  }*/
}
