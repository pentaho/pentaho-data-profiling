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

package com.pentaho.profiling.services.metrics.bundle;

import com.pentaho.profiling.api.metrics.MetricContributor;
import com.pentaho.profiling.api.metrics.MetricContributors;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.bundle.MetricContributorBundle;
import com.pentaho.profiling.api.metrics.bundle.MetricContributorBundleImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

/**
 * Created by bryan on 3/18/15.
 */
public class MetricContributorsReaderImplTest {
  private List<MetricContributorBundle> metricContributorBundles;
  private MetricContributorsReaderImpl metricContributorsReader;

  @Before
  public void setup() {
    metricContributorBundles = new ArrayList<MetricContributorBundle>();
    metricContributorsReader = new MetricContributorsReaderImpl( metricContributorBundles );
  }

  @Test
  public void testGetClass() {
    MetricContributorBundleImpl metricContributorBundle = new MetricContributorBundleImpl();
    metricContributorBundle.getMetricContributorClasses().add( Integer.class );
    metricContributorBundles.add( metricContributorBundle );
    assertNull( metricContributorsReader.getClass( String.class.getCanonicalName() ) );
    metricContributorBundle = new MetricContributorBundleImpl();
    metricContributorBundle.getMetricContributorClasses().add( String.class );
    metricContributorBundles.add( metricContributorBundle );
    assertEquals( String.class, metricContributorsReader.getClass( String.class.getCanonicalName() ) );
  }

  @Test
  public void testRead() throws IOException {
    // Mocks have extra interfaces only so their classes will be different
    MetricContributor metricContributor1 =
      mock( MetricContributor.class, withSettings().extraInterfaces( Iface1.class ) );
    MetricContributor metricContributor2 =
      mock( MetricContributor.class, withSettings().extraInterfaces( Iface2.class ) );

    MetricManagerContributor metricManagerContributor1 =
      mock( MetricManagerContributor.class, withSettings().extraInterfaces( Iface1.class ) );
    MetricManagerContributor metricManagerContributor2 =
      mock( MetricManagerContributor.class, withSettings().extraInterfaces( Iface2.class ) );

    MetricContributorBundleImpl metricContributorBundle = new MetricContributorBundleImpl();
    metricContributorBundle.getMetricContributorClasses().add( metricContributor1.getClass() );
    metricContributorBundle.getMetricContributorClasses().add( metricManagerContributor1.getClass() );
    metricContributorBundles.add( metricContributorBundle );

    String metricContributorsString =
      "[\"java.util.ArrayList\", [[\"" + metricContributor1.getClass().getCanonicalName() + "\", {}], [\""
        + metricContributor2.getClass().getCanonicalName() + "\", {}]]]";
    String metricManagerContributorsString =
      "[\"java.util.ArrayList\", [[\"" + metricManagerContributor1.getClass().getCanonicalName() + "\", {}], [\""
        + metricManagerContributor2.getClass().getCanonicalName() + "\", {}]]]";
    String json =
      "[\"com.pentaho.profiling.api.metrics.MetricContributors\", {\"metricContributors\":" + metricContributorsString
        + ", \"metricManagerContributors\":" + metricManagerContributorsString + "}]";
    MetricContributors metricContributors =
      metricContributorsReader.read( new ByteArrayInputStream( json.getBytes( Charset.forName( "UTF-8" ) ) ) );
    List<MetricContributor> metricContributorList = metricContributors.getMetricContributors();
    assertEquals( 1, metricContributorList.size() );
    assertEquals( metricContributor1.getClass(), metricContributorList.get( 0 ).getClass() );
    List<MetricManagerContributor> metricManagerContributorList = metricContributors.getMetricManagerContributors();
    assertEquals( 1, metricManagerContributorList.size() );
    assertEquals( metricManagerContributor1.getClass(), metricManagerContributorList.get( 0 ).getClass() );
  }

  private interface Iface1 {
  }

  private interface Iface2 {
  }
}
