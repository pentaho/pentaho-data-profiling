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

package org.pentaho.profiling.api.metrics;

import org.pentaho.profiling.api.ProfileFieldProperty;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by mhall on 28/01/15.
 */
public class MetricContributorUtilsTest {
  @Test
  public void testConstructor() {
    // This is only done for cobertura's sake
    new MetricContributorUtils();
  }

  @Test public void testCreateMetricProperty() {
    String keyPath = "test-key-path";
    String nameKey = "test-name-key";
    String path1 = "path1";
    String path2 = "path2";
    ProfileFieldProperty metricProperty = MetricContributorUtils.createMetricProperty( keyPath, nameKey, path1, path2 );
    assertNotNull( metricProperty );
    assertEquals( keyPath, metricProperty.getNamePath() );
    assertEquals( nameKey, metricProperty.getNameKey() );
    assertEquals( 4, metricProperty.getPathToProperty().size() );
    assertEquals( "types", metricProperty.getPathToProperty().get( 0 ) );
    assertEquals( path1, metricProperty.getPathToProperty().get( 2 ) );
    assertEquals( path2, metricProperty.getPathToProperty().get( 3 ) );
  }
}
