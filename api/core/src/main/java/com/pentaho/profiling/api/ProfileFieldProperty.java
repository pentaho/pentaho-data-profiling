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

package com.pentaho.profiling.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bryan on 9/7/14.
 */
public class ProfileFieldProperty {
  private String namePath;
  private String nameKey;
  private List<String> pathToProperty;

  public ProfileFieldProperty() {
    this( null, null, null );
  }

  public ProfileFieldProperty( String namePath, String nameKey, List<String> pathToProperty ) {
    this.namePath = namePath;
    this.nameKey = nameKey;
    if ( pathToProperty == null ) {
      pathToProperty = new ArrayList<String>();
    }
    this.pathToProperty = Collections.unmodifiableList( new ArrayList<String>( pathToProperty ) );
  }

  public String getNamePath() {
    return namePath;
  }

  public String getNameKey() {
    return nameKey;
  }

  public List<String> getPathToProperty() {
    return pathToProperty;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    ProfileFieldProperty that = (ProfileFieldProperty) o;

    if ( !nameKey.equals( that.nameKey ) ) {
      return false;
    }
    if ( !namePath.equals( that.namePath ) ) {
      return false;
    }
    if ( !pathToProperty.equals( that.pathToProperty ) ) {
      return false;
    }

    return true;
  }

  @Override public String toString() {
    return "ProfileFieldProperty{"
      + "namePath='"
      + namePath
      + '\''
      + ", nameKey='"
      + nameKey
      + '\''
      + ", pathToProperty="
      + pathToProperty
      + '}';
  }

  @Override
  public int hashCode() {
    int result = namePath.hashCode();
    result = 31 * result + nameKey.hashCode();
    result = 31 * result + pathToProperty.hashCode();
    return result;
  }
}
