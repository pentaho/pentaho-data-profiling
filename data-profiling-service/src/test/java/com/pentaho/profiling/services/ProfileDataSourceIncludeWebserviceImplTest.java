/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
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

import com.pentaho.profiling.api.datasource.DataSourceReference;
import com.pentaho.profiling.services.api.ProfileDataSourceInclude;
import com.pentaho.profiling.services.api.ProfileDataSourceIncludeService;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 8/15/14.
 */
public class ProfileDataSourceIncludeWebserviceImplTest {
  @Test
  public void testSetIncludeServices() {
    List<ProfileDataSourceIncludeService> list = new ArrayList<ProfileDataSourceIncludeService>();
    ProfileDataSourceIncludeService service = mock( ProfileDataSourceIncludeService.class );
    list.add( service );
    ProfileDataSourceIncludeWebserviceImpl profileDataSourceIncludeWebservice =
      new ProfileDataSourceIncludeWebserviceImpl();
    profileDataSourceIncludeWebservice.setIncludeServices( list );
    assertEquals( list, profileDataSourceIncludeWebservice.getIncludeServices() );
  }

  @Test
  public void testGetInclude() {
    List<ProfileDataSourceIncludeService> list = new ArrayList<ProfileDataSourceIncludeService>();
    ProfileDataSourceIncludeService service = mock( ProfileDataSourceIncludeService.class );
    list.add( service );
    ProfileDataSourceIncludeWebserviceImpl profileDataSourceIncludeWebservice =
      new ProfileDataSourceIncludeWebserviceImpl();
    profileDataSourceIncludeWebservice.setIncludeServices( list );
    DataSourceReference dataSourceReference = mock( DataSourceReference.class );
    ProfileDataSourceInclude profileDataSourceInclude = mock( ProfileDataSourceInclude.class );
    when( service.getInclude( dataSourceReference ) ).thenReturn( profileDataSourceInclude );
    assertEquals( profileDataSourceInclude, profileDataSourceIncludeWebservice.getInclude( dataSourceReference ) );
  }

  @Test
  public void testGetIncludeNull() {
    List<ProfileDataSourceIncludeService> list = new ArrayList<ProfileDataSourceIncludeService>();
    ProfileDataSourceIncludeService service = mock( ProfileDataSourceIncludeService.class );
    list.add( service );
    ProfileDataSourceIncludeWebserviceImpl profileDataSourceIncludeWebservice =
      new ProfileDataSourceIncludeWebserviceImpl();
    profileDataSourceIncludeWebservice.setIncludeServices( list );
    DataSourceReference dataSourceReference = mock( DataSourceReference.class );
    assertNull( profileDataSourceIncludeWebservice.getInclude( dataSourceReference ) );
  }

  @Test
  public void testGetIncludeUrl() {
    List<ProfileDataSourceIncludeService> list = new ArrayList<ProfileDataSourceIncludeService>();
    ProfileDataSourceIncludeService service = mock( ProfileDataSourceIncludeService.class );
    list.add( service );
    ProfileDataSourceIncludeWebserviceImpl profileDataSourceIncludeWebservice =
      new ProfileDataSourceIncludeWebserviceImpl();
    profileDataSourceIncludeWebservice.setIncludeServices( list );
    final String id = "id";
    final String provider = "test_provider";
    final ProfileDataSourceInclude profileDataSourceInclude = mock( ProfileDataSourceInclude.class );
    when( service.getInclude( any( DataSourceReference.class ) ) ).thenAnswer( new Answer<ProfileDataSourceInclude>() {
      @Override public ProfileDataSourceInclude answer( InvocationOnMock invocation ) throws Throwable {
        DataSourceReference dataSourceReference = (DataSourceReference) invocation.getArguments()[ 0 ];
        if ( id.equals( dataSourceReference.getId() ) && provider
          .equals( dataSourceReference.getDataSourceProvider() ) ) {
          return profileDataSourceInclude;
        }
        return null;
      }
    } );
    assertEquals( profileDataSourceInclude, profileDataSourceIncludeWebservice.getIncludeUrl( id, provider ) );
  }

  @Test
  public void testGetCreateUrlDsReference() {
    ProfileDataSourceIncludeWebserviceImpl profileDataSourceIncludeWebservice =
      new ProfileDataSourceIncludeWebserviceImpl();
    DataSourceReference dataSourceReference = mock( DataSourceReference.class );
    ProfileDataSourceIncludeService nullService = mock( ProfileDataSourceIncludeService.class );
    ProfileDataSourceIncludeService urlService = mock( ProfileDataSourceIncludeService.class );
    profileDataSourceIncludeWebservice.setIncludeServices( Arrays.asList( nullService, urlService ) );
    when( urlService.getCreateUrl( dataSourceReference ) ).thenReturn( "test-url" );
    assertEquals( "test-url", profileDataSourceIncludeWebservice.getCreateUrl( dataSourceReference ) );
  }

  @Test
  public void testGetCreateUrlStrings() {
    ProfileDataSourceIncludeWebserviceImpl profileDataSourceIncludeWebservice =
      new ProfileDataSourceIncludeWebserviceImpl();
    ProfileDataSourceIncludeService nullService = mock( ProfileDataSourceIncludeService.class );
    ProfileDataSourceIncludeService urlService = mock( ProfileDataSourceIncludeService.class );
    profileDataSourceIncludeWebservice.setIncludeServices( Arrays.asList( nullService, urlService ) );
    when( urlService.getCreateUrl( any( DataSourceReference.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        DataSourceReference reference = (DataSourceReference) invocation.getArguments()[ 0 ];
        if ( "test-id".equals( reference.getId() ) && "test-provider".equals( reference.getDataSourceProvider() ) ) {
          return "test-url";
        }
        return null;
      }
    } );
    assertEquals( "test-url", profileDataSourceIncludeWebservice.getCreateUrl( "test-id", "test-provider" ).getUrl() );
  }
}
