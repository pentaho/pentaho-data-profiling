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

package org.pentaho.profiling.model;

import org.pentaho.profiling.api.AggregateProfile;
import org.pentaho.profiling.api.Profile;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 3/6/15.
 */
public class AggregateProfileServiceImplTest {
  private AggregateProfileServiceImpl aggregateProfileService;

  @Before
  public void setup() {
    aggregateProfileService = new AggregateProfileServiceImpl();
  }

  @Test
  public void testRegisterGet() {
    AggregateProfile aggregateProfile = mock( AggregateProfile.class );
    String value = "test-id";
    when( aggregateProfile.getId() ).thenReturn( value );
    aggregateProfileService.registerAggregateProfile( aggregateProfile );
    List<AggregateProfile> aggregateProfiles = aggregateProfileService.getAggregateProfiles();
    assertEquals( 1, aggregateProfiles.size() );
    assertEquals( aggregateProfile, aggregateProfiles.get( 0 ) );
  }

  @Test
  public void testAddChild() {
    AggregateProfile aggregateProfile = mock( AggregateProfile.class );
    String value = "test-id";
    String childId = "child-id";
    when( aggregateProfile.getId() ).thenReturn( value );
    aggregateProfileService.registerAggregateProfile( aggregateProfile );
    aggregateProfileService.addChild( value, childId );
    verify( aggregateProfile ).addChildProfile( childId );
    assertEquals( aggregateProfile, aggregateProfileService.getAggregateProfile( childId ) );
    assertNull( aggregateProfileService.getAggregateProfile( "fake" ) );
  }
}
