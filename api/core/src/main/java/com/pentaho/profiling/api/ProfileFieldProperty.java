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
    return "ProfileFieldProperty{" +
      "namePath='" + namePath + '\'' +
      ", nameKey='" + nameKey + '\'' +
      ", pathToProperty=" + pathToProperty +
      '}';
  }

  @Override
  public int hashCode() {
    int result = namePath.hashCode();
    result = 31 * result + nameKey.hashCode();
    result = 31 * result + pathToProperty.hashCode();
    return result;
  }
}
