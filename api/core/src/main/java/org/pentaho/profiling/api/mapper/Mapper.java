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

package org.pentaho.profiling.api.mapper;

import org.pentaho.profiling.api.action.ProfileActionException;

/**
 * Interface for easily supporting a new datasource in profiling
 */
public interface Mapper extends HasStatusMessages {
  /**
   * Take data from the datasource and feed it into the StreamingProfile
   *
   * @throws ProfileActionException if an error occurs while processing data
   */
  void run() throws ProfileActionException;

  void commit();

  /**
   * Stop running
   */
  void stop();

  /**
   * Check the current status of the mapper
   *
   * @return a boolean indicating whether the mapper is running or not
   */
  boolean isRunning();
}
