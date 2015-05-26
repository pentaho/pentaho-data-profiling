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

package com.pentaho.profiling.services;

import com.pentaho.profiling.api.Profile;
import com.pentaho.profiling.api.ProfileCreationException;
import com.pentaho.profiling.api.ProfileFactory;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusReadOperation;
import com.pentaho.profiling.api.ProfileStatusReader;
import com.pentaho.profiling.api.ProfilingService;
import com.pentaho.profiling.api.configuration.DataSourceMetadata;
import com.pentaho.profiling.api.configuration.ProfileConfiguration;
import com.pentaho.profiling.api.dto.ProfileStatusDTO;
import com.pentaho.profiling.api.metrics.MetricContributorService;
import com.pentaho.profiling.api.sample.SampleProviderManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 8/15/14.
 */
public class ProfilingServiceWebserviceImplTest {
  private ProfilingService delegate;
  private ProfilingServiceWebserviceImpl webservice;
  private MetricContributorService metricContributorService;
  private SampleProviderManager sampleProviderManager;

  @Before
  public void setup() {
    delegate = mock( ProfilingService.class );
    metricContributorService = mock( MetricContributorService.class );
    sampleProviderManager = mock( SampleProviderManager.class );
    webservice = new ProfilingServiceWebserviceImpl( sampleProviderManager, delegate, metricContributorService );
  }

  @Test
  public void testCreate() throws ProfileCreationException {
    final ProfileStatus profileStatus = mock( ProfileStatus.class );
    ProfileStatusManager result = mock( ProfileStatusManager.class );
    when( result.read( any( ProfileStatusReadOperation.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        return ( (ProfileStatusReadOperation) invocation.getArguments()[ 0 ] ).read( profileStatus );
      }
    } );
    ProfileConfiguration profileCreateRequest = mock( ProfileConfiguration.class );
    when( delegate.create( profileCreateRequest ) ).thenReturn( result );
    assertEquals( result, webservice.create( profileCreateRequest ) );
  }

  @Test
  public void testGetActiveProfiles() {
    List<ProfileStatusReader> profiles = new ArrayList<ProfileStatusReader>();
    String id = "test-id";
    final ProfileStatus profileStatus = mock( ProfileStatus.class );
    ProfileStatusManager result = mock( ProfileStatusManager.class );
    when( profileStatus.getId() ).thenReturn( id );
    when( result.read( any( ProfileStatusReadOperation.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        return ( (ProfileStatusReadOperation) invocation.getArguments()[ 0 ] ).read( profileStatus );
      }
    } );
    profiles.add( result );
    when( delegate.getActiveProfiles() ).thenReturn( profiles );
    assertEquals( 1, webservice.getActiveProfilesWebservice().size() );
    ProfileStatus webserviceResult = webservice.getActiveProfilesWebservice().get( 0 );
    assertTrue( webserviceResult instanceof ProfileStatusDTO );
    assertEquals( id, webserviceResult.getId() );
  }

  @Test
  public void testGetProfileUpdate() {
    final ProfileStatus profileStatus = mock( ProfileStatus.class );
    ProfileStatusManager result = mock( ProfileStatusManager.class );
    String id = "id";
    when( profileStatus.getId() ).thenReturn( id );
    when( result.read( any( ProfileStatusReadOperation.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        return ( (ProfileStatusReadOperation) invocation.getArguments()[ 0 ] ).read( profileStatus );
      }
    } );
    when( delegate.getProfileUpdate( id ) ).thenReturn( result );
    ProfileStatus profileUpdateWebservice = webservice.getProfileUpdateWebservice( id );
    assertEquals( id, profileUpdateWebservice.getId() );
    assertTrue( profileUpdateWebservice instanceof ProfileStatusDTO );
  }

  @Test
  public void testStopWrapper() {
    String id = "test-profile-id";
    webservice.stop( id );
    verify( delegate ).stop( id );
  }

  @Test
  public void testStopId() {
    String id = "test-profile-id";
    webservice.stop( id );
    verify( delegate ).stop( id );
  }

  @Test
  public void testDiscardProfile() {
    webservice.discardProfile( "test-id" );
    verify( delegate ).discardProfile( "test-id" );
  }

  @Test
  public void testAccepts() {
    when( delegate.accepts( any( DataSourceMetadata.class ) ) ).thenReturn( true );
    assertTrue( webservice.accepts( null ) );
    when( delegate.accepts( any( DataSourceMetadata.class ) ) ).thenReturn( false );
    assertFalse( webservice.accepts( null ) );
  }

  @Test
  public void testIsRunning() {
    when( delegate.isRunning( anyString() ) ).thenReturn( true );
    assertTrue( webservice.isRunning( null ) );
    when( delegate.isRunning( anyString() ) ).thenReturn( false );
    assertFalse( webservice.isRunning( null ) );
  }

  @Test
  public void testGetProfileFactory() {
    DataSourceMetadata dataSourceMetadata = mock( DataSourceMetadata.class );
    ProfileFactory profileFactory = mock( ProfileFactory.class );
    when( delegate.getProfileFactory( any( DataSourceMetadata.class ) ) ).thenReturn( profileFactory );
    assertEquals( profileFactory, webservice.getProfileFactory( dataSourceMetadata ) );
  }

  @Test
  public void testGetProfile() {
    Profile profile = mock( Profile.class );
    String test = "test";
    when( delegate.getProfile( test ) ).thenReturn( profile );
    assertEquals( profile, webservice.getProfile( test ) );
  }
}
