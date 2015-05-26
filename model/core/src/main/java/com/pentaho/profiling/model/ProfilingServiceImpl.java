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

package com.pentaho.profiling.model;

import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.Profile;
import com.pentaho.profiling.api.ProfileCreationException;
import com.pentaho.profiling.api.ProfileFactory;
import com.pentaho.profiling.api.ProfileState;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusReader;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.ProfilingService;
import com.pentaho.profiling.api.configuration.DataSourceMetadata;
import com.pentaho.profiling.api.configuration.ProfileConfiguration;
import com.pentaho.profiling.api.metrics.MetricContributorService;
import com.pentaho.profiling.api.util.Pair;
import org.pentaho.osgi.notification.api.DelegatingNotifierImpl;
import org.pentaho.osgi.notification.api.NotificationListener;
import org.pentaho.osgi.notification.api.NotificationObject;
import org.pentaho.osgi.notification.api.NotifierWithHistory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by bryan on 7/31/14.
 */
public class ProfilingServiceImpl implements ProfilingService, NotifierWithHistory {
  public static final String PROFILE_STATUS_CANONICAL_NAME = ProfileStatus.class.getCanonicalName();
  public static final String PROFILING_SERVICE_CANONICAL_NAME = ProfilingService.class.getCanonicalName();
  private final Map<String, Profile> profileMap = new ConcurrentHashMap<String, Profile>();
  private final Map<String, ProfileStatusManager> profileStatusManagerMap = new ConcurrentHashMap<String,
    ProfileStatusManager>();
  private final Map<String, NotificationObject> previousNotifications = new ConcurrentHashMap<String,
    NotificationObject>();
  private final DelegatingNotifierImpl delegatingNotifier =
    new DelegatingNotifierImpl(
      new HashSet<String>( Arrays.asList( PROFILING_SERVICE_CANONICAL_NAME, PROFILE_STATUS_CANONICAL_NAME ) ), this );
  private final ExecutorService executorService;
  private final MetricContributorService metricContributorService;
  private final AtomicLong profilesSequence = new AtomicLong( 1L );
  private List<Pair<Integer, ProfileFactory>> factories = new ArrayList<Pair<Integer, ProfileFactory>>();

  public ProfilingServiceImpl( ExecutorService executorService, MetricContributorService metricContributorService ) {
    this.executorService = executorService;
    this.metricContributorService = metricContributorService;
  }

  // FOR UNIT TEST ONLY
  protected Map<String, Profile> getProfileMap() {
    return profileMap;
  }

  // FOR UNIT TEST ONLY
  protected Map<String, ProfileStatusManager> getProfileStatusManagerMap() {
    return profileStatusManagerMap;
  }

  @Override public ProfileFactory getProfileFactory(
    DataSourceMetadata dataSourceMetadata ) {
    synchronized ( factories ) {
      for ( Pair<Integer, ProfileFactory> factoryPair : factories ) {
        ProfileFactory factory = factoryPair.getSecond();
        if ( factory.accepts( dataSourceMetadata ) ) {
          return factory;
        }
      }
    }
    return null;
  }

  @Override public boolean accepts( DataSourceMetadata dataSourceMetadata ) {
    return getProfileFactory( dataSourceMetadata ) != null;
  }

  @Override
  public ProfileStatusManager create( ProfileConfiguration profileConfiguration ) throws ProfileCreationException {
    ProfileFactory profileOperationProviderFactory =
      getProfileFactory( profileConfiguration.getDataSourceMetadata() );
    String configName = profileConfiguration.getConfigName();
    if ( configName != null ) {
      profileConfiguration.setMetricContributors( metricContributorService.getDefaultMetricContributors( configName ) );
    } else if ( profileConfiguration.getMetricContributors() == null ) {
      profileConfiguration.setMetricContributors(
        metricContributorService.getDefaultMetricContributors( MetricContributorService.DEFAULT_CONFIGURATION ) );
    }
    if ( profileOperationProviderFactory != null ) {
      String profileId = UUID.randomUUID().toString();
      ProfileStatusManager profileStatusManager =
        new ProfileStatusManagerImpl( profileId, null, profileConfiguration, this );
      Profile profile = profileOperationProviderFactory.create( profileConfiguration, profileStatusManager );
      profile.start( executorService );
      profileMap.put( profile.getId(), profile );
      profileStatusManagerMap.put( profile.getId(), profileStatusManager );
      notifyProfiles();
      return profileStatusManager;
    }

    return null;
  }

  private void notifyProfiles() {
    NotificationObject notificationObject =
      new NotificationObject( PROFILING_SERVICE_CANONICAL_NAME, PROFILES, profilesSequence.getAndIncrement(),
        new ArrayList<Profile>( profileMap.values() ) );
    previousNotifications.put( PROFILES, notificationObject );
    try {
      delegatingNotifier.notify( notificationObject );
    } catch ( Exception e ) {
      e.printStackTrace();
    }
  }

  @Override
  public List<ProfileStatusReader> getActiveProfiles() {
    return new ArrayList<ProfileStatusReader>( profileStatusManagerMap.values() );
  }

  @Override
  public ProfileStatusManager getProfileUpdate( String profileId ) {
    return profileStatusManagerMap.get( profileId );
  }

  @Override public void stop( String profileId ) {
    Profile profile = profileMap.get( profileId );
    if ( profile != null ) {
      profile.stop();
    }
  }

  @Override public void stopAll() {
    for ( Profile profile : profileMap.values() ) { profile.stop(); }
  }

  @Override public boolean isRunning( String profileId ) {
    return profileMap.get( profileId ).isRunning();
  }

  @Override public void discardProfile( String profileId ) {
    stop( profileId );
    profileMap.remove( profileId );
    ProfileStatusManager remove = profileStatusManagerMap.remove( profileId );
    if ( remove != null ) {
      remove.write( new ProfileStatusWriteOperation<Void>() {
        @Override public Void write( MutableProfileStatus profileStatus ) {
          profileStatus.setProfileState( ProfileState.DISCARDED );
          return null;
        }
      } );
    }
    previousNotifications.remove( profileId );
    notifyProfiles();
  }

  @Override public void discardProfiles() {
    for ( Map.Entry<String, Profile> entry : profileMap.entrySet() ) {
      discardProfile( entry.getKey() );
    }
  }

  @Override public List<NotificationObject> getPreviousNotificationObjects() {
    return new ArrayList<NotificationObject>( previousNotifications.values() );
  }

  @Override public Set<String> getEmittedTypes() {
    return delegatingNotifier.getEmittedTypes();
  }

  @Override public void register( NotificationListener notificationListener ) {
    delegatingNotifier.register( notificationListener );
  }

  @Override public void unregister( NotificationListener notificationListener ) {
    delegatingNotifier.unregister( notificationListener );
  }

  public void notify( ProfileStatus profileStatus ) {
    NotificationObject notificationObject =
      new NotificationObject( PROFILE_STATUS_CANONICAL_NAME, profileStatus.getId(),
        profileStatus.getSequenceNumber(), profileStatus );
    previousNotifications.put( profileStatus.getId(), notificationObject );
    try {
      delegatingNotifier.notify( notificationObject );
    } catch ( Throwable e ) {
      e.printStackTrace();
    }
  }

  public void profileFactoryAdded( ProfileFactory profileFactory, Map properties ) {
    Integer ranking = (Integer) properties.get( "service.ranking" );
    if ( ranking == null ) {
      ranking = 0;
    }
    synchronized ( factories ) {
      factories.add( Pair.of( ranking, profileFactory ) );
      Collections.sort( factories, new Comparator<Pair<Integer, ProfileFactory>>() {
        @Override public int compare( Pair<Integer, ProfileFactory> o1,
                                      Pair<Integer, ProfileFactory> o2 ) {
          int result = o2.getFirst() - o1.getFirst();
          if ( result == 0 ) {
            result = o1.getSecond().toString().compareTo( o2.getSecond().toString() );
          }
          return result;
        }
      } );
    }
  }

  public void profileFactoryRemoved( ProfileFactory profileFactory, Map properties ) {
    List<Pair<Integer, ProfileFactory>> newFactories = null;
    synchronized ( this.factories ) {
      newFactories =
        new ArrayList<Pair<Integer, ProfileFactory>>( Math.max( 0, this.factories.size() - 1 ) );
      for ( Pair<Integer, ProfileFactory> factoryPair : this.factories ) {
        if ( !factoryPair.getSecond().equals( profileFactory ) ) {
          newFactories.add( factoryPair );
        }
      }
      this.factories = newFactories;
    }
  }

  @Override public Profile getProfile( String profileId ) {
    return profileMap.get( profileId );
  }

  public void registerProfile( Profile profile, ProfileStatusManager profileStatusManager ) {
    profileMap.put( profile.getId(), profile );
    profileStatusManagerMap.put( profileStatusManager.getId(), profileStatusManager );
  }
}
