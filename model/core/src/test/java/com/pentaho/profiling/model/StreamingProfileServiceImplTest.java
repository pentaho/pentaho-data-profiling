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

package com.pentaho.profiling.model;

import com.pentaho.profiling.api.StreamingProfile;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricContributorsFactory;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 4/2/15.
 */
public class StreamingProfileServiceImplTest {
  @Test
  public void testStreamingProfileServiceImpl() throws ProfileActionException {
    StreamingProfileServiceImpl streamingProfileService = new StreamingProfileServiceImpl();
    String id = "test-id";
    StreamingProfile streamingProfile = mock( StreamingProfile.class );
    when( streamingProfile.getId() ).thenReturn( id );
    streamingProfileService.registerStreamingProfile( streamingProfile );
    StreamingProfile returnValue = streamingProfileService.getStreamingProfile( id );
    assertEquals( streamingProfile, returnValue );
    List<DataSourceFieldValue> dataSourceFieldValues = mock( List.class );
    streamingProfileService.processRecord( id, dataSourceFieldValues );
    verify( streamingProfile ).processRecord( dataSourceFieldValues );
  }
}
