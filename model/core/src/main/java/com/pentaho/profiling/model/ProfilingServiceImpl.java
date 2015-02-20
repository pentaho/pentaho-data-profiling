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
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.ProfilingService;
import com.pentaho.profiling.api.action.ProfileActionExecutor;
import com.pentaho.profiling.api.datasource.DataSourceReference;
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

/**
 * Created by bryan on 7/31/14.
 */
public class ProfilingServiceImpl implements ProfilingService, NotifierWithHistory {
  private final Map<String, Profile> profileMap = new ConcurrentHashMap<String, Profile>();
  private final Map<String, ProfileStatusManager> profileStatusManagerMap = new ConcurrentHashMap<String,
    ProfileStatusManager>();
  private final Map<String, NotificationObject> previousNotifications = new ConcurrentHashMap<String,
    NotificationObject>();
  private final DelegatingNotifierImpl delegatingNotifier =
    new DelegatingNotifierImpl( new HashSet<String>( Arrays.asList( ProfilingServiceImpl.class.getCanonicalName() ) ),
      this );
  private List<Pair<Integer, ProfileFactory>> factories = new ArrayList<Pair<Integer, ProfileFactory>>();
  private ProfileActionExecutor profileActionExecutor;

  public ProfileActionExecutor getProfileActionExecutor() {
    return profileActionExecutor;
  }

  public void setProfileActionExecutor( ProfileActionExecutor profileActionExecutor ) {
    this.profileActionExecutor = profileActionExecutor;
  }

  // FOR UNIT TEST ONLY
  protected Map<String, Profile> getProfileMap() {
    return profileMap;
  }

  // FOR UNIT TEST ONLY
  protected Map<String, ProfileStatusManager> getProfileStatusManagerMap() {
    return profileStatusManagerMap;
  }

  private ProfileFactory getProfileOperationProviderFactory(
    DataSourceReference dataSourceReference ) {
    synchronized ( factories ) {
      for ( Pair<Integer, ProfileFactory> factoryPair : factories ) {
        ProfileFactory factory = factoryPair.getSecond();
        if ( factory.accepts( dataSourceReference ) ) {
          return factory;
        }
      }
    }
    return null;
  }

  @Override public boolean accepts( DataSourceReference dataSourceReference ) {
    return getProfileOperationProviderFactory( dataSourceReference ) != null;
  }

  @Override
  public ProfileStatusManager create( DataSourceReference dataSourceReference ) throws ProfileCreationException {
    ProfileFactory profileOperationProviderFactory = getProfileOperationProviderFactory( dataSourceReference );
    if ( profileOperationProviderFactory != null ) {
      String profileId = UUID.randomUUID().toString();
      ProfileStatusManager profileStatusManager =
        new ProfileStatusManagerImpl( profileId, dataSourceReference, this );
      Profile profile = profileOperationProviderFactory.create( dataSourceReference, profileStatusManager );
      profile.start( profileActionExecutor );
      profileMap.put( profile.getId(), profile );
      profileStatusManagerMap.put( profile.getId(), profileStatusManager );
      return profileStatusManager;
    }
    return null;
  }

  @Override
  public List<ProfileStatusManager> getActiveProfiles() {
    return new ArrayList<ProfileStatusManager>( profileStatusManagerMap.values() );
  }

  @Override
  public ProfileStatusManager getProfileUpdate( String profileId ) {
    return profileStatusManagerMap.get( profileId );
  }

  @Override public void stop( String profileId ) {
    profileMap.get( profileId ).stop();
  }

  @Override public boolean isRunning( String profileId ) {
    return profileMap.get( profileId ).isRunning();
  }

  @Override public void discardProfile( String profileId ) {
    profileMap.remove( profileId ).stop();
    profileStatusManagerMap.get( profileId ).write( new ProfileStatusWriteOperation<Void>() {
      @Override public Void write( MutableProfileStatus profileStatus ) {
        profileStatus.setProfileState( ProfileState.DISCARDED );
        return null;
      }
    } );
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
      new NotificationObject( ProfilingServiceImpl.class.getCanonicalName(), profileStatus.getId(),
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
}
