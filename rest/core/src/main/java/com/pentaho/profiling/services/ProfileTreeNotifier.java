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
