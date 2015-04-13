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
import com.pentaho.profiling.api.Profile;
import com.pentaho.profiling.api.ProfileCreationException;
import com.pentaho.profiling.api.ProfileFactory;
import com.pentaho.profiling.api.ProfileState;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusReadOperation;
import com.pentaho.profiling.api.ProfileStatusReader;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.configuration.DataSourceMetadata;
import com.pentaho.profiling.api.configuration.ProfileConfiguration;
import com.pentaho.profiling.api.metrics.MetricContributorService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.osgi.notification.api.NotificationListener;
import org.pentaho.osgi.notification.api.NotificationObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 8/14/14.
 */
public class ProfilingServiceImplTest {
  private ProfileFactory profileFactory;
  private ProfilingServiceImpl profilingService;
  private Profile profile;
  private ProfileStatus profileStatus;
  private ProfileStatusManager profileStatusManager;
  private String profileId;
  private ExecutorService executorService;
  private MetricContributorService metricContributorService;

  @Before
  public void setup() {
    profileFactory = mock( ProfileFactory.class );
    executorService = mock( ExecutorService.class );
    metricContributorService = mock( MetricContributorService.class );
    profilingService = new ProfilingServiceImpl( executorService, metricContributorService );
    profilingService.profileFactoryAdded( profileFactory, new HashMap() );
    profile = mock( Profile.class );
    profileId = "test-id";
    when( profile.getId() ).thenReturn( profileId );
    profileStatusManager = mock( ProfileStatusManager.class );
    profileStatus = mock( ProfileStatus.class );
    when( profileStatus.getId() ).thenReturn( profileId );
  }

  @Test
  public void testCreateNoFactories() throws ProfileCreationException {
    profilingService.profileFactoryRemoved( profileFactory, new HashMap() );
    DataSourceMetadata dataSourceMetadata = mock( DataSourceMetadata.class );
    assertNull(
      profilingService.create( new ProfileConfiguration( dataSourceMetadata, null, null ) ) );
  }

  @Test
  public void testCreateNoMatchingFactories() throws ProfileCreationException {
    DataSourceMetadata dataSourceMetadata = mock( DataSourceMetadata.class );
    when( profileFactory.accepts( dataSourceMetadata ) ).thenReturn( false );
    assertNull( profilingService.create( new ProfileConfiguration( dataSourceMetadata, null, null ) ) );
    assertFalse( profilingService.accepts( dataSourceMetadata ) );
  }

  @Test
  public void testCreateMatchingFactory() throws ProfileCreationException, IOException {
    DataSourceMetadata dataSourceMetadata = mock( DataSourceMetadata.class );
    ProfileConfiguration profileConfiguration = mock( ProfileConfiguration.class );
    when( profileConfiguration.getDataSourceMetadata() ).thenReturn( dataSourceMetadata );
    Profile profile = mock( Profile.class );
    String value = "test-id";
    when( profile.getId() ).thenReturn( value );
    when( profileFactory.accepts( dataSourceMetadata ) ).thenReturn( true );
    when( profileFactory.create( eq( profileConfiguration ), any( ProfileStatusManager.class ) ) )
      .thenReturn( profile );
    ProfileStatusManager profileStatusManager = profilingService.create( profileConfiguration );
    assertEquals( profileConfiguration, profileStatusManager.getProfileConfiguration() );
    assertTrue( profilingService.accepts( dataSourceMetadata ) );
  }

  @Test
  public void testGetActiveProfiles() {
    String profileId = "PROFILE_ID";
    ProfileStatus profileStatus = mock( ProfileStatusImpl.class );
    profilingService.getProfileMap().put( profileId, profile );
    profilingService.getProfileStatusManagerMap().put( profileId, profileStatusManager );
    List<ProfileStatusReader> statuses = profilingService.getActiveProfiles();
    assertEquals( 1, statuses.size() );
    assertEquals( profileStatusManager, statuses.get( 0 ) );
  }

  @Test
  public void testIsRunning() {
    String profileId = "PROFILE_ID";
    profilingService.getProfileMap().put( profileId, profile );
    when( profile.isRunning() ).thenReturn( true );
    assertTrue( profilingService.isRunning( profileId ) );
  }

  @Test
  public void testGetProfileUpdate() {
    String profileId = "PROFILE_ID";
    profilingService.getProfileStatusManagerMap().put( profileId, profileStatusManager );
    assertEquals( profileStatusManager, profilingService.getProfileUpdate( profileId ) );
  }

  @Test
  public void testStop() {
    String profileId = "PROFILE_ID";
    ProfileStatus profileStatus = mock( ProfileStatusImpl.class );
    profilingService.getProfileMap().put( profileId, profile );
    profilingService.stop( profileId );
    verify( profile ).stop();
  }

  @Test
  public void testDiscard() {
    when( profileStatusManager.getId() ).thenReturn( profileId );
    profilingService.getProfileMap().put( profileId, profile );
    profilingService.getProfileStatusManagerMap().put( profileId, profileStatusManager );
    assertEquals( profileStatusManager, profilingService.getProfileUpdate( profileId ) );
    final MutableProfileStatus mutableProfileStatus = mock( MutableProfileStatus.class );
    when( profileStatusManager.write( any( ProfileStatusWriteOperation.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        return ( (ProfileStatusWriteOperation) invocation.getArguments()[ 0 ] ).write( mutableProfileStatus );
      }
    } );
    profilingService.discardProfile( profileId );
    verify( profile ).stop();
    verify( mutableProfileStatus ).setProfileState( ProfileState.DISCARDED );
  }

  @Test
  public void testGetEmittedTypes() {
    assertEquals( new HashSet<String>( Arrays.asList( ProfilingServiceImpl.class.getCanonicalName() ) ),
      profilingService.getEmittedTypes() );
  }

  @Test
  public void testRegister() {
    NotificationListener notificationListener = mock( NotificationListener.class );
    profilingService.register( notificationListener );
    ArgumentCaptor<NotificationObject> argumentCaptor = ArgumentCaptor.forClass( NotificationObject.class );
    profilingService.notify( profileStatus );
    verify( notificationListener ).notify( argumentCaptor.capture() );
    NotificationObject notificationObject = argumentCaptor.getValue();
    assertEquals( profileId, notificationObject.getId() );
    assertEquals( ProfilingServiceImpl.class.getCanonicalName(), notificationObject.getType() );
    assertEquals( profileStatus, notificationObject.getObject() );
  }

  @Test
  public void testUnRegister() {
    NotificationListener notificationListener = mock( NotificationListener.class );
    profilingService.register( notificationListener );
    profilingService.unregister( notificationListener );
    profilingService.notify( profileStatus );
    verifyNoMoreInteractions( notificationListener );
  }

  @Test
  public void testPreviousNotifications() {
    when( profileStatusManager.read( any( ProfileStatusReadOperation.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        return ( (ProfileStatusReadOperation) invocation.getArguments()[ 0 ] ).read( profileStatus );
      }
    } );
    profilingService.notify( profileStatus );
    ArgumentCaptor<NotificationObject> argumentCaptor = ArgumentCaptor.forClass( NotificationObject.class );
    NotificationListener notificationListener = mock( NotificationListener.class );
    profilingService.register( notificationListener );
    verify( notificationListener ).notify( argumentCaptor.capture() );
    NotificationObject notificationObject = argumentCaptor.getValue();
    assertEquals( profileId, notificationObject.getId() );
    assertEquals( ProfilingServiceImpl.class.getCanonicalName(), notificationObject.getType() );
    assertEquals( profileStatus, notificationObject.getObject() );
  }

  /*@Test
  public void testSetProfileActionExecutor() {
    ProfileActionExecutor profileActionExecutor = mock( ProfileActionExecutor.class );
    profilingService.setProfileActionExecutor( profileActionExecutor );
    assertEquals( profileActionExecutor, profilingService.getProfileActionExecutor() );
  }*/
}
