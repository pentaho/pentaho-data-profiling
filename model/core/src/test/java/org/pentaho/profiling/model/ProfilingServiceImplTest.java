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
import org.pentaho.profiling.api.Profile;
import org.pentaho.profiling.api.ProfileCreationException;
import org.pentaho.profiling.api.ProfileFactory;
import org.pentaho.profiling.api.ProfileState;
import org.pentaho.profiling.api.ProfileStatus;
import org.pentaho.profiling.api.ProfileStatusManager;
import org.pentaho.profiling.api.ProfileStatusReadOperation;
import org.pentaho.profiling.api.ProfileStatusReader;
import org.pentaho.profiling.api.ProfileStatusWriteOperation;
import org.pentaho.profiling.api.ProfilingService;
import org.pentaho.profiling.api.configuration.DataSourceMetadata;
import org.pentaho.profiling.api.configuration.ProfileConfiguration;
import org.pentaho.profiling.api.metrics.MetricContributorService;
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
  public void testStopAll() {
    String profileId = "PROFILE_ID";
    profilingService.getProfileMap().put( profileId, profile );
    profilingService.stopAll();
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
  public void testDiscardAll() {
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
    profilingService.discardProfiles();
    verify( profile ).stop();
    verify( mutableProfileStatus ).setProfileState( ProfileState.DISCARDED );
  }

  @Test
  public void testGetEmittedTypes() {
    assertEquals( new HashSet<String>(
        Arrays.asList( ProfilingService.class.getCanonicalName(), ProfileStatus.class.getCanonicalName() ) ),
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
    assertEquals( ProfileStatus.class.getCanonicalName(), notificationObject.getType() );
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
    assertEquals( ProfileStatus.class.getCanonicalName(), notificationObject.getType() );
    assertEquals( profileStatus, notificationObject.getObject() );
  }

  /*@Test
  public void testSetProfileActionExecutor() {
    ProfileActionExecutor profileActionExecutor = mock( ProfileActionExecutor.class );
    profilingService.setProfileActionExecutor( profileActionExecutor );
    assertEquals( profileActionExecutor, profilingService.getProfileActionExecutor() );
  }*/
}
