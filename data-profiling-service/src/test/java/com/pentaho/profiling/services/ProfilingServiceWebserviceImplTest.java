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

package com.pentaho.profiling.services;

import com.pentaho.profiling.api.Profile;
import com.pentaho.profiling.api.ProfileCreationException;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.ProfilingService;
import com.pentaho.profiling.api.datasource.DataSourceReference;
import com.pentaho.profiling.api.measure.MeasureMetadata;
import com.pentaho.profiling.api.measure.RequestedMeasure;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 8/15/14.
 */
public class ProfilingServiceWebserviceImplTest {
  private ProfilingService delegate;
  private ProfilingServiceWebserviceImpl webservice;

  @Before
  public void setup() {
    delegate = mock( ProfilingService.class );
    webservice = new ProfilingServiceWebserviceImpl();
    webservice.setDelegate( delegate );
  }

  @Test
  public void testGetDelegate() {
    assertEquals( delegate, webservice.getDelegate() );
  }

  @Test
  public void testCreate() throws ProfileCreationException {
    ProfileStatus result = mock( ProfileStatus.class );
    DataSourceReference dataSourceReference = mock( DataSourceReference.class );
    when( delegate.create( dataSourceReference ) ).thenReturn( result );
    assertEquals( result, webservice.create( dataSourceReference ) );
  }

  @Test
  public void testGetSupportedMeasures() {
    List<MeasureMetadata> result = new ArrayList<MeasureMetadata>(  );
    MeasureMetadata measureMetadata = mock( MeasureMetadata.class );
    result.add( measureMetadata );
    String id = "id";
    when( delegate.getSupportedMeasures( id ) ).thenReturn( result );
    assertEquals( result, webservice.getSupportedMeasures( id ) );
  }

  @Test
  public void testSetRequestedMeasures() {
    List<RequestedMeasure> requestedMeasures = new ArrayList<RequestedMeasure>(  );
    RequestedMeasure measure = mock( RequestedMeasure.class );
    requestedMeasures.add( measure );
    String id = "id";
    webservice.setRequestedMeasures( id, requestedMeasures );
    verify( delegate ).setRequestedMeasures( id, requestedMeasures );
  }

  @Test
  public void testGetActiveProfiles() {
    List<ProfileStatus> profiles = new ArrayList<ProfileStatus>(  );
    ProfileStatus profile = mock( ProfileStatus.class );
    profiles.add( profile );
    when( delegate.getActiveProfiles() ).thenReturn( profiles );
    assertEquals( profiles, webservice.getActiveProfiles() );
  }

  @Test
  public void testGetProfileUpdate() {
    ProfileStatus profile = mock( ProfileStatus.class );
    String id = "id";
    when( delegate.getProfileUpdate( id ) ).thenReturn( profile );
    assertEquals( profile, webservice.getProfileUpdate( id ) );
  }
}
