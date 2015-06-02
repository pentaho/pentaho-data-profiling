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

package org.pentaho.profiling.api;

import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.commit.CommitStrategy;
import org.pentaho.profiling.api.mapper.HasStatusMessages;
import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;

import java.util.List;

/**
 * Created by bryan on 3/23/15.
 */
public interface StreamingProfile extends Profile {
  String STREAMING_PROFILE = "STREAMING_PROFILE";

  <T> T perform( ProfileStatusWriteOperation<T> profileStatusWriteOperation );

  void processRecord( List<DataSourceFieldValue> dataSourceFieldValues ) throws ProfileActionException;

  void setCommitStrategy( CommitStrategy strategy );

  void setHasStatusMessages( HasStatusMessages hasStatusMessages );
}
