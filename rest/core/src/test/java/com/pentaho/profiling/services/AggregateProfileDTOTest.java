/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2015 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */

package com.pentaho.profiling.services;

import com.pentaho.profiling.api.AggregateProfile;
import com.pentaho.profiling.api.Profile;
import com.pentaho.profiling.api.core.test.BeanTester;
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
