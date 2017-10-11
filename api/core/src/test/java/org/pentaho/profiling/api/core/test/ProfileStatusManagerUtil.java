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

package org.pentaho.profiling.api.core.test;

import org.pentaho.profiling.api.MutableProfileStatus;
import org.pentaho.profiling.api.ProfileStatusManager;
import org.pentaho.profiling.api.ProfileStatusWriteOperation;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 5/14/15.
 */
public class ProfileStatusManagerUtil {
  public static MutableProfileStatus mockMutableProfileStatus( ProfileStatusManager profileStatusManager ) {
    final MutableProfileStatus mutableProfileStatus = mock( MutableProfileStatus.class );
    when( profileStatusManager.write( any( ProfileStatusWriteOperation.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        ProfileStatusWriteOperation profileStatusWriteOperation =
          (ProfileStatusWriteOperation) invocation.getArguments()[ 0 ];
        return profileStatusWriteOperation.write( mutableProfileStatus );
      }
    } );
    return mutableProfileStatus;
  }
}
