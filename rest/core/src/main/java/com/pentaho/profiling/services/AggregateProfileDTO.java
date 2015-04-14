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
import com.pentaho.profiling.api.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bryan on 4/14/15.
 */
public class AggregateProfileDTO {
  private String name;
  private String id;
  private List<AggregateProfileDTO> childProfiles;

  public AggregateProfileDTO() {
    this( null, null, null );
  }

  public AggregateProfileDTO( String name, String id,
                              List<AggregateProfileDTO> childProfiles ) {
    this.name = name;
    this.id = id;
    this.childProfiles = childProfiles;
  }

  public AggregateProfileDTO( AggregateProfile aggregateProfile ) {
    this( aggregateProfile.getName(), aggregateProfile.getId(), buildChildren( aggregateProfile ) );
  }

  public AggregateProfileDTO( Profile profile ) {
    this( profile.getName(), profile.getId(), new ArrayList<AggregateProfileDTO>() );
  }

  public static List<AggregateProfileDTO> forProfiles( List<AggregateProfile> aggregateProfiles ) {
    if ( aggregateProfiles == null ) {
      return null;
    }
    List<AggregateProfileDTO> result = new ArrayList<AggregateProfileDTO>( aggregateProfiles.size() );
    for ( AggregateProfile aggregateProfile : aggregateProfiles ) {
      result.add( new AggregateProfileDTO( aggregateProfile ) );
    }
    return result;
  }

  private static List<AggregateProfileDTO> buildChildren( AggregateProfile aggregateProfile ) {
    List<Profile> childProfiles = aggregateProfile.getChildProfiles();
    if ( childProfiles == null ) {
      return null;
    }
    List<AggregateProfileDTO> result = new ArrayList<AggregateProfileDTO>( childProfiles.size() );
    for ( Profile childProfile : childProfiles ) {
      if ( childProfile instanceof AggregateProfile ) {
        result.add( new AggregateProfileDTO( (AggregateProfile) childProfile ) );
      } else {
        result.add( new AggregateProfileDTO( childProfile ) );
      }
    }
    return result;
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId( String id ) {
    this.id = id;
  }

  public List<AggregateProfileDTO> getChildProfiles() {
    return childProfiles;
  }

  public void setChildProfiles( List<AggregateProfileDTO> childProfiles ) {
    this.childProfiles = childProfiles;
  }
}
