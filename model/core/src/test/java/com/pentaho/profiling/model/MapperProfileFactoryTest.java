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

import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.configuration.DataSourceMetadata;
import com.pentaho.profiling.api.configuration.ProfileConfiguration;
import com.pentaho.profiling.api.mapper.MapperDefinition;
import com.pentaho.profiling.api.metrics.MetricContributorsFactory;
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
