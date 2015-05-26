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
