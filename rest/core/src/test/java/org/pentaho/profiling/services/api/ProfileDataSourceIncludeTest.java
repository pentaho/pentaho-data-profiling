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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by bryan on 8/15/14.
 */
public class ProfileDataSourceIncludeTest {
  @Test
  public void testNoArgConstructor() {
    ProfileDataSourceInclude profileDataSourceInclude = new ProfileDataSourceInclude( );
    assertNull( profileDataSourceInclude.getRequire() );
    assertNull( profileDataSourceInclude.getUrl() );
  }

  @Test
  public void testUrlConstructor() {
    String url = "test_url";
    ProfileDataSourceInclude profileDataSourceInclude = new ProfileDataSourceInclude( url );
    assertNull( profileDataSourceInclude.getRequire() );
    assertEquals( url, profileDataSourceInclude.getUrl() );
  }

  @Test
  public void testUrlAndRequireConstructor() {
    String url = "test_url";
    String require = "test_require";
    ProfileDataSourceInclude profileDataSourceInclude = new ProfileDataSourceInclude( url, require );
    assertEquals( require, profileDataSourceInclude.getRequire() );
    assertEquals( url, profileDataSourceInclude.getUrl() );
  }

  @Test
  public void testSetUrl() {
    String url = "test_url";
    ProfileDataSourceInclude profileDataSourceInclude = new ProfileDataSourceInclude( );
    profileDataSourceInclude.setUrl( url );
    assertEquals( url, profileDataSourceInclude.getUrl() );
  }

  @Test
  public void testSetRequire() {
    String require = "test_require";
    ProfileDataSourceInclude profileDataSourceInclude = new ProfileDataSourceInclude( );
    profileDataSourceInclude.setRequire( require );
    assertEquals( require, profileDataSourceInclude.getRequire() );
  }
}
