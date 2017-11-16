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
import org.pentaho.profiling.api.configuration.ProfileConfiguration;
import org.pentaho.profiling.api.configuration.core.StreamingProfileMetadata;
import org.pentaho.profiling.api.json.ObjectMapperFactory;
import org.pentaho.profiling.core.integration.tests.utils.DataProfilingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static org.pentaho.profiling.core.integration.tests.utils.DataSourceFieldValueUtils.createDataSourceFieldValues;
import static org.pentaho.profiling.core.integration.tests.utils.DataSourceFieldValueUtils.createRecordList;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Created by bryan on 3/26/15.
 */
public class StreamingProfileTest {
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
  public void testStreaming() throws IOException, InterruptedException {
    String name = "test-name";
    ProfileConfiguration profileConfiguration =
      new ProfileConfiguration( new StreamingProfileMetadata( name ), null, null );
    String id =
      given().contentType( ContentType.JSON ).body( profileConfiguration ).post(
        "/cxf/profile" ).then().contentType( ContentType.JSON ).extract().path( "id" );
    given().contentType( ContentType.JSON )
      .body( createRecordList( createDataSourceFieldValues( "test1", "test2", 1, 3, 5, 7, 9, 11 ) ) )
      .post( "/cxf/streaming/processRecords/" + id ).then().assertThat().statusCode( 204 );
    given().contentType( ContentType.JSON ).put( "/cxf/profile/stop/" + id ).then().assertThat()
      .statusCode( 204 );
    given().contentType( ContentType.JSON ).get( "/cxf/profile/" + id ).then().contentType( ContentType.JSON )
      .body( "fields[0].physicalName", equalTo( "test1" ) )
      .body( "fields[0].logicalName", equalTo( "test2" ) )
      .body( "fields[0].types[0].typeName", equalTo( "java.lang.Integer" ) )
      .body( "fields[0].types[0].count", equalTo( 6 ) )
      .body( "fields[0].types[0].valueTypeMetricsMap.CategoricalMetricContributor.categorical", equalTo( true ) )
      .body( "fields[0].types[0].valueTypeMetricsMap.CategoricalMetricContributor.categories.1", equalTo( 1 ) )
      .body( "fields[0].types[0].valueTypeMetricsMap.CategoricalMetricContributor.categories.3", equalTo( 1 ) )
      .body( "fields[0].types[0].valueTypeMetricsMap.CategoricalMetricContributor.categories.5", equalTo( 1 ) )
      .body( "fields[0].types[0].valueTypeMetricsMap.CategoricalMetricContributor.categories.7", equalTo( 1 ) )
      .body( "fields[0].types[0].valueTypeMetricsMap.CategoricalMetricContributor.categories.9", equalTo( 1 ) )
      .body( "fields[0].types[0].valueTypeMetricsMap.CategoricalMetricContributor.categories.11", equalTo( 1 ) )
      .body( "fields[0].types[0].valueTypeMetricsMap.NumericMetricContributor.max", equalTo( 11.0F ) )
      .body( "fields[0].types[0].valueTypeMetricsMap.NumericMetricContributor.mean", equalTo( 6.0F ) )
      .body( "fields[0].types[0].valueTypeMetricsMap.NumericMetricContributor.min", equalTo( 1.0F ) )
      .body( "fields[0].types[0].valueTypeMetricsMap.NumericMetricContributor.sum", equalTo( 36.0F ) )
      .body( "fields[0].types[0].valueTypeMetricsMap.NumericMetricContributor.sumOfSquares", equalTo( 286.0F ) )
      .body( "fields[0].types[0].valueTypeMetricsMap.NumericMetricContributor.stdDev", equalTo( 3.7416573867739413F ) )
      .body( "fields[0].types[0].valueTypeMetricsMap.NumericMetricContributor.variance", equalTo( 14.0F ) )
      .body( "fields[0].types[0].valueTypeMetricsMap.CardinalityMetricContributor.cardinality", equalTo( 6 ) );
  }
}
