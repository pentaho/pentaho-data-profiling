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

package org.pentaho.profiling.api.configuration.core;

import org.pentaho.profiling.api.AggregateProfile;
import org.pentaho.profiling.api.configuration.DataSourceMetadata;

/**
 * Created by bryan on 4/10/15.
 */
public class AggregateProfileMetadata implements DataSourceMetadata {
  private String name;

  public AggregateProfileMetadata() {
    this( null );
  }

  public AggregateProfileMetadata( String name ) {
    this.name = name;
  }

  @Override public String getLabel() {
    return AggregateProfile.AGGREGATE_PROFILE;
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }
}
