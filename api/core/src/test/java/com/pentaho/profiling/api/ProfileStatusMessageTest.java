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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by bryan on 9/17/14.
 */
public class ProfileStatusMessageTest {
  @Test
  public void testNoArgConstructor() {
    ProfileStatusMessage profileStatusMessage = new ProfileStatusMessage();
    assertNull( profileStatusMessage.getMessagePath() );
    assertNull( profileStatusMessage.getMessageKey() );
    assertEquals( 0, profileStatusMessage.getMessageVariables().size() );
  }

  @Test
  public void testMessagePathMessageKeyMessageVariablesConstructor() {
    String messagePath = "test-path";
    String messageKey = "test-key";
    List<String> messageVariables = new ArrayList<String>( Arrays.asList( "test-var" ) );
    ProfileStatusMessage profileStatusMessage = new ProfileStatusMessage( messagePath, messageKey, messageVariables );
    assertEquals( messagePath, profileStatusMessage.getMessagePath() );
    assertEquals( messageKey, profileStatusMessage.getMessageKey() );
    assertEquals( messageVariables, profileStatusMessage.getMessageVariables() );
  }
}
