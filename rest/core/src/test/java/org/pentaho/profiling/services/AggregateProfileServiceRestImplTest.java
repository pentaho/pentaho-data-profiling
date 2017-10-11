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

package org.pentaho.profiling.services;

import org.pentaho.profiling.api.AggregateProfile;
import org.pentaho.profiling.api.AggregateProfileService;
import org.pentaho.profiling.api.Profile;
import org.pentaho.profiling.api.sample.SampleProviderManager;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 3/6/15.
 */
public class AggregateProfileServiceRestImplTest {
  private AggregateProfileService delegate;
  private AggregateProfileServiceRestImpl aggregateProfileService;
  private SampleProviderManager sampleProviderManager;

  @Before
  public void setup() {
    delegate = mock( AggregateProfileService.class );
    sampleProviderManager = mock( SampleProviderManager.class );
    aggregateProfileService = new AggregateProfileServiceRestImpl( delegate, sampleProviderManager );
  }

  @Test
  public void testGetAggregateProfileDTOs() {
    AggregateProfile aggregateProfile = mock( AggregateProfile.class );
    AggregateProfile childProfile = mock( AggregateProfile.class );
    String id = "aggregateProfile";
    String childId = "childProfile";
    when( aggregateProfile.getId() ).thenReturn( id );
    when( childProfile.getId() ).thenReturn( childId );
    when( aggregateProfile.getChildProfiles() ).thenReturn( Arrays.<Profile>asList( childProfile ) );
    when( childProfile.getChildProfiles() ).thenReturn( null );
    List<AggregateProfile> profiles = Arrays.asList( aggregateProfile, childProfile );
    when( delegate.getAggregateProfiles() ).thenReturn( profiles );
    assertEquals( 1, aggregateProfileService.getAggregateProfileDTOs().size() );
  }

  @Test
  public void testAddChild() {
    AggregateAddChildWrapper aggregateAddChildWrapper = new AggregateAddChildWrapper( "testParent", "testChild" );
    aggregateProfileService.addChild( aggregateAddChildWrapper );
    verify( delegate )
      .addChild( aggregateAddChildWrapper.getProfileId(), aggregateAddChildWrapper.getChildProfileId() );
  }

  @Test
  public void testGetProfile() {
    AggregateProfile aggregateProfile = mock( AggregateProfile.class );
    when( delegate.getAggregateProfile( "test" ) ).thenReturn( aggregateProfile );
    assertEquals( aggregateProfile, aggregateProfileService.getAggregateProfile( "test" ) );
  }
}
