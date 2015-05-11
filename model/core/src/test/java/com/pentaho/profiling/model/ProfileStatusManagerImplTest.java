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

import com.pentaho.profiling.api.MutableProfileField;
import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileState;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.ProfileStatusMessage;
import com.pentaho.profiling.api.ProfileStatusReadOperation;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.action.ProfileActionExceptionWrapper;
import com.pentaho.profiling.api.configuration.ProfileConfiguration;
import com.pentaho.profiling.api.util.ObjectHolder;
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
public class ProfileStatusManagerImplTest {
  private ProfilingServiceImpl profilingService;
  private ProfileConfiguration profileConfiguration;
  private String id = "test-id";
  private String name = "test-name";

  @Before
  public void setup() {
    profilingService = mock( ProfilingServiceImpl.class );
    profileConfiguration = mock( ProfileConfiguration.class );
  }

  @Test
  public void testGetId() {
    ProfileStatusManagerImpl profileStatusManager =
      new ProfileStatusManagerImpl( id, name, profileConfiguration, profilingService );
    assertEquals( id, profileStatusManager.getId() );
    assertEquals( name, profileStatusManager.getName() );
  }

  @Test
  public void testGetDataSourceReference() {
    ProfileStatusManagerImpl profileStatusManager =
      new ProfileStatusManagerImpl( id, name, profileConfiguration, profilingService );
    assertEquals( profileConfiguration, profileStatusManager.getProfileConfiguration() );
  }

  @Test
  public void testRead() {
    ProfileStatusManagerImpl profileStatusManager =
      new ProfileStatusManagerImpl( id, name, profileConfiguration, profilingService );
    final ObjectHolder<ProfileConfiguration> dataSourceReferenceObjectHolder = new ObjectHolder<ProfileConfiguration>();
    assertEquals( id, profileStatusManager.read( new ProfileStatusReadOperation<String>() {
      @Override public String read( ProfileStatus profileStatus ) {
        dataSourceReferenceObjectHolder.setObject( profileStatus.getProfileConfiguration() );
        return profileStatus.getId();
      }
    } ) );
    assertEquals( profileConfiguration, dataSourceReferenceObjectHolder.getObject() );
  }

  @Test
  public void testGetFields() {
    ProfileStatusManagerImpl profileStatusManager =
      new ProfileStatusManagerImpl( id, name, profileConfiguration, profilingService );

    final List<MutableProfileField> fieldList = new ArrayList<MutableProfileField>();
    final MutableProfileField mockField = mock( MutableProfileField.class );
    when( mockField.getPhysicalName() ).thenReturn( "testPname" );
    when( mockField.clone() ).thenReturn( mockField );
    fieldList.add( mockField );

    assertEquals( id, profileStatusManager.write( new ProfileStatusWriteOperation<Object>() {
      @Override public Object write( MutableProfileStatus profileStatus ) {
        profileStatus.getMutableFieldMap().put( "testPname", mockField );
        return profileStatus.getId();
      }
    } ) );
    assertEquals( 1, profileStatusManager.getFields().size() );
    assertEquals( mockField, profileStatusManager.getField( "testPname" ) );
  }

  @Test
  public void testGetTotalEntries() {
    ProfileStatusManagerImpl profileStatusManager =
      new ProfileStatusManagerImpl( id, name, profileConfiguration, profilingService );

    assertEquals( id, profileStatusManager.write( new ProfileStatusWriteOperation<Object>() {
      @Override public Object write( MutableProfileStatus profileStatus ) {
        profileStatus.setTotalEntities( 555L );
        return profileStatus.getId();
      }
    } ) );
    assertEquals( Long.valueOf( 555L ), profileStatusManager.getTotalEntities() );
  }

  @Test
  public void testGetCurrentOperation() {
    ProfileStatusManagerImpl profileStatusManager =
      new ProfileStatusManagerImpl( id, name, profileConfiguration, profilingService );
    ProfileStatusMessage profileStatusMessage = mock( ProfileStatusMessage.class );
    final List<ProfileStatusMessage> profileStatusMessages = Arrays.asList( profileStatusMessage );
    assertEquals( id, profileStatusManager.write( new ProfileStatusWriteOperation<Object>() {
      @Override public Object write( MutableProfileStatus profileStatus ) {
        profileStatus.setStatusMessages( profileStatusMessages );
        return profileStatus.getId();
      }
    } ) );
    assertEquals( profileStatusMessages, profileStatusManager.getStatusMessages() );
  }

  @Test
  public void testGetOperationError() {
    ProfileStatusManagerImpl profileStatusManager =
      new ProfileStatusManagerImpl( id, name, profileConfiguration, profilingService );
    final ProfileActionExceptionWrapper profileActionExceptionWrapper = mock( ProfileActionExceptionWrapper.class );
    assertEquals( id, profileStatusManager.write( new ProfileStatusWriteOperation<Object>() {
      @Override public Object write( MutableProfileStatus profileStatus ) {
        profileStatus.setOperationError( profileActionExceptionWrapper );
        return profileStatus.getId();
      }
    } ) );
    assertEquals( profileActionExceptionWrapper, profileStatusManager.getOperationError() );
  }

  @Test
  public void testGetProfileFieldProperties() {
    ProfileStatusManagerImpl profileStatusManager =
      new ProfileStatusManagerImpl( id, name, profileConfiguration, profilingService );

    final List<ProfileFieldProperty> profileFieldProperties = new ArrayList<ProfileFieldProperty>();
    ProfileFieldProperty profileFieldProperty = mock( ProfileFieldProperty.class );
    profileFieldProperties.add( profileFieldProperty );

    assertEquals( id, profileStatusManager.write( new ProfileStatusWriteOperation<Object>() {
      @Override public Object write( MutableProfileStatus profileStatus ) {
        profileStatus.setProfileFieldProperties( profileFieldProperties );
        return profileStatus.getId();
      }
    } ) );
    assertEquals( profileFieldProperties, profileStatusManager.getProfileFieldProperties() );
  }

  @Test
  public void testGetSequenceNumber() {
    ProfileStatusManagerImpl profileStatusManager =
      new ProfileStatusManagerImpl( id, name, profileConfiguration, profilingService );

    assertEquals( id, profileStatusManager.write( new ProfileStatusWriteOperation<Object>() {
      @Override public Object write( MutableProfileStatus profileStatus ) {
        return profileStatus.getId();
      }
    } ) );
    assertEquals( 2L, profileStatusManager.getSequenceNumber() );
  }

  @Test
  public void testGetProfileState() {
    ProfileStatusManagerImpl profileStatusManager =
      new ProfileStatusManagerImpl( id, name, profileConfiguration, profilingService );
    profileStatusManager.write( new ProfileStatusWriteOperation<Object>() {
      @Override public Object write( MutableProfileStatus profileStatus ) {
        profileStatus.setProfileState( ProfileState.ACTIVE );
        return null;
      }
    } );
    assertEquals( ProfileState.ACTIVE, profileStatusManager.getProfileState() );
    profileStatusManager.write( new ProfileStatusWriteOperation<Object>() {
      @Override public Object write( MutableProfileStatus profileStatus ) {
        profileStatus.setProfileState( ProfileState.DISCARDED );
        return null;
      }
    } );
    assertEquals( ProfileState.DISCARDED, profileStatusManager.getProfileState() );
  }
}
