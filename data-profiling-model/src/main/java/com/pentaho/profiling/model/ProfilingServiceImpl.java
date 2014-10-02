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

import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.Profile;
import com.pentaho.profiling.api.ProfileCreationException;
import com.pentaho.profiling.api.ProfileFactory;
import com.pentaho.profiling.api.ProfileState;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.ProfilingService;
import com.pentaho.profiling.api.datasource.DataSourceReference;
import com.pentaho.profiling.api.operations.ProfileOperation;
import org.pentaho.osgi.notification.api.DelegatingNotifierImpl;
import org.pentaho.osgi.notification.api.NotificationListener;
import org.pentaho.osgi.notification.api.NotificationObject;
import org.pentaho.osgi.notification.api.NotifierWithHistory;

import java.util.ArrayList;
import java.util.Arrays;
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
  private List<ProfileFactory> factories;

  public List<ProfileFactory> getFactories() {
    return factories;
  }

  public void setFactories( List<ProfileFactory> factories ) {
    this.factories = factories;
  }

  // FOR UNIT TEST ONLY
  protected Map<String, Profile> getProfileMap() {
    return profileMap;
  }

  // FOR UNIT TEST ONLY
  protected Map<String, ProfileStatusManager> getProfileStatusManagerMap() {
    return profileStatusManagerMap;
  }

  @Override
  public ProfileStatusManager create( DataSourceReference dataSourceReference ) throws ProfileCreationException {
    Profile profile = null;
    for ( ProfileFactory factory : factories ) {
      if ( factory.accepts( dataSourceReference ) ) {
        ProfileStatusManager profileStatusManager =
          new ProfileStatusManagerImpl( UUID.randomUUID().toString(), dataSourceReference, this );
        profile = factory.create( profileStatusManager );
        profileMap.put( profile.getId(), profile );
        profileStatusManagerMap.put( profile.getId(), profileStatusManager );
        return profileStatusManager;
      }
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

  @Override public void stopCurrentOperation( String profileId ) {
    profileMap.get( profileId ).stopCurrentOperation();
  }

  @Override public void startOperation( String profileId, String operationId ) {
    profileMap.get( profileId ).startOperation( operationId );
  }

  @Override public List<ProfileOperation> getOperations( String profileId ) {
    Profile profile = profileMap.get( profileId );
    if ( profile != null ) {
      return profile.getProfileOperations();
    }
    return null;
  }

  @Override public void discardProfile( String profileId ) {
    profileMap.remove( profileId ).stopCurrentOperation();
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
    delegatingNotifier.notify( notificationObject );
  }
}
