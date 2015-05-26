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

package com.pentaho.profiling.api.action;

import com.pentaho.profiling.api.ProfileStatusMessage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by bryan on 3/19/15.
 */
public class ProfileActionExceptionTest {
  @Test
  public void testConstructor() {
    ProfileStatusMessage profileStatusMessage = mock( ProfileStatusMessage.class );
    Exception cause = new Exception();
    ProfileActionException profileActionException = new ProfileActionException( profileStatusMessage, cause );
    assertEquals( cause, profileActionException.getCause() );
    assertEquals( profileStatusMessage, profileActionException.getProfileStatusMessage() );
  }

  @Test
  public void test() {
    System.out.print( byte[].class.getCanonicalName() );
  }
}
