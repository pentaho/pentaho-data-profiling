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

import com.pentaho.profiling.api.Profile;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.datasource.DataSourceReference;
import com.pentaho.profiling.api.metrics.MetricContributor;
import com.pentaho.profiling.api.metrics.MetricContributors;
import com.pentaho.profiling.api.metrics.MetricContributorsFactory;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.osgi.notification.api.NotificationListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 3/6/15.
 */
public class AggregateProfileImplTest {
  private String id;
  private DataSourceReference dataSourceReference;
  private ProfileStatusManager profileStatusManager;
  private ProfilingServiceImpl profilingService;
  private List<MetricContributor> metricContributors;
  private MetricContributor metricContributor;
  private ProfileFieldProperty profileFieldProperty;
  private ExecutorService executorService;
  private AggregateProfileImpl aggregateProfile;
  private MetricContributorsFactory metricContributorsFactory;
  private Runnable refreshRunnable;

  @Before
  public void setup() {
    id = "test-id";
    dataSourceReference = mock( DataSourceReference.class );
    profilingService = mock( ProfilingServiceImpl.class );
    metricContributors = new ArrayList<MetricContributor>();
    metricContributor = mock( MetricContributor.class );
    profileFieldProperty = mock( ProfileFieldProperty.class );
    when( metricContributor.getProfileFieldProperties() ).thenReturn( Arrays.asList( profileFieldProperty ) );
    metricContributors.add( metricContributor );
    metricContributorsFactory = mock( MetricContributorsFactory.class );
    profileStatusManager = new ProfileStatusManagerImpl( id, dataSourceReference, profilingService );
    executorService = mock( ExecutorService.class );
    when( executorService.submit( any( Runnable.class ) ) ).thenAnswer( new Answer<Future>() {
      @Override public Future answer( InvocationOnMock invocation ) throws Throwable {
        refreshRunnable = (Runnable) invocation.getArguments()[ 0 ];
        return null;
      }
    } );
    aggregateProfile =
      new AggregateProfileImpl( dataSourceReference, profileStatusManager, profilingService, metricContributorsFactory,
        new MetricContributors( metricContributors, new ArrayList<MetricManagerContributor>() ) );
  }

  @Test
  public void testGetId() {
    assertEquals( id, aggregateProfile.getId() );
  }

  @Test
  public void testStart() {
    assertFalse( aggregateProfile.isRunning() );
    aggregateProfile.start( executorService );
    aggregateProfile.start( executorService );
    verify( profilingService, times( 1 ) ).register( any( NotificationListener.class ) );
    verify( executorService, times( 1 ) ).submit( any( Runnable.class ) );
    assertTrue( aggregateProfile.isRunning() );
  }

  @Test
  public void testStop() {
    aggregateProfile.start( executorService );
    verify( profilingService, times( 1 ) ).register( any( NotificationListener.class ) );
    verify( executorService, times( 1 ) ).submit( any( Runnable.class ) );
    aggregateProfile.stop();
    verify( profilingService ).unregister( any( NotificationListener.class ) );
    assertFalse( aggregateProfile.isRunning() );
  }

  @Test
  public void testRegisterGetChildProfiles() {
    String id = "child-id";
    Profile child = mock( Profile.class );
    when( child.getId() ).thenReturn( id );
    when( profilingService.getProfile( id ) ).thenReturn( child );
    aggregateProfile.addChildProfile( id );
    aggregateProfile.addChildProfile( id );
    aggregateProfile.addChildProfile( "fake-id" );
    List<Profile> childProfiles = aggregateProfile.getChildProfiles();
    assertEquals( 1, childProfiles.size() );
    assertEquals( child, childProfiles.get( 0 ) );
  }
}
