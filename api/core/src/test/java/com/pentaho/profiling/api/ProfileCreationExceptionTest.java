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

package com.pentaho.profiling.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by bryan on 8/14/14.
 */
public class ProfileCreationExceptionTest {
  @Test
  public void testMessageConstructor() {
    String message = "MESSAGE";
    assertEquals( message, new ProfileCreationException( message ).getMessage() );
  }

  @Test
  public void testThrowableConstructor() {
    Throwable throwable = new Throwable( "MESSAGE_2" );
    assertEquals( throwable, new ProfileCreationException( throwable ).getCause() );
  }

  @Test
  public void testMessageAndThrowableConstructor() {
    String message = "MESSAGE";
    Throwable throwable = new Throwable( "MESSAGE_2" );
    ProfileCreationException profileCreationException = new ProfileCreationException( message, throwable );
    assertEquals( message, profileCreationException.getMessage() );
    assertEquals( throwable, profileCreationException.getCause() );
  }
}
