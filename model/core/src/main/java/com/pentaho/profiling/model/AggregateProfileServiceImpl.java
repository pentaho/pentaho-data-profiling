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

import com.pentaho.profiling.api.AggregateProfile;
import com.pentaho.profiling.api.AggregateProfileService;
import com.pentaho.profiling.api.Profile;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by bryan on 3/5/15.
 */
public class AggregateProfileServiceImpl implements AggregateProfileService, NotifierWithHistory {
  private final Map<String, AggregateProfile> aggregateProfileMap = new ConcurrentHashMap<String, AggregateProfile>();
  private final Map<String, String> aggregateProfileTopLevelMap = new ConcurrentHashMap<String, String>();
  private final AtomicLong notificationCounter = new AtomicLong( 1L );
  private final DelegatingNotifierImpl delegatingNotifier =
    new DelegatingNotifierImpl(
      new HashSet<String>( Arrays.asList( AggregateProfileService.class.getCanonicalName() ) ),
      this );
  private volatile NotificationObject previousNotification = null;

  public void registerAggregateProfile( AggregateProfile aggregateProfile ) {
    String id = aggregateProfile.getId();
    aggregateProfileMap.put( id, aggregateProfile );
    aggregateProfileTopLevelMap.put( id, id );
    notifyTopLevel();
  }

  @Override public List<AggregateProfile> getAggregateProfiles() {
    return new ArrayList<AggregateProfile>( aggregateProfileMap.values() );
  }

  @Override public AggregateProfile getAggregateProfile( String profileId ) {
    String aggregateId = aggregateProfileTopLevelMap.get( profileId );
    if ( aggregateId != null ) {
      return aggregateProfileMap.get( aggregateId );
    }
    return null;
  }

  @Override public void addChild( String profileId, String childProfileId ) {
    AggregateProfile aggregateProfile = aggregateProfileMap.get( profileId );
    aggregateProfile.addChildProfile( childProfileId );
    String topLevel = profileId;
    String next;
    while ( ( next = aggregateProfileTopLevelMap.get( topLevel ) ) != null && !next.equals( topLevel ) ) {
      topLevel = next;
    }
    aggregateProfileTopLevelMap.put( childProfileId, topLevel );
    updateChildren( topLevel, aggregateProfile );
    notifyTopLevel();
  }

  private void updateChildren( String topLevel, AggregateProfile aggregateProfile ) {
    for ( Profile child : aggregateProfile.getChildProfiles() ) {
      String childId = child.getId();
      aggregateProfileTopLevelMap.put( childId, topLevel );
      if ( child instanceof AggregateProfile ) {
        updateChildren( topLevel, (AggregateProfile) child );
      }
    }
  }

  private synchronized void notifyTopLevel() {
    List<AggregateProfile> topLevelProfiles = new ArrayList<AggregateProfile>();
    for ( String topLevelId : new HashSet<String>( aggregateProfileTopLevelMap.values() ) ) {
      topLevelProfiles.add( aggregateProfileMap.get( topLevelId ) );
    }
    NotificationObject notificationObject =
      new NotificationObject( AggregateProfileService.class.getCanonicalName(), AGGREGATE_PROFILES,
        notificationCounter.getAndIncrement(), topLevelProfiles );
    previousNotification = notificationObject;
    delegatingNotifier.notify( notificationObject );
  }

  @Override public List<NotificationObject> getPreviousNotificationObjects() {
    NotificationObject previousNotification = this.previousNotification;
    if ( previousNotification != null ) {
      return new ArrayList<NotificationObject>( Arrays.asList( previousNotification ) );
    }
    return new ArrayList<NotificationObject>();
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
}
