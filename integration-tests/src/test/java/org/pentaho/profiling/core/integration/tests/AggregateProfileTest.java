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

package org.pentaho.profiling.core.integration.tests;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.mapper.ObjectMapperDeserializationContext;
import com.jayway.restassured.mapper.ObjectMapperSerializationContext;
import org.pentaho.model.metrics.contributor.metricManager.impl.NumericMetricContributor;
import org.pentaho.profiling.api.ProfileStatus;
import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.configuration.ProfileConfiguration;
import org.pentaho.profiling.api.configuration.core.AggregateProfileMetadata;
import org.pentaho.profiling.api.configuration.core.StreamingProfileMetadata;
import org.pentaho.profiling.api.dto.ProfileStatusDTO;
import org.pentaho.profiling.api.json.ObjectMapperFactory;
import org.pentaho.profiling.api.metrics.MetricContributor;
import org.pentaho.profiling.api.metrics.MetricContributors;
import org.pentaho.profiling.api.metrics.MetricManagerContributor;
import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.pentaho.profiling.api.metrics.univariate.MetricManagerBasedMetricContributor;
import org.pentaho.profiling.model.ProfileStatusImpl;
import org.pentaho.profiling.core.integration.tests.utils.DataProfilingService;
import org.pentaho.profiling.core.integration.tests.utils.ProfileStatusValidationUtil;
import org.pentaho.profiling.services.AggregateAddChildWrapper;
import org.pentaho.profiling.services.AggregateProfileDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jayway.restassured.RestAssured.given;
import static org.pentaho.profiling.core.integration.tests.utils.DataSourceFieldValueUtils.createDataSourceFieldValues;
import static org.pentaho.profiling.core.integration.tests.utils.DataSourceFieldValueUtils.createRecordList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by bryan on 3/27/15.
 */
public class AggregateProfileTest {
  private static DataProfilingService dataProfilingService;
  private static int origPort;

  @BeforeClass
  public static void startup() throws Exception {
    dataProfilingService = new DataProfilingService();
    dataProfilingService.start();
    origPort = RestAssured.port;
    RestAssured.port = 8181;
    // Configure object mapper
    final ObjectMapper typeObjectMapper =
      new ObjectMapperFactory().createMapper();
    RestAssured.objectMapper( new com.jayway.restassured.mapper.ObjectMapper() {
      @Override public Object deserialize( ObjectMapperDeserializationContext context ) {
        try {
          return typeObjectMapper.readValue( context.getDataToDeserialize().asByteArray(), context.getType() );
        } catch ( IOException e ) {
          e.printStackTrace();
          return null;
        }
      }

      @Override public Object serialize( ObjectMapperSerializationContext context ) {
        try {
          return typeObjectMapper.writeValueAsString( context.getObjectToSerialize() );
        } catch ( IOException e ) {
          e.printStackTrace();
          return null;
        }
      }
    } );
  }

  @AfterClass
  public static void shutdown() throws IOException {
    dataProfilingService.stop();
    RestAssured.port = origPort;
  }

  @Test
  public void testAggregate() throws IOException, InterruptedException, ProfileActionException {
    // Configure metric contributors
    List<MetricContributor> metricContributorList = new ArrayList<MetricContributor>();
    List<MetricManagerContributor> metricManagerContributorList = new ArrayList<MetricManagerContributor>();
    metricManagerContributorList.add( new NumericMetricContributor() );
    metricContributorList.add( new MetricManagerBasedMetricContributor( metricManagerContributorList ) );
    MetricContributors metricContributors = new MetricContributors();
    metricContributors.setMetricManagerContributors( metricManagerContributorList );

    // Create aggregate profiles
    ProfileConfiguration profileCreateRequest = new ProfileConfiguration();
    String topAggregateName = "test-top-aggregate";
    profileCreateRequest.setDataSourceMetadata( new AggregateProfileMetadata( topAggregateName ) );
    profileCreateRequest.setMetricContributors( metricContributors );
    String topAggregateId =
      given().contentType( ContentType.JSON ).body( profileCreateRequest ).post(
        "/cxf/profile" ).then().contentType( ContentType.JSON ).extract().path( "id" );
    String subAggregateName = "test-sub-aggregate";
    profileCreateRequest.setDataSourceMetadata( new AggregateProfileMetadata( subAggregateName ) );
    String subAggregateId =
      given().contentType( ContentType.JSON ).body( profileCreateRequest ).post(
        "/cxf/profile" ).then().contentType( ContentType.JSON ).extract().path( "id" );

    // Create streaming profiles
    profileCreateRequest = new ProfileConfiguration();
    profileCreateRequest.setMetricContributors( metricContributors );
    profileCreateRequest.setDataSourceMetadata( new StreamingProfileMetadata( "stream1" ) );
    String stream1Id =
      given().contentType( ContentType.JSON ).body( profileCreateRequest ).post(
        "/cxf/profile" ).then().contentType( ContentType.JSON ).extract().path( "id" );
    profileCreateRequest.setDataSourceMetadata( new StreamingProfileMetadata( "stream2" ) );
    String stream2Id =
      given().contentType( ContentType.JSON ).body( profileCreateRequest ).post(
        "/cxf/profile" ).then().contentType( ContentType.JSON ).extract().path( "id" );
    profileCreateRequest.setDataSourceMetadata( new StreamingProfileMetadata( "stream3" ) );
    String stream3Id =
      given().contentType( ContentType.JSON ).body( profileCreateRequest ).post(
        "/cxf/profile" ).then().contentType( ContentType.JSON ).extract().path( "id" );
    profileCreateRequest.setDataSourceMetadata( new StreamingProfileMetadata( "stream4" ) );
    String stream4Id =
      given().contentType( ContentType.JSON ).body( profileCreateRequest ).post(
        "/cxf/profile" ).then().contentType( ContentType.JSON ).extract().path( "id" );
    profileCreateRequest.setDataSourceMetadata( new StreamingProfileMetadata( "stream5" ) );
    String stream5Id =
      given().contentType( ContentType.JSON ).body( profileCreateRequest ).post(
        "/cxf/profile" ).then().contentType( ContentType.JSON ).extract().path( "id" );

    // Add subAggregate to top level aggregate
    given().contentType( ContentType.JSON ).body( new AggregateAddChildWrapper( topAggregateId, subAggregateId ) ).post(
      "/cxf/aggregate/add" ).then().assertThat().statusCode( 204 );

    // Add stream 1 and 2 to top level aggregate
    given().contentType( ContentType.JSON ).body( new AggregateAddChildWrapper( topAggregateId, stream1Id ) ).post(
      "/cxf/aggregate/add" ).then().assertThat().statusCode( 204 );
    given().contentType( ContentType.JSON ).body( new AggregateAddChildWrapper( topAggregateId, stream2Id ) ).post(
      "/cxf/aggregate/add" ).then().assertThat().statusCode( 204 );

    // Add stream 3 and 4 to sub aggregate (stream 5 will be an orphan)
    given().contentType( ContentType.JSON ).body( new AggregateAddChildWrapper( subAggregateId, stream3Id ) ).post(
      "/cxf/aggregate/add" ).then().assertThat().statusCode( 204 );
    given().contentType( ContentType.JSON ).body( new AggregateAddChildWrapper( subAggregateId, stream4Id ) ).post(
      "/cxf/aggregate/add" ).then().assertThat().statusCode( 204 );

    // Check aggregate list, we expect top, sub aggregate, and they should have their children
    List<AggregateProfileDTO> aggregates =
      given().contentType( ContentType.JSON ).get( "/cxf/aggregate" ).then().contentType( ContentType.JSON ).extract()
        .as( List.class );
    Set<String> aggregateIds = new HashSet<String>();
    for ( AggregateProfileDTO aggregate : aggregates ) {
      String id = aggregate.getId();
      aggregateIds.add( id );
      List<AggregateProfileDTO> aggregateChildren = aggregate.getChildProfiles();
      Set<String> children = new HashSet<String>( aggregateChildren.size() );
      for ( AggregateProfileDTO aggregateChild : aggregateChildren ) {
        children.add( aggregateChild.getId() );
      }
      if ( topAggregateId.equals( id ) ) {
        assertEquals( topAggregateName, aggregate.getName() );
        assertTrue( children.contains( subAggregateId ) );
        assertTrue( children.contains( stream1Id ) );
        assertTrue( children.contains( stream2Id ) );
        assertFalse( children.contains( stream3Id ) );
        assertFalse( children.contains( stream4Id ) );
        assertFalse( children.contains( stream5Id ) );
      } else if ( subAggregateId.equals( id ) ) {
        assertEquals( subAggregateName, aggregate.getName() );
        assertFalse( children.contains( stream1Id ) );
        assertFalse( children.contains( stream2Id ) );
        assertTrue( children.contains( stream3Id ) );
        assertTrue( children.contains( stream4Id ) );
        assertFalse( children.contains( stream5Id ) );
      }
    }
    assertTrue( aggregateIds.contains( topAggregateId ) );
    assertFalse( aggregateIds.contains( subAggregateId ) );
    assertFalse( aggregateIds.contains( stream1Id ) );
    assertFalse( aggregateIds.contains( stream2Id ) );
    assertFalse( aggregateIds.contains( stream3Id ) );
    assertFalse( aggregateIds.contains( stream4Id ) );
    assertFalse( aggregateIds.contains( stream5Id ) );

    // Ensure that the aggregate returned for any children is that of the top level
    given().contentType( ContentType.JSON ).get( "/cxf/aggregate/" + topAggregateId ).then()
      .contentType( ContentType.JSON )
      .body( "id", equalTo( topAggregateId ) );
    given().contentType( ContentType.JSON ).get( "/cxf/aggregate/" + subAggregateId ).then()
      .contentType( ContentType.JSON )
      .body( "id", equalTo( topAggregateId ) );
    given().contentType( ContentType.JSON ).get( "/cxf/aggregate/" + stream1Id ).then().contentType( ContentType.JSON )
      .body( "id", equalTo( topAggregateId ) );
    given().contentType( ContentType.JSON ).get( "/cxf/aggregate/" + stream2Id ).then().contentType( ContentType.JSON )
      .body( "id", equalTo( topAggregateId ) );
    given().contentType( ContentType.JSON ).get( "/cxf/aggregate/" + stream3Id ).then().contentType( ContentType.JSON )
      .body( "id", equalTo( topAggregateId ) );
    given().contentType( ContentType.JSON ).get( "/cxf/aggregate/" + stream4Id ).then().contentType( ContentType.JSON )
      .body( "id", equalTo( topAggregateId ) );

    // Stream 5 should have no content for aggregate profile
    given().contentType( ContentType.JSON ).get( "/cxf/aggregate/" + stream5Id ).then().assertThat().statusCode( 204 );

    // Send data into the streams
    String numberPhysicalName = "test1";
    String numberLogicalName = "test2";
    List<List<DataSourceFieldValue>> stream1Records =
      createRecordList( createDataSourceFieldValues( numberPhysicalName, numberLogicalName, 1, 3, 5, 7, 9, 11 ) );
    given().contentType( ContentType.JSON ).body( stream1Records ).post( "/cxf/streaming/processRecords/" + stream1Id )
      .then().assertThat().statusCode( 204 );
    given().contentType( ContentType.JSON ).put( "/cxf/profile/stop/" + stream1Id ).then().assertThat()
      .statusCode( 204 );
    List<List<DataSourceFieldValue>> stream2Records =
      createRecordList( createDataSourceFieldValues( numberPhysicalName, numberLogicalName, 2, 4, 6, 8, 10, 12 ) );
    given().contentType( ContentType.JSON ).body( stream2Records ).post( "/cxf/streaming/processRecords/" + stream2Id )
      .then().assertThat().statusCode( 204 );
    given().contentType( ContentType.JSON ).put( "/cxf/profile/stop/" + stream2Id ).then().assertThat().statusCode(
      204 );
    List<List<DataSourceFieldValue>> stream3Records =
      createRecordList( createDataSourceFieldValues( numberPhysicalName, numberLogicalName, 13, 14 ) );
    given().contentType( ContentType.JSON ).body( stream3Records ).post( "/cxf/streaming/processRecords/" + stream3Id )
      .then().assertThat().statusCode( 204 );
    given().contentType( ContentType.JSON ).put( "/cxf/profile/stop/" + stream3Id ).then().assertThat().statusCode(
      204 );
    List<List<DataSourceFieldValue>> stream4Records =
      createRecordList( createDataSourceFieldValues( numberPhysicalName, numberLogicalName, 15, 16 ) );
    given().contentType( ContentType.JSON ).body( stream4Records ).post( "/cxf/streaming/processRecords/" + stream4Id )
      .then().assertThat().statusCode( 204 );
    given().contentType( ContentType.JSON ).put( "/cxf/profile/stop/" + stream4Id ).then().assertThat().statusCode(
      204 );

    // Stream 5 shouldn't affect our aggregate results
    List<List<DataSourceFieldValue>> stream5Records =
      createRecordList( createDataSourceFieldValues( numberPhysicalName, numberLogicalName, 1516 ) );
    given().contentType( ContentType.JSON ).body( stream5Records ).post( "/cxf/streaming/processRecords/" + stream5Id )
      .then().assertThat().statusCode( 204 );
    given().contentType( ContentType.JSON ).put( "/cxf/profile/stop/" + stream5Id ).then().assertThat()
      .statusCode( 204 );

    ProfileStatus stream1Status = new ProfileStatusImpl(
      given().contentType( ContentType.JSON ).get( "/cxf/profile/" + stream1Id ).then().contentType( ContentType.JSON )
        .extract().as( ProfileStatusDTO.class ) );
    ProfileStatus stream2Status = new ProfileStatusImpl(
      given().contentType( ContentType.JSON ).get( "/cxf/profile/" + stream2Id ).then().contentType( ContentType.JSON )
        .extract().as( ProfileStatusDTO.class ) );
    ProfileStatus stream3Status = new ProfileStatusImpl(
      given().contentType( ContentType.JSON ).get( "/cxf/profile/" + stream3Id ).then().contentType( ContentType.JSON )
        .extract().as( ProfileStatusDTO.class ) );
    ProfileStatus stream4Status = new ProfileStatusImpl(
      given().contentType( ContentType.JSON ).get( "/cxf/profile/" + stream4Id ).then().contentType( ContentType.JSON )
        .extract().as( ProfileStatusDTO.class ) );
    ProfileStatus stream5Status = new ProfileStatusImpl(
      given().contentType( ContentType.JSON ).get( "/cxf/profile/" + stream5Id ).then().contentType( ContentType.JSON )
        .extract().as( ProfileStatusDTO.class ) );

    ProfileStatusValidationUtil
      .validateProfileFieldsAgainstRecords( stream1Status, metricContributorList, stream1Records );
    ProfileStatusValidationUtil
      .validateProfileFieldsAgainstRecords( stream2Status, metricContributorList, stream2Records );
    ProfileStatusValidationUtil
      .validateProfileFieldsAgainstRecords( stream3Status, metricContributorList, stream3Records );
    ProfileStatusValidationUtil
      .validateProfileFieldsAgainstRecords( stream4Status, metricContributorList, stream4Records );
    ProfileStatusValidationUtil
      .validateProfileFieldsAgainstRecords( stream5Status, metricContributorList, stream5Records );

    Thread.sleep( 1100 );
    List<List<DataSourceFieldValue>> subAggregateRecords = new ArrayList<List<DataSourceFieldValue>>( stream3Records );
    subAggregateRecords.addAll( stream4Records );

    ProfileStatus subAggregateStatus = new ProfileStatusImpl(
      given().contentType( ContentType.JSON ).get( "/cxf/profile/" + subAggregateId ).then()
        .contentType( ContentType.JSON )
        .extract().as( ProfileStatusDTO.class ) );
    ProfileStatusValidationUtil
      .validateProfileFieldsAgainstRecords( subAggregateStatus, metricContributorList, subAggregateRecords );

    Thread.sleep( 1100 );
    List<List<DataSourceFieldValue>> topAggregateRecords = new ArrayList<List<DataSourceFieldValue>>( stream1Records );
    topAggregateRecords.addAll( stream2Records );
    topAggregateRecords.addAll( subAggregateRecords );
    ProfileStatus topAggregateStatus = new ProfileStatusImpl(
      given().contentType( ContentType.JSON ).get( "/cxf/profile/" + topAggregateId ).then()
        .contentType( ContentType.JSON )
        .extract().as( ProfileStatusDTO.class ) );
    ProfileStatusValidationUtil
      .validateProfileFieldsAgainstRecords( topAggregateStatus, metricContributorList, topAggregateRecords );

  }
}
