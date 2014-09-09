/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
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

package com.pentaho.profiling.api;

import com.pentaho.profiling.api.datasource.DataSourceReference;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * Created by bryan on 8/14/14.
 */
public class ProfileStatusTest {
  @Test
  public void testSetId() {
    String id = "ID_VALUE";
    ProfileStatus profileStatus = new ProfileStatus();
    profileStatus.setId( id );
    assertEquals( id, profileStatus.getId() );
  }

  @Test
  public void testSetDataSourceReference() {
    String id = "ID_VALUE";
    String dataSourcePovider = "PROVIDER_VALUE";
    ProfileStatus profileStatus = new ProfileStatus();
    DataSourceReference dataSourceReference = new DataSourceReference( id, dataSourcePovider );
    profileStatus.setDataSourceReference( dataSourceReference );
    assertEquals( dataSourceReference, profileStatus.getDataSourceReference() );
  }

  /*@Test
  public void testSetFields() {
    ProfilingField profilingField = new ProfilingField();
    profilingField.getValues().setName( "TEST" );
    List<ProfilingField> fields = new ArrayList<ProfilingField>( Arrays.asList( profilingField ) );
    ProfileStatus profileStatus = new ProfileStatus();
    profileStatus.setFields( fields );
    assertEquals( fields, profileStatus.getFields() );
  }*/

  @Test
  public void testSetTotalEntries() {
    Long totalEntries = 101L;
    ProfileStatus profileStatus = new ProfileStatus();
    profileStatus.setTotalEntities( totalEntries );
    assertEquals( totalEntries, profileStatus.getTotalEntities() );
  }
}
