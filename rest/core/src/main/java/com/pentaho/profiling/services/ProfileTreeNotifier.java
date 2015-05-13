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

import com.pentaho.profiling.api.AggregateProfile;
import com.pentaho.profiling.api.AggregateProfileService;
import com.pentaho.profiling.api.Profile;
import com.pentaho.profiling.api.ProfilingService;
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
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by bryan on 5/12/15.
 */
public class ProfileTreeNotifier implements NotifierWithHistory, NotificationListener {
  public static final String PROFILING_SERVICE_CANONICAL_NAME = ProfilingService.class.getCanonicalName();
  public static final String AGGREGATE_PROFILE_SERVICE_CANONICAL_NAME =
    AggregateProfileService.class.getCanonicalName();
  public static final String CANONICAL_NAME = ProfileTreeNotifier.class.getCanonicalName();
  public static final String PROFILE_TREE = "profileTree";
  private final DelegatingNotifierImpl delegatingNotifier =
    new DelegatingNotifierImpl(
      new HashSet<String>( Arrays.asList( ProfileTreeNotifier.class.getCanonicalName() ) ),
      this );
  private final AtomicLong seqence = new AtomicLong( 1L );
  private volatile NotificationObject previousNotificationObject;
  private volatile List<AggregateProfile> aggregateProfiles = new ArrayList<AggregateProfile>();
  private volatile List<Profile> allProfiles = new ArrayList<Profile>();

  @Override public List<NotificationObject> getPreviousNotificationObjects() {
    NotificationObject previousNotificationObject = this.previousNotificationObject;
    if ( previousNotificationObject == null ) {
      return new ArrayList<NotificationObject>();
    }
    return new ArrayList<NotificationObject>( Arrays.asList( previousNotificationObject ) );
  }

  public void addNotifier( NotifierWithHistory notifierWithHistory ) {
    if ( notifierWithHistory == null ) {
      return;
    }
    Set<String> emittedTypes = notifierWithHistory.getEmittedTypes();
    if ( emittedTypes.contains( PROFILING_SERVICE_CANONICAL_NAME ) || emittedTypes.contains(
      AGGREGATE_PROFILE_SERVICE_CANONICAL_NAME ) ) {
      notifierWithHistory.register( this );
    }
  }

  @Override public void notify( NotificationObject notificationObject ) {
    if ( PROFILING_SERVICE_CANONICAL_NAME.equals( notificationObject.getType() ) && ProfilingService.PROFILES
      .equals( notificationObject.getId() ) ) {
      allProfiles = (List<Profile>) notificationObject.getObject();
    } else if ( AGGREGATE_PROFILE_SERVICE_CANONICAL_NAME.equals( notificationObject.getType() )
      && AggregateProfileService.AGGREGATE_PROFILES.equals( notificationObject.getId() ) ) {
      aggregateProfiles = (List<AggregateProfile>) notificationObject.getObject();
    } else {
      return;
    }
    List<AggregateProfileDTO> aggregateProfileDTOs = AggregateProfileDTO.forProfiles( aggregateProfiles );
    Set<String> representedIds = new HashSet<String>();
    buildRepresentedIds( representedIds, aggregateProfileDTOs );
    for ( Profile profile : allProfiles ) {
      if ( !representedIds.contains( profile.getId() ) ) {
        aggregateProfileDTOs.add( new AggregateProfileDTO( profile ) );
      }
    }
    Collections.sort( aggregateProfileDTOs, new Comparator<AggregateProfileDTO>() {
      @Override public int compare( AggregateProfileDTO o1, AggregateProfileDTO o2 ) {
        if ( o1.getName() == null ) {
          if ( o2.getName() == null ) {
            return o1.getId().compareTo( o2.getId() );
          } else {
            return 1;
          }
        } else if ( o2.getName() == null ) {
          return -1;
        }
        return o1.getName().compareTo( o2.getName() );
      }
    } );
    previousNotificationObject =
      new NotificationObject( CANONICAL_NAME, PROFILE_TREE, seqence.getAndIncrement(), aggregateProfileDTOs );
    delegatingNotifier.notify( previousNotificationObject );
  }

  private void buildRepresentedIds( Set<String> representedIds, List<AggregateProfileDTO> aggregateProfileDTOs ) {
    for ( AggregateProfileDTO aggregateProfileDTO : aggregateProfileDTOs ) {
      buildRepresentedIds( representedIds, aggregateProfileDTO );
    }
  }

  private void buildRepresentedIds( Set<String> representedIds, AggregateProfileDTO aggregateProfileDTO ) {
    representedIds.add( aggregateProfileDTO.getId() );
    List<AggregateProfileDTO> childProfiles = aggregateProfileDTO.getChildProfiles();
    if ( childProfiles != null ) {
      buildRepresentedIds( representedIds, childProfiles );
    }
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
