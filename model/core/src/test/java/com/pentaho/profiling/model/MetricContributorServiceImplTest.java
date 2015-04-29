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

import com.pentaho.profiling.api.MutableProfileFieldValueType;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileFieldValueType;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.json.ObjectMapperFactory;
import com.pentaho.profiling.api.metrics.MetricContributor;
import com.pentaho.profiling.api.metrics.MetricContributors;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.bundle.MetricContributorBundle;
import com.pentaho.profiling.api.metrics.bundle.MetricContributorBundleImpl;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

/**
 * Created by bryan on 4/24/15.
 */
public class MetricContributorServiceImplTest {
  public static final String KARAF_HOME = "src/test/resources";
  private List<MetricContributorBundle> metricContributorBundles;
  private ObjectMapperFactory objectMapperFactory;
  private String jsonFile;
  private MetricContributorServiceImpl metricContributorService;
  private Path karafHomePath;
  private Path jsonPath;

  @Before
  public void setup() {
    metricContributorBundles = new ArrayList<MetricContributorBundle>();
    objectMapperFactory = mock( ObjectMapperFactory.class );
    jsonFile = "testMetricContributors.json";
    karafHomePath = FileSystems.getDefault().getPath( KARAF_HOME ).normalize().toAbsolutePath();
    metricContributorService =
      new MetricContributorServiceImpl( metricContributorBundles, objectMapperFactory,
        karafHomePath.normalize().toFile().getAbsolutePath() );
    jsonPath = karafHomePath.resolve( MetricContributorServiceImpl.ETC_METRIC_CONTRIBUTORS_JSON );
  }

  @After
  public void tearDown() {
    File file = jsonPath.toFile();
    if ( file.exists() ) {
      file.delete();
    }
  }

  @Test
  public void testTwoArgConstructor() {
    assertNull(
      new MetricContributorServiceImpl( metricContributorBundles, objectMapperFactory, null ).getJsonFile() );
    assertEquals( jsonPath, FileSystems.getDefault().getPath(
      new MetricContributorServiceImpl( metricContributorBundles, objectMapperFactory, KARAF_HOME ).getJsonFile() )
      .normalize().toAbsolutePath() );
  }

  @Test
  public void testSetDefault() {
    MetricContributors metricContributors = new MetricContributors();
    String configuration = "new";
    metricContributorService.setDefaultMetricContributors( configuration, metricContributors );
    assertEquals( metricContributors, metricContributorService.getDefaultMetricContributors( configuration ) );
  }

  @Test
  public void testGetFull() {
    MetricContributorBundleImpl metricContributorBundle = new MetricContributorBundleImpl();
    metricContributorBundle.setClasses( new ArrayList<Class>( Arrays.asList( TestMetricContributor.class ) ) );
    metricContributorService =
      new MetricContributorServiceImpl( Arrays.<MetricContributorBundle>asList( metricContributorBundle ),
        objectMapperFactory, karafHomePath.normalize().toFile().getAbsolutePath() );
    assertEquals( new MetricContributors( new ArrayList<MetricContributor>(), new ArrayList
        <MetricManagerContributor>( Arrays.asList( new TestMetricContributor() ) ) ),
      metricContributorService.getDefaultMetricContributors( "full" ) );
  }

  public static class TestMetricContributor implements MetricManagerContributor {
    private String param1 = "initialValue";
    private String name;

    public String getParam1() {
      return param1;
    }

    public void setParam1( String param1 ) {
      this.param1 = param1;
    }

    @Override public String getName() {
      return name;
    }

    @Override public void setName( String name ) {
      this.name = name;
    }

    @Override public Set<String> supportedTypes() {
      return new HashSet<String>();
    }

    @Override
    public void process( MutableProfileFieldValueType dataSourceMetricManager,
                         DataSourceFieldValue dataSourceFieldValue )
      throws ProfileActionException {

    }

    @Override public void merge( MutableProfileFieldValueType into, ProfileFieldValueType from )
      throws MetricMergeException {

    }

    @Override public void setDerived( MutableProfileFieldValueType dataSourceMetricManager )
      throws ProfileActionException {

    }

    @Override public List<ProfileFieldProperty> profileFieldProperties() {
      return null;
    }

    @Override public boolean equals( Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      TestMetricContributor that = (TestMetricContributor) o;

      if ( param1 != null ? !param1.equals( that.param1 ) : that.param1 != null ) {
        return false;
      }
      return !( name != null ? !name.equals( that.name ) : that.name != null );

    }

    @Override public int hashCode() {
      int result = param1 != null ? param1.hashCode() : 0;
      result = 31 * result + ( name != null ? name.hashCode() : 0 );
      return result;
    }
  }
}
