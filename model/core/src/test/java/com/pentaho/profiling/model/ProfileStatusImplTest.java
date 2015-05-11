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

import com.pentaho.profiling.api.ProfileField;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileState;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.ProfileStatusMessage;
import com.pentaho.profiling.api.action.ProfileActionExceptionWrapper;
import com.pentaho.profiling.api.configuration.ProfileConfiguration;
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
public class ProfileStatusImplTest {
  @Test
  public void testIdDatasourceReferenceConstructor() {
    String id = "test-id";
    String name = "test-name";
    ProfileConfiguration profileConfiguration = mock( ProfileConfiguration.class );
    ProfileStatusImpl profileStatus = new ProfileStatusImpl( id, name, profileConfiguration );
    assertEquals( id, profileStatus.getId() );
    assertEquals( name, profileStatus.getName() );
    assertEquals( profileConfiguration, profileStatus.getProfileConfiguration() );
    assertEquals( 0L, profileStatus.getSequenceNumber() );
  }

  @Test
  public void testIdDatasourceReferenceSequenceConstructor() {
    String id = "test-id";
    String name = "test-name";
    long sequence = 99L;
    ProfileConfiguration profileConfiguration = mock( ProfileConfiguration.class );
    ProfileStatusImpl profileStatus = new ProfileStatusImpl( id, name, profileConfiguration, sequence );
    assertEquals( id, profileStatus.getId() );
    assertEquals( name, profileStatus.getName() );
    assertEquals( profileConfiguration, profileStatus.getProfileConfiguration() );
    assertEquals( sequence, profileStatus.getSequenceNumber() );
  }

  @Test
  public void testFullConstructor() {
    List<ProfileField> fieldList = new ArrayList<ProfileField>();
    ProfileField mockField = mock( ProfileField.class );
    when( mockField.getPhysicalName() ).thenReturn( "testPname" );
    when( mockField.clone() ).thenReturn( mockField );
    fieldList.add( mockField );
    Long totalEntities = 100L;
    List profileStatusMessage = mock( List.class );
    ProfileActionExceptionWrapper profileActionExceptionWrapper = mock( ProfileActionExceptionWrapper.class );
    List<ProfileFieldProperty> profileFieldProperties = new ArrayList<ProfileFieldProperty>();
    ProfileFieldProperty profileFieldProperty = mock( ProfileFieldProperty.class );
    profileFieldProperties.add( profileFieldProperty );
    String id = "test-id";
    String name = "test-name";
    long sequence = 99L;
    ProfileConfiguration profileConfiguration = mock( ProfileConfiguration.class );
    ProfileStatusImpl profileStatus =
      new ProfileStatusImpl( ProfileState.ACTIVE, fieldList, totalEntities, profileStatusMessage,
        profileActionExceptionWrapper,
        profileFieldProperties, id, profileConfiguration, sequence, name );
    assertEquals( 1, profileStatus.getFields().size() );
    assertEquals( mockField, profileStatus.getFields().get( 0 ) );
    assertEquals( totalEntities, profileStatus.getTotalEntities() );
    assertEquals( profileStatusMessage, profileStatus.getStatusMessages() );
    assertEquals( profileActionExceptionWrapper, profileStatus.getOperationError() );
    assertEquals( profileFieldProperties, profileStatus.getProfileFieldProperties() );
    assertEquals( id, profileStatus.getId() );
    assertEquals( name, profileStatus.getName() );
    assertEquals( profileConfiguration, profileStatus.getProfileConfiguration() );
    assertEquals( sequence, profileStatus.getSequenceNumber() );
    assertEquals( ProfileState.ACTIVE, profileStatus.getProfileState() );
  }

  @Test
  public void testCopyConstructor() {
    List<ProfileField> fieldList = new ArrayList<ProfileField>();
    ProfileField mockField = mock( ProfileField.class );
    when( mockField.getPhysicalName() ).thenReturn( "testPname" );
    when( mockField.clone() ).thenReturn( mockField );
    fieldList.add( mockField );
    ProfileState profileState = ProfileState.DISCARDED;
    Long totalEntities = 150L;
    ProfileStatusMessage profileStatusMessage = mock( ProfileStatusMessage.class );
    ProfileActionExceptionWrapper profileActionExceptionWrapper = mock( ProfileActionExceptionWrapper.class );
    List<ProfileFieldProperty> profileFieldProperties = new ArrayList<ProfileFieldProperty>();
    ProfileFieldProperty profileFieldProperty = mock( ProfileFieldProperty.class );
    profileFieldProperties.add( profileFieldProperty );
    String id = "test-id";
    long sequence = 99L;
    ProfileConfiguration profileConfiguration = mock( ProfileConfiguration.class );
    ProfileStatus profileStatusToCopy = mock( ProfileStatus.class );
    when( profileStatusToCopy.getFields() ).thenReturn( fieldList );
    when( profileStatusToCopy.getTotalEntities() ).thenReturn( totalEntities );
    when( profileStatusToCopy.getStatusMessages() ).thenReturn( Arrays.asList( profileStatusMessage ) );
    when( profileStatusToCopy.getOperationError() ).thenReturn( profileActionExceptionWrapper );
    when( profileStatusToCopy.getProfileFieldProperties() ).thenReturn( profileFieldProperties );
    when( profileStatusToCopy.getId() ).thenReturn( id );
    when( profileStatusToCopy.getProfileConfiguration() ).thenReturn( profileConfiguration );
    when( profileStatusToCopy.getSequenceNumber() ).thenReturn( sequence );
    when( profileStatusToCopy.getProfileState() ).thenReturn( profileState );
    ProfileStatusImpl profileStatus =
      new ProfileStatusImpl( profileStatusToCopy );
    assertEquals( 1, profileStatus.getFields().size() );
    assertEquals( mockField, profileStatus.getFields().get( 0 ) );
    assertEquals( totalEntities, profileStatus.getTotalEntities() );
    assertEquals( 1, profileStatus.getStatusMessages().size() );
    assertEquals( profileStatusMessage, profileStatus.getStatusMessages().get( 0 ) );
    assertEquals( profileActionExceptionWrapper, profileStatus.getOperationError() );
    assertEquals( profileFieldProperties, profileStatus.getProfileFieldProperties() );
    assertEquals( id, profileStatus.getId() );
    assertEquals( profileConfiguration, profileStatus.getProfileConfiguration() );
    assertEquals( sequence + 1, profileStatus.getSequenceNumber() );
    assertEquals( profileState, profileStatus.getProfileState() );
  }
}
