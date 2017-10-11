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

package org.pentaho.profiling.model;

import org.pentaho.profiling.api.MutableProfileStatus;
import org.pentaho.profiling.api.ProfileStatusManager;
import org.pentaho.profiling.api.ProfileStatusWriteOperation;
import org.pentaho.profiling.api.configuration.DataSourceMetadata;
import org.pentaho.profiling.api.configuration.ProfileConfiguration;
import org.pentaho.profiling.api.mapper.MapperDefinition;
import org.pentaho.profiling.api.metrics.MetricContributorsFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 4/24/15.
 */
public class MapperProfileFactoryTest {
  private List<MapperDefinition> mapperDefinitions;
  private MetricContributorsFactory metricContributorsFactory;
  private MapperProfileFactory mapperProfileFactory;

  @Before
  public void setup() {
    mapperDefinitions = new ArrayList<MapperDefinition>();
    metricContributorsFactory = mock( MetricContributorsFactory.class );
    mapperProfileFactory = new MapperProfileFactory( mapperDefinitions, metricContributorsFactory );
  }

  @Test
  public void testAccepts() {
    DataSourceMetadata dataSourceMetadata = mock( DataSourceMetadata.class );
    MapperDefinition mapperDefinition = mock( MapperDefinition.class );
    mapperDefinitions.add( mapperDefinition );
    when( mapperDefinition.accepts( dataSourceMetadata ) ).thenReturn( true ).thenReturn( false );
    assertTrue( mapperProfileFactory.accepts( dataSourceMetadata ) );
    assertFalse( mapperProfileFactory.accepts( dataSourceMetadata ) );
  }

  @Test
  public void testCreate() {
    ProfileConfiguration profileConfiguration = mock( ProfileConfiguration.class );
    DataSourceMetadata dataSourceMetadata = mock( DataSourceMetadata.class );
    MapperDefinition mapperDefinition = mock( MapperDefinition.class );
    mapperDefinitions.add( mapperDefinition );
    ProfileStatusManager profileStatusManager = mock( ProfileStatusManager.class );
    final MutableProfileStatus mutableProfileStatus = mock( MutableProfileStatus.class );

    when( profileConfiguration.getDataSourceMetadata() ).thenReturn( dataSourceMetadata );
    String testLabel = "test-label";
    when( dataSourceMetadata.getLabel() ).thenReturn( testLabel );
    when( mapperDefinition.accepts( dataSourceMetadata ) ).thenReturn( true );
    when( profileStatusManager.write( any( ProfileStatusWriteOperation.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        return ( (ProfileStatusWriteOperation) invocation.getArguments()[ 0 ] ).write( mutableProfileStatus );
      }
    } );
    assertTrue(
      mapperProfileFactory.create( profileConfiguration, profileStatusManager ) instanceof MapperProfileImpl );
    verify( mutableProfileStatus ).setName( testLabel );
  }
}
