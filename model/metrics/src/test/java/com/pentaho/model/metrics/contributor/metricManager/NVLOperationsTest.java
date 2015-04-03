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

package com.pentaho.model.metrics.contributor.metricManager;

import com.pentaho.model.metrics.contributor.metricManager.impl.CardinalityMetricContributor;
import com.pentaho.model.metrics.contributor.metricManager.impl.CategoricalMetricContributor;
import com.pentaho.model.metrics.contributor.metricManager.impl.PercentileMetricContributor;
import com.pentaho.model.metrics.contributor.metricManager.impl.RegexAddressMetricContributor;
import com.pentaho.profiling.api.ProfileCreateRequest;
import com.pentaho.profiling.api.datasource.DataSourceReference;
import com.pentaho.profiling.api.json.ObjectMapperFactory;
import com.pentaho.profiling.api.metrics.MetricContributor;
import com.pentaho.profiling.api.metrics.MetricContributors;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by bryan on 2/5/15.
 */
public class NVLOperationsTest {
  @Test
  public void testConstructor() {
    // Required for code coverage, class is only used in static context
    NVLOperations nvlOperations = new NVLOperations();
  }

  @Test
  public void testOUt() throws IOException {
    ObjectMapper objectMapper = new ObjectMapperFactory( NVLOperationsTest.class.getClassLoader() ).createMapper();
    System.out.println( objectMapper.writeValueAsString( new ProfileCreateRequest(
      new DataSourceReference( "test", "com.pentaho.profiling.mongo.api.MongoProfilingConnectionMetadataService" ), new MetricContributors(
      new ArrayList<MetricContributor>( Arrays.<MetricContributor>asList() ),
      new ArrayList<MetricManagerContributor>(
        Arrays.<MetricManagerContributor>asList( new CategoricalMetricContributor(), new CardinalityMetricContributor(),
          new RegexAddressMetricContributor(), new PercentileMetricContributor() ) ) ) ) ) );
  }
}
