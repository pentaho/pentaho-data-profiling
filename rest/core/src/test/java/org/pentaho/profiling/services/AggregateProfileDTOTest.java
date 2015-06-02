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

package org.pentaho.profiling.services;

import org.pentaho.profiling.api.AggregateProfile;
import org.pentaho.profiling.api.Profile;
import org.pentaho.profiling.api.core.test.BeanTester;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 4/16/15.
 */
public class AggregateProfileDTOTest extends BeanTester {
  public AggregateProfileDTOTest() {
    super( AggregateProfileDTO.class );
  }

  @Test
  public void testForProfiles() {
    String aggregateProfileId = "aggregateProfileId";
    String aggregateProfileName = "aggregateProfileName";
    String childAggregateId = "childAggregateId";
    String childAggregateName = "childAggregateName";
    String profileId = "profileId";
    String profileNmae = "profileName";
    AggregateProfile aggregateProfile = mock( AggregateProfile.class );
    when( aggregateProfile.getId() ).thenReturn( aggregateProfileId );
    when( aggregateProfile.getName() ).thenReturn( aggregateProfileName );
    AggregateProfile childAggregate = mock( AggregateProfile.class );
    when( childAggregate.getId() ).thenReturn( childAggregateId );
    when( childAggregate.getName() ).thenReturn( childAggregateName );
    Profile childProfile = mock( Profile.class );
    when( childProfile.getId() ).thenReturn( profileId );
    when( childProfile.getName() ).thenReturn( profileNmae );
    when( aggregateProfile.getChildProfiles() ).thenReturn( Arrays.<Profile>asList( childAggregate ) );
    when( childAggregate.getChildProfiles() ).thenReturn( Arrays.asList( childProfile ) );

    List<AggregateProfileDTO> aggregateProfileDTOs =
      AggregateProfileDTO.forProfiles( Arrays.asList( aggregateProfile ) );
    assertEquals( 1, aggregateProfileDTOs.size() );
    AggregateProfileDTO aggregateProfileDTO = aggregateProfileDTOs.get( 0 );
    assertEquals( aggregateProfileId, aggregateProfileDTO.getId() );
    assertEquals( aggregateProfileName, aggregateProfileDTO.getName() );
    List<AggregateProfileDTO> aggregateProfileDTOChildProfiles = aggregateProfileDTO.getChildProfiles();
    assertEquals( 1, aggregateProfileDTOChildProfiles.size() );
    AggregateProfileDTO childAggregateDTO = aggregateProfileDTOChildProfiles.get( 0 );
    assertEquals( childAggregateId, childAggregateDTO.getId() );
    assertEquals( childAggregateName, childAggregateDTO.getName() );
    List<AggregateProfileDTO> childAggregateDTOChildProfiles = childAggregateDTO.getChildProfiles();
    assertEquals( 1, childAggregateDTOChildProfiles.size() );
    AggregateProfileDTO childProfileDTO = childAggregateDTOChildProfiles.get( 0 );
    assertEquals( profileId, childProfileDTO.getId() );
    assertEquals( profileNmae, childProfileDTO.getName() );
    assertEquals( 0, childProfileDTO.getChildProfiles().size() );
  }

  @Test
  public void testForProfilesNull() {
    assertEquals( 0, AggregateProfileDTO.forProfiles( null ).size() );
  }

  @Test
  public void testAggregateProfileNullChildren() {
    AggregateProfile aggregateProfile = mock( AggregateProfile.class );
    when( aggregateProfile.getChildProfiles() ).thenReturn( null );
    assertEquals( 0, new AggregateProfileDTO( aggregateProfile ).getChildProfiles().size() );
  }
}
