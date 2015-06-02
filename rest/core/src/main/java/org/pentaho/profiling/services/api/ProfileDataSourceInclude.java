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

package org.pentaho.profiling.services.api;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bryan on 8/6/14.
 */
@XmlRootElement
public class ProfileDataSourceInclude {
  private String url;
  private String require;

  public ProfileDataSourceInclude() {
    this( null );
  }

  public ProfileDataSourceInclude( String url ) {
    this( url, null );
  }

  public ProfileDataSourceInclude( String url, String require ) {
    this.url = url;
    this.require = require;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl( String url ) {
    this.url = url;
  }

  public String getRequire() {
    return require;
  }

  public void setRequire( String require ) {
    this.require = require;
  }
}
