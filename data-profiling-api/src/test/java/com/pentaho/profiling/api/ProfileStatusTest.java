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
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


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

  @Test
  public void testSetFieldsNull() {
    ProfileStatus profileStatus = new ProfileStatus();
    assertNull( profileStatus.getFields() );
    profileStatus.setFields( null );
    assertNull( profileStatus.getFields() );
  }

  @Test
  public void testSetFields() {
    ProfilingField initial = mock( ProfilingField.class );
    ProfilingField copy = mock( ProfilingField.class );
    ProfilingField copy2 = mock( ProfilingField.class );
    when( initial.copy() ).thenReturn( copy );
    when( copy.copy() ).thenReturn( copy2 );
    ProfileStatus profileStatus = new ProfileStatus();
    profileStatus.setFields( Arrays.asList( initial ) );
    List<ProfilingField> fields = profileStatus.getFields();
    assertEquals( 1, fields.size() );
    assertEquals( copy2, fields.get( 0 ) );
  }

  @Test
  public void testSetTotalEntries() {
    Long totalEntries = 101L;
    ProfileStatus profileStatus = new ProfileStatus();
    profileStatus.setTotalEntities( totalEntries );
    assertEquals( totalEntries, profileStatus.getTotalEntities() );
  }

  @Test
  public void testSetCurrentOperation() {
    String operation = "test-operation";
    ProfileStatus profileStatus = new ProfileStatus();
    profileStatus.setCurrentOperation( operation );
    assertEquals( operation, profileStatus.getCurrentOperation() );
  }

  @Test
  public void testSetCurrentOperationVariables() {
    String variable = "test-variable";
    ProfileStatus profileStatus = new ProfileStatus();
    profileStatus.setCurrentOperationVariables( Arrays.asList( variable ) );
    List<String> variables = profileStatus.getCurrentOperationVariables();
    assertEquals( 1, variables.size() );
    assertEquals( variable, variables.get( 0 ) );
  }

  @Test
  public void testSetProfileFieldProperties() {
    ProfileFieldProperty property = mock( ProfileFieldProperty.class );
    List<ProfileFieldProperty> profileFieldProperties = new ArrayList<ProfileFieldProperty>( Arrays.asList( property ) );
    ProfileStatus profileStatus = new ProfileStatus();
    profileStatus.setProfileFieldProperties( profileFieldProperties );
    assertEquals( profileFieldProperties, profileStatus.getProfileFieldProperties() );
  }

  @Test
  public void testGetCurrentOperationPath() {
    String path = "test-path";
    ProfileStatus profileStatus = new ProfileStatus();
    profileStatus.setCurrentOperationPath( path );
    assertEquals( path, profileStatus.getCurrentOperationPath() );
  }
}
