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
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricContributor;
import com.pentaho.profiling.api.metrics.MetricContributors;
import com.pentaho.profiling.api.metrics.MetricContributorsFactory;
import com.pentaho.profiling.api.metrics.ProfileFieldProperties;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldManager;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 4/2/15.
 */
public class StreamingProfileImplTest {
  private ProfileStatusManager profileStatusManager;
  private MutableProfileStatus mutableProfileStatus;
  private MetricContributor metricContributor;
  private StreamingProfileImpl streamingProfile;
  private ExecutorService executorService;

  @Before
  public void setup() {
    profileStatusManager = mock( ProfileStatusManager.class );
    mutableProfileStatus = mock( MutableProfileStatus.class );
    when( profileStatusManager.write( any( ProfileStatusWriteOperation.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        return ( (ProfileStatusWriteOperation) invocation.getArguments()[ 0 ] ).write( mutableProfileStatus );
      }
    } );
    MetricContributorsFactory metricContributorsFactory = mock( MetricContributorsFactory.class );
    MetricContributors metricContributors = mock( MetricContributors.class );
    executorService = mock( ExecutorService.class );
    when( executorService.submit( isA( Runnable.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        ( (Runnable) invocation.getArguments()[ 0 ] ).run();
        return null;
      }
    } );
    ArgumentCaptor<List> profileFieldPropertiesCaptor = ArgumentCaptor.forClass( List.class );
    metricContributor = mock( MetricContributor.class );
    ProfileFieldProperty profileFieldProperty = mock( ProfileFieldProperty.class );
    when( metricContributor.getProfileFieldProperties() ).thenReturn( Arrays.asList( profileFieldProperty ) );
    when( metricContributorsFactory.construct( metricContributors ) ).thenReturn( Arrays.asList( metricContributor ) );
    streamingProfile = new StreamingProfileImpl( profileStatusManager, metricContributorsFactory, metricContributors );
    verify( mutableProfileStatus ).setProfileFieldProperties( profileFieldPropertiesCaptor.capture() );
    List<ProfileFieldProperty> profileFieldProperties = profileFieldPropertiesCaptor.getValue();
    List<ProfileFieldProperty> expectedProfileFieldProperties =
      new ArrayList<ProfileFieldProperty>( Arrays.asList( ProfileFieldProperties.LOGICAL_NAME,
        ProfileFieldProperties.PHYSICAL_NAME, ProfileFieldProperties.FIELD_TYPE,
        ProfileFieldProperties.COUNT_FIELD, profileFieldProperty ) );
    assertEquals( expectedProfileFieldProperties, profileFieldProperties );
  }

  @Test
  public void testConstructor() {
    // Noop, setup tests constructor
  }

  @Test
  public void testGetId() {
    String id = "test-id";
    when( profileStatusManager.getId() ).thenReturn( id );
    assertEquals( id, streamingProfile.getId() );
  }

  @Test
  public void testGetName() {
    String name = "test-name";
    when( profileStatusManager.getName() ).thenReturn( name );
    assertEquals( name, streamingProfile.getName() );
  }

  @Test
  public void testStartStopIsRunning() {
    assertFalse( streamingProfile.isRunning() );
    streamingProfile.start( executorService );
    assertTrue( streamingProfile.isRunning() );
    streamingProfile.stop();
    assertFalse( streamingProfile.isRunning() );
  }

  @Test
  public void testProcessRecord() throws ProfileActionException {
    List<DataSourceFieldValue> dataSourceFieldValues = new ArrayList<DataSourceFieldValue>();
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( "test-value" );
    dataSourceFieldValue.setPhysicalName( "test-physical-name" );
    dataSourceFieldValue.setLogicalName( "test-logical-name" );
    dataSourceFieldValues.add( dataSourceFieldValue );
    streamingProfile.start( executorService );
    streamingProfile.processRecord( dataSourceFieldValues );
    verify( metricContributor ).processFields( isA( DataSourceFieldManager.class ), eq( dataSourceFieldValues ) );
    verify( metricContributor ).setDerived( isA( DataSourceFieldManager.class ) );
  }
}
