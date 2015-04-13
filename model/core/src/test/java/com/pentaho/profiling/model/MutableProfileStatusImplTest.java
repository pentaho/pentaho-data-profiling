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

package com.pentaho.profiling.model;

import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileState;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.ProfileStatusMessage;
import com.pentaho.profiling.api.ProfilingField;
import com.pentaho.profiling.api.action.ProfileActionExceptionWrapper;
import com.pentaho.profiling.api.configuration.ProfileConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 9/30/14.
 */
public class MutableProfileStatusImplTest {
  private ProfileStatus constructorArg;

  @Before
  public void setup() {
    constructorArg = mock( ProfileStatus.class );
  }

  @Test
  public void testCopyConstructor() {
    List<ProfilingField> fieldList = new ArrayList<ProfilingField>();
    ProfilingField mockField = mock( ProfilingField.class );
    fieldList.add( mockField );
    Long totalEntities = 150L;
    ProfileStatusMessage profileStatusMessage = mock( ProfileStatusMessage.class );
    ProfileActionExceptionWrapper profileActionExceptionWrapper = mock( ProfileActionExceptionWrapper.class );
    List<ProfileFieldProperty> profileFieldProperties = new ArrayList<ProfileFieldProperty>();
    ProfileFieldProperty profileFieldProperty = mock( ProfileFieldProperty.class );
    profileFieldProperties.add( profileFieldProperty );
    String id = "test-id";
    String name = "test-name";
    long sequence = 99L;
    ProfileConfiguration profileConfiguration = mock( ProfileConfiguration.class );
    when( constructorArg.getFields() ).thenReturn( fieldList );
    when( constructorArg.getTotalEntities() ).thenReturn( totalEntities );
    when( constructorArg.getStatusMessages() ).thenReturn( Arrays.asList( profileStatusMessage ) );
    when( constructorArg.getOperationError() ).thenReturn( profileActionExceptionWrapper );
    when( constructorArg.getProfileFieldProperties() ).thenReturn( profileFieldProperties );
    when( constructorArg.getId() ).thenReturn( id );
    when( constructorArg.getName() ).thenReturn( name );
    when( constructorArg.getProfileConfiguration() ).thenReturn( profileConfiguration );
    when( constructorArg.getSequenceNumber() ).thenReturn( sequence );
    MutableProfileStatusImpl profileStatus =
      new MutableProfileStatusImpl( constructorArg );
    assertEquals( 1, profileStatus.getFields().size() );
    assertEquals( mockField, profileStatus.getFields().get( 0 ) );
    assertEquals( totalEntities, profileStatus.getTotalEntities() );
    assertEquals( profileStatusMessage, profileStatus.getStatusMessages().get( 0 ) );
    assertEquals( profileActionExceptionWrapper, profileStatus.getOperationError() );
    assertEquals( profileFieldProperties, profileStatus.getProfileFieldProperties() );
    assertEquals( id, profileStatus.getId() );
    assertEquals( name, profileStatus.getName() );
    assertEquals( profileConfiguration, profileStatus.getProfileConfiguration() );
    assertEquals( sequence + 1, profileStatus.getSequenceNumber() );
  }

  @Test
  public void testSetFields() {
    MutableProfileStatusImpl profileStatus = new MutableProfileStatusImpl( constructorArg );
    List<ProfilingField> fieldList = new ArrayList<ProfilingField>();
    ProfilingField mockField = mock( ProfilingField.class );
    fieldList.add( mockField );
    profileStatus.setFields( fieldList );
    assertEquals( 1, profileStatus.getFields().size() );
    assertEquals( mockField, profileStatus.getFields().get( 0 ) );
  }

  @Test
  public void testSetName() {
    MutableProfileStatusImpl profileStatus = new MutableProfileStatusImpl( constructorArg );
    String name = "test-name";
    profileStatus.setName( name );
    assertEquals( name, profileStatus.getName() );
  }

  @Test
  public void testSetTotalEntities() {
    MutableProfileStatusImpl profileStatus = new MutableProfileStatusImpl( constructorArg );
    profileStatus.setTotalEntities( 444L );
    assertEquals( Long.valueOf( 444L ), profileStatus.getTotalEntities() );
  }

  @Test
  public void testSetCurrentOperation() {
    ProfileStatusMessage profileStatusMessage = mock( ProfileStatusMessage.class );
    MutableProfileStatusImpl profileStatus = new MutableProfileStatusImpl( constructorArg );
    profileStatus.setStatusMessages( Arrays.asList( profileStatusMessage ) );
    assertEquals( profileStatusMessage, profileStatus.getStatusMessages().get( 0 ) );
  }

  @Test
  public void testSetOperationError() {
    ProfileActionExceptionWrapper profileActionExceptionWrapper = mock( ProfileActionExceptionWrapper.class );
    MutableProfileStatusImpl profileStatus = new MutableProfileStatusImpl( constructorArg );
    profileStatus.setOperationError( profileActionExceptionWrapper );
    assertEquals( profileActionExceptionWrapper, profileStatus.getOperationError() );
  }

  @Test
  public void testSetProfileFieldProperties() {
    List<ProfileFieldProperty> profileFieldProperties = new ArrayList<ProfileFieldProperty>();
    ProfileFieldProperty profileFieldProperty = mock( ProfileFieldProperty.class );
    profileFieldProperties.add( profileFieldProperty );
    MutableProfileStatusImpl profileStatus = new MutableProfileStatusImpl( constructorArg );
    profileStatus.setProfileFieldProperties( profileFieldProperties );
    assertEquals( profileFieldProperties, profileStatus.getProfileFieldProperties() );
  }

  @Test
  public void testSetProfileState() {
    MutableProfileStatus mutableProfileStatus = new MutableProfileStatusImpl( constructorArg );
    mutableProfileStatus.setProfileState( ProfileState.ACTIVE );
    assertEquals( ProfileState.ACTIVE, mutableProfileStatus.getProfileState() );
    mutableProfileStatus.setProfileState( ProfileState.DISCARDED );
    assertEquals( ProfileState.DISCARDED, mutableProfileStatus.getProfileState() );
  }
}
