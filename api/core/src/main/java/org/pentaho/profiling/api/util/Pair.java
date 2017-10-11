/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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

package org.pentaho.profiling.api.util;

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
