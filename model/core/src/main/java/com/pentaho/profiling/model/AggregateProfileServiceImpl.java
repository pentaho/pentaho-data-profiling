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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bryan on 3/5/15.
 */
public class AggregateProfileServiceImpl implements AggregateProfileService {
  private final Map<String, AggregateProfile> aggregateProfileMap = new ConcurrentHashMap<String, AggregateProfile>();
  private final Map<String, String> aggregateProfileTopLevelMap = new ConcurrentHashMap<String, String>();

  public void registerAggregateProfile( AggregateProfile aggregateProfile ) {
    String id = aggregateProfile.getId();
    aggregateProfileMap.put( id, aggregateProfile );
    aggregateProfileTopLevelMap.put( id, id );
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
  }

  private void updateChildren( String topLevel, AggregateProfile aggregateProfile ) {
    for ( Profile child : aggregateProfile.getChildProfiles() ) {
      aggregateProfileTopLevelMap.put( child.getId(), topLevel );
      if ( child instanceof AggregateProfile ) {
        updateChildren( topLevel, (AggregateProfile) child );
      }
    }
  }
}
