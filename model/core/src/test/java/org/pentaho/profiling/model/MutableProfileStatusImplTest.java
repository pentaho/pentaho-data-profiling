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

package org.pentaho.profiling.model;

import org.pentaho.profiling.api.MutableProfileStatus;
import org.pentaho.profiling.api.ProfileField;
import org.pentaho.profiling.api.ProfileFieldProperty;
import org.pentaho.profiling.api.ProfileState;
import org.pentaho.profiling.api.ProfileStatus;
import org.pentaho.profiling.api.ProfileStatusMessage;
import org.pentaho.profiling.api.action.ProfileActionExceptionWrapper;
import org.pentaho.profiling.api.configuration.ProfileConfiguration;
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
    List<ProfileField> fieldList = new ArrayList<ProfileField>();
    ProfileField mockField = mock( ProfileField.class );
    when( mockField.getPhysicalName() ).thenReturn( "testPname" );
    when( mockField.clone() ).thenReturn( mockField );
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
    assertEquals( mockField.getPhysicalName(), profileStatus.getFields().get( 0 ).getPhysicalName() );
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
