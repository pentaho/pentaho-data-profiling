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

package org.pentaho.profiling.model;

import org.pentaho.profiling.api.Profile;
import org.pentaho.profiling.api.ProfileFieldProperty;
import org.pentaho.profiling.api.ProfileStatusManager;
import org.pentaho.profiling.api.configuration.ProfileConfiguration;
import org.pentaho.profiling.api.metrics.MetricContributor;
import org.pentaho.profiling.api.metrics.MetricContributors;
import org.pentaho.profiling.api.metrics.MetricContributorsFactory;
import org.pentaho.profiling.api.metrics.MetricManagerContributor;
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
  private String name;
  private ProfileConfiguration profileConfiguration;
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
    name = "test-name";
    profileConfiguration = mock( ProfileConfiguration.class );
    profilingService = mock( ProfilingServiceImpl.class );
    metricContributors = new ArrayList<MetricContributor>();
    metricContributor = mock( MetricContributor.class );
    profileFieldProperty = mock( ProfileFieldProperty.class );
    when( metricContributor.getProfileFieldProperties() ).thenReturn( Arrays.asList( profileFieldProperty ) );
    metricContributors.add( metricContributor );
    metricContributorsFactory = mock( MetricContributorsFactory.class );
    profileStatusManager = new ProfileStatusManagerImpl( id, name, profileConfiguration, profilingService );
    executorService = mock( ExecutorService.class );
    when( executorService.submit( any( Runnable.class ) ) ).thenAnswer( new Answer<Future>() {
      @Override public Future answer( InvocationOnMock invocation ) throws Throwable {
        refreshRunnable = (Runnable) invocation.getArguments()[ 0 ];
        return null;
      }
    } );
    aggregateProfile =
      new AggregateProfileImpl( profileStatusManager, profilingService, metricContributorsFactory,
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
    aggregateProfile.start( executorService );
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
