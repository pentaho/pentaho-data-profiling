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
import org.pentaho.model.metrics.contributor.metricManager.impl.CardinalityMetricContributor;
import org.pentaho.model.metrics.contributor.metricManager.impl.CategoricalMetricContributor;
import org.pentaho.model.metrics.contributor.metricManager.impl.DateMetricContributor;
import org.pentaho.model.metrics.contributor.metricManager.impl.NumericMetricContributor;
import org.pentaho.model.metrics.contributor.metricManager.impl.RegexAddressMetricContributor;
import org.pentaho.model.metrics.contributor.metricManager.impl.StringLengthMetricContributor;
import org.pentaho.model.metrics.contributor.metricManager.impl.WordCountMetricContributor;
import org.pentaho.profiling.api.json.ObjectMapperFactory;
import org.pentaho.profiling.api.metrics.MetricContributor;
import org.pentaho.profiling.api.metrics.MetricContributors;
import org.pentaho.profiling.api.metrics.MetricManagerContributor;
import org.pentaho.profiling.model.metrics.contributor.percentile.PercentileMetricContributor;
import org.pentaho.profiling.core.integration.tests.utils.DataProfilingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

/**
 * Created by bryan on 4/14/15.
 */
public class MetricContributorsTest {
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
          return typeObjectMapper.readValue( context.getDataToDeserialize().asByteArray(), Object.class );
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
  public void testMetricContributorsService() {
    // Test that full has 1 of each metric contributor
    String fullMCPath = "/cxf/metrics/full";
    MetricContributors fullMetricContributors =
      given().contentType( ContentType.JSON ).get( fullMCPath ).then().contentType( ContentType.JSON )
        .extract().as(
        MetricContributors.class );
    Set<Class> metricContributorClasses = new HashSet<Class>();
    for ( MetricContributor metricContributor : fullMetricContributors.getMetricContributors() ) {
      metricContributorClasses.remove( metricContributor.getClass() );
    }
    assertEquals( 0, metricContributorClasses.size() );
    Set<Class> metricManagerContributorClasses = new HashSet<Class>( Arrays.asList( CardinalityMetricContributor.class,
      CategoricalMetricContributor.class, DateMetricContributor.class, NumericMetricContributor.class,
      PercentileMetricContributor.class, RegexAddressMetricContributor.class, StringLengthMetricContributor.class,
      WordCountMetricContributor.class ) );
    for ( MetricManagerContributor metricManagerContributor : fullMetricContributors.getMetricManagerContributors() ) {
      metricManagerContributorClasses.remove( metricManagerContributor.getClass() );
    }
    assertEquals( 0, metricManagerContributorClasses.size() );
    // Default and full should start out the same
    String defaultMCPath = "/cxf/metrics/default";
    MetricContributors defaultMetricContributors =
      given().contentType( ContentType.JSON ).get( defaultMCPath ).then().contentType( ContentType.JSON )
        .extract().as(
        MetricContributors.class );
    assertEquals( fullMetricContributors, defaultMetricContributors );

    // Test that we can set and get a custom configuration
    RegexAddressMetricContributor startsWithAMetricContributor = new RegexAddressMetricContributor();
    startsWithAMetricContributor.setRegex( "^A.*" );
    startsWithAMetricContributor.setName( "StartsWithA" );
    startsWithAMetricContributor
      .setSupportedTypes( new HashSet<String>( Arrays.asList( String.class.getCanonicalName() ) ) );
    startsWithAMetricContributor.setNamePath( "test-path" );
    startsWithAMetricContributor.setNameKey( "test-key" );
    MetricContributors testMetricContributors =
      new MetricContributors( new ArrayList<MetricContributor>(), new ArrayList
        <MetricManagerContributor>( Arrays.asList( startsWithAMetricContributor ) ) );
    String testMCPath = "/cxf/metrics/test";
    given().contentType( ContentType.JSON ).body( testMetricContributors ).post(
      testMCPath ).then().assertThat().statusCode( 204 );
    MetricContributors testRoundTripContributors =
      given().contentType( ContentType.JSON ).get( testMCPath ).then().contentType( ContentType.JSON )
        .extract().as(
        MetricContributors.class );
    assertEquals( testMetricContributors, testRoundTripContributors );

    // Should be able to set the default
    given().contentType( ContentType.JSON ).body( testMetricContributors ).post(
      defaultMCPath ).then().assertThat().statusCode( 204 );
    defaultMetricContributors =
      given().contentType( ContentType.JSON ).get( defaultMCPath ).then().contentType( ContentType.JSON )
        .extract().as(
        MetricContributors.class );
    assertEquals( testMetricContributors, defaultMetricContributors );

    // Should not be able to set full
    given().contentType( ContentType.JSON ).body( testMetricContributors ).post(
      fullMCPath ).then().assertThat().statusCode( 204 );
    MetricContributors fullMetricContributorsRoundTrip =
      given().contentType( ContentType.JSON ).get( fullMCPath ).then().contentType( ContentType.JSON )
        .extract().as(
        MetricContributors.class );
    assertEquals( fullMetricContributors, fullMetricContributorsRoundTrip );
  }
}
