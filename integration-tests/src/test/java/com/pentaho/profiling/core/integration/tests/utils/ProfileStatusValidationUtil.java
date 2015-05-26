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

package com.pentaho.profiling.core.integration.tests.utils;

import com.pentaho.profiling.api.MutableProfileField;
import com.pentaho.profiling.api.ProfileField;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.dto.ProfileFieldDTO;
import com.pentaho.profiling.api.json.ObjectMapperFactory;
import com.pentaho.profiling.api.metrics.MetricContributor;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.model.MutableProfileStatusImpl;
import com.pentaho.profiling.model.ProfileStatusImpl;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by bryan on 3/27/15.
 */
public class ProfileStatusValidationUtil {
  public static void validateProfileFieldsAgainstRecords( ProfileStatus profileStatus,
                                                          List<MetricContributor> metricContributors,
                                                          List<List<DataSourceFieldValue>> records )
    throws ProfileActionException, IOException {
    MutableProfileStatusImpl mutableProfileStatus =
      new MutableProfileStatusImpl( new ProfileStatusImpl( null, null, null ) );
    for ( List<DataSourceFieldValue> dataSourceFieldValues : records ) {
      for ( DataSourceFieldValue dataSourceFieldValue : dataSourceFieldValues ) {
        MutableProfileField field = mutableProfileStatus
          .getOrCreateField( dataSourceFieldValue.getPhysicalName(), dataSourceFieldValue.getLogicalName() );
        field.getOrCreateValueTypeMetrics( dataSourceFieldValue.getFieldTypeName() ).incrementCount();
      }
      for ( MetricContributor metricContributor : metricContributors ) {
        metricContributor.processFields( mutableProfileStatus, dataSourceFieldValues );
      }
    }
    for ( MetricContributor metricContributor : metricContributors ) {
      metricContributor.setDerived( mutableProfileStatus );
    }
    List<ProfileField> dtos = new ArrayList<ProfileField>();
    for ( ProfileField profileField : mutableProfileStatus.getFields() ) {
      dtos.add( new ProfileFieldDTO( profileField ) );
    }
    ObjectMapper outputMapper =
      new ObjectMapperFactory( ProfileStatusValidationUtil.class.getClassLoader() ).createMapper();
    ObjectMapper inputMapper = new ObjectMapper();
    assertEquals( inputMapper.readTree( outputMapper.writeValueAsString( profileStatus.getFields() ) ),
      inputMapper.readTree( outputMapper.writeValueAsString( dtos ) ) );
  }
}
