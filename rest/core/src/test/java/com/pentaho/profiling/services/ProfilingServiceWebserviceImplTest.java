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
