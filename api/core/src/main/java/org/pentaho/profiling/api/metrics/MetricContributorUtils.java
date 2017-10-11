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

import java.util.Arrays;
import java.util.List;

/**
 * Created by mhall on 25/01/15.
 */
public class MetricContributorUtils {
  public static final String TYPE = "types";
  public static final String FIELD_NOT_FOUND = "Expected field was not found: ";
  public static final String STATISTICS = "statistics";
  public static final String COUNT = "count";
  public static final String CATEGORIES = "categories";
  public static final String CATEGORICAL = "categorical";
  /**
   * Unit test only
   */
  protected MetricContributorUtils() {

  }

  public static ProfileFieldProperty createMetricProperty( String keyPath, String nameKey, String valueTypeMetricsName,
                                                           String propertyName ) {
    return new ProfileFieldProperty( keyPath, nameKey,
      Arrays.asList( "types", "valueTypeMetricsMap", valueTypeMetricsName, propertyName ) );
  }

  public static String[] removeType( List<String> pathToProperty ) {
    if ( pathToProperty != null ) {
      if ( pathToProperty.size() >= 2 && "type".equals( pathToProperty.get( 0 ) ) ) {
        pathToProperty = pathToProperty.subList( 1, pathToProperty.size() );
      }
      return pathToProperty.toArray( new String[ pathToProperty.size() ] );
    }
    return null;
  }
}
