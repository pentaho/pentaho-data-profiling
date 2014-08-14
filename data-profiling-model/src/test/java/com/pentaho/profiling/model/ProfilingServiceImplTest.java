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

package com.pentaho.profiling.model;

import com.pentaho.profiling.api.Profile;
import com.pentaho.profiling.api.ProfileCreationException;
import com.pentaho.profiling.api.ProfileFactory;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.datasource.DataSourceReference;
import com.pentaho.profiling.api.measure.MeasureMetadata;
import com.pentaho.profiling.api.measure.RequestedMeasure;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * Created by bryan on 8/14/14.
 */
public class ProfilingServiceImplTest {
  private Map<String, Profile> cachedProfileMap;
  private ProfileFactory profileFactory;
  private ProfilingServiceImpl profilingService;
  private Profile profile;

  @Before
  public void setup() {
    cachedProfileMap = ProfilingServiceImpl.getProfileMap();
    ProfilingServiceImpl.setProfileMap( new HashMap<String, Profile>() );
    profileFactory = mock( ProfileFactory.class );
    profilingService = new ProfilingServiceImpl();
    profilingService.setFactories( Arrays.asList( profileFactory ) );
    profile = mock( Profile.class );
  }

  @After
  public void tearDown() {
    ProfilingServiceImpl.setProfileMap( cachedProfileMap );
  }

  @Test
  public void testSetFactories() {
    ProfilingServiceImpl profilingService = new ProfilingServiceImpl();
    List<ProfileFactory> profileFactories = new ArrayList<ProfileFactory>();
    profileFactories.add( profileFactory );
    profilingService.setFactories( profileFactories );
    assertEquals( profileFactories, profilingService.getFactories() );
  }

  @Test
  public void testCreateNoFactories() throws ProfileCreationException {
    profilingService.setFactories( new ArrayList<ProfileFactory>() );
    assertNull( profilingService.create( new DataSourceReference( "Test", "Test" ) ) );
  }

  @Test
  public void testCreateNoMatchingFactories() throws ProfileCreationException {
    DataSourceReference dataSourceReference = new DataSourceReference();
    when( profileFactory.accepts( dataSourceReference ) ).thenReturn( false );
    assertNull( profilingService.create( dataSourceReference ) );
  }

  @Test
  public void testCreateMatchingFactory() throws ProfileCreationException {
    DataSourceReference dataSourceReference = new DataSourceReference();
    ProfileStatus profileStatus = mock( ProfileStatus.class );
    when( profile.getProfileUpdate() ).thenReturn( profileStatus );
    when( profileFactory.accepts( dataSourceReference ) ).thenReturn( true );
    when( profileFactory.create( dataSourceReference ) ).thenReturn( profile );
    assertEquals( profileStatus, profilingService.create( dataSourceReference ) );
  }

  @Test
  public void testGetSupportedMeasures() {
    String profileId = "PROFILE_ID";
    MeasureMetadata measureMetadata = new MeasureMetadata();
    measureMetadata.setMeasureName( "TEST" );
    List<MeasureMetadata> measureMetadatas = new ArrayList<MeasureMetadata>( Arrays.asList( measureMetadata ) );
    when( profile.getSupportedMeasures() ).thenReturn( measureMetadatas );
    ProfilingServiceImpl.getProfileMap().put( profileId, profile );
    assertEquals( measureMetadatas, profilingService.getSupportedMeasures( profileId ) );
  }

  @Test
  public void testSetSupportedMeasures() {
    String profileId = "PROFILE_ID";
    List<RequestedMeasure> requestedMeasures = new ArrayList<RequestedMeasure>();
    RequestedMeasure requestedMeasure = new RequestedMeasure();
    requestedMeasure.setName( "TEST" );
    requestedMeasures.add( requestedMeasure );
    ProfilingServiceImpl.getProfileMap().put( profileId, profile );
    profilingService.setRequestedMeasures( profileId, requestedMeasures );
    verify( profile ).setRequestedMeasures( requestedMeasures );
  }

  @Test
  public void testGetActiveProfiles() {
    String profileId = "PROFILE_ID";
    ProfileStatus profileStatus = mock( ProfileStatus.class );
    when( profile.getProfileUpdate() ).thenReturn( profileStatus );
    ProfilingServiceImpl.getProfileMap().put( profileId, profile );
    List<ProfileStatus> statuses = profilingService.getActiveProfiles();
    assertEquals( 1, statuses.size() );
    assertEquals( profileStatus, statuses.get( 0 ) );
  }

  @Test
  public void testGetProfileUpdate() {
    String profileId = "PROFILE_ID";
    ProfileStatus profileStatus = mock( ProfileStatus.class );
    when( profile.getProfileUpdate() ).thenReturn( profileStatus );
    ProfilingServiceImpl.getProfileMap().put( profileId, profile );
    assertEquals( profileStatus, profilingService.getProfileUpdate( profileId ) );
  }
}
