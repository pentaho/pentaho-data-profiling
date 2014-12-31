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

/**
 * Created by bryan on 12/2/14.
 */
public class Pair<First, Second> {
  private final First first;
  private final Second second;

  public static <First, Second> Pair of( First first, Second second ) {
    return new Pair( first, second );
  }

  public Pair( First first, Second second ) {
    this.first = first;
    this.second = second;
  }

  public First getFirst() {
    return first;
  }

  public Second getSecond() {
    return second;
  }

  @Override public String toString() {
    return "Pair{"
      + "first="
      + first
      + ", second="
      + second
      + '}';
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    Pair pair = (Pair) o;

    if ( first != null ? !first.equals( pair.first ) : pair.first != null ) {
      return false;
    }
    if ( second != null ? !second.equals( pair.second ) : pair.second != null ) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = first != null ? first.hashCode() : 0;
    result = 31 * result + ( second != null ? second.hashCode() : 0 );
    return result;
  }
}
