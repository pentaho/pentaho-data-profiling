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
import com.pentaho.profiling.api.AggregateProfileService;
import org.junit.Before;
import org.junit.Test;

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

  @Before
  public void setup() {
    delegate = mock( AggregateProfileService.class );
    aggregateProfileService = new AggregateProfileServiceRestImpl( delegate );
  }

  @Test
  public void testGetAggregateProfiles() {
    List<AggregateProfile> profiles = mock( List.class );
    when( delegate.getAggregateProfiles() ).thenReturn( profiles );
    assertEquals( profiles, aggregateProfileService.getAggregateProfiles() );
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
