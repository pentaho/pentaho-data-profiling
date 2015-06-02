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

package org.pentaho.profiling.api.metrics;

import org.pentaho.profiling.api.MessageUtils;
import org.pentaho.profiling.api.ProfileFieldProperty;
import org.pentaho.profiling.api.stats.Statistic;

import java.util.Arrays;

/**
 * Created by bryan on 2/9/15.
 */
public class ProfileFieldProperties {
  public static final String KEY = "profiling-metrics-api";
  public static final String KEY_PATH = MessageUtils.getId( KEY, ProfileFieldProperties.class );
  public static final ProfileFieldProperty LOGICAL_NAME = new ProfileFieldProperty( KEY_PATH,
    "logicalName", Arrays.asList( "logicalName" ) );
  public static final ProfileFieldProperty PHYSICAL_NAME = new ProfileFieldProperty( KEY_PATH,
    "physicalName", Arrays.asList( "physicalName" ) );
  public static final ProfileFieldProperty FIELD_TYPE =
    new ProfileFieldProperty( KEY_PATH, "typeName",
      Arrays.asList( "types", "typeName" ) );
  public static final ProfileFieldProperty COUNT_FIELD = new ProfileFieldProperty( KEY_PATH, Statistic.COUNT,
    Arrays.asList( "types", Statistic.COUNT ) );
}
