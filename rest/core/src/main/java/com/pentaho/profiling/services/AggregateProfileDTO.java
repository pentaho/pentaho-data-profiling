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

package org.pentaho.profiling.services;

import org.pentaho.profiling.api.AggregateProfile;
import org.pentaho.profiling.api.Profile;

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
      return new ArrayList<AggregateProfileDTO>();
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
      return new ArrayList<AggregateProfileDTO>();
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

  //OperatorWrap isn't helpful for autogenerated methods
  //CHECKSTYLE:OperatorWrap:OFF
  @Override public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    AggregateProfileDTO that = (AggregateProfileDTO) o;

    if ( name != null ? !name.equals( that.name ) : that.name != null ) {
      return false;
    }
    if ( id != null ? !id.equals( that.id ) : that.id != null ) {
      return false;
    }
    return !( childProfiles != null ? !childProfiles.equals( that.childProfiles ) : that.childProfiles != null );

  }

  @Override public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + ( id != null ? id.hashCode() : 0 );
    result = 31 * result + ( childProfiles != null ? childProfiles.hashCode() : 0 );
    return result;
  }

  @Override public String toString() {
    return "AggregateProfileDTO{" +
      "name='" + name + '\'' +
      ", id='" + id + '\'' +
      ", childProfiles=" + childProfiles +
      '}';
  }
  //CHECKSTYLE:OperatorWrap:ON
}
