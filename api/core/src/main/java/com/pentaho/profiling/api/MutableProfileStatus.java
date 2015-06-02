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

import org.pentaho.profiling.api.action.ProfileActionExceptionWrapper;

import java.util.List;
import java.util.Map;

/**
 * Created by bryan on 9/29/14.
 */
public interface MutableProfileStatus extends ProfileStatus {
  void setProfileState( ProfileState profileState );

  MutableProfileField getOrCreateField( String physicalName, String logicalName );

  Map<String, MutableProfileField> getMutableFieldMap();

  void addField( ProfileField field );

  void setField( ProfileField field );

  void setName( String name );

  void setTotalEntities( Long totalEntities );

  void setStatusMessages( List<ProfileStatusMessage> messages );

  void setOperationError( ProfileActionExceptionWrapper operationError );

  void setProfileFieldProperties( List<ProfileFieldProperty> profileFieldProperties );
}
