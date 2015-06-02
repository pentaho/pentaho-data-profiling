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

package org.pentaho.profiling.rest.doc;

/**
 * Created by bryan on 4/8/15.
 */
public class EndpointResponse {
  private String type;
  private String description;

  public EndpointResponse() {
    this( null, null );
  }

  public EndpointResponse( String type, String description ) {
    this.type = type;
    this.description = description;
  }

  public String getType() {
    return type;
  }

  public void setType( String type ) {
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription( String description ) {
    this.description = description;
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

    EndpointResponse that = (EndpointResponse) o;

    if ( type != null ? !type.equals( that.type ) : that.type != null ) {
      return false;
    }
    return !( description != null ? !description.equals( that.description ) : that.description != null );

  }

  @Override public int hashCode() {
    int result = type != null ? type.hashCode() : 0;
    result = 31 * result + ( description != null ? description.hashCode() : 0 );
    return result;
  }

  @Override public String toString() {
    return "EndpointResponse{" +
      "type='" + type + '\'' +
      ", description='" + description + '\'' +
      '}';
  }
  //CHECKSTYLE:OperatorWrap:ON
}
