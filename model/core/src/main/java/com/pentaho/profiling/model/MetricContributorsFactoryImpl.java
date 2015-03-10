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

import com.pentaho.profiling.api.metrics.MetricContributor;
import com.pentaho.profiling.api.metrics.MetricContributorService;
import com.pentaho.profiling.api.metrics.MetricContributors;
import com.pentaho.profiling.api.metrics.MetricContributorsFactory;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.univariate.MetricManagerBasedMetricContributor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bryan on 3/13/15.
 */
public class MetricContributorsFactoryImpl implements MetricContributorsFactory {
  private final MetricContributorService metricContributorService;

  public MetricContributorsFactoryImpl( MetricContributorService metricContributorService ) {
    this.metricContributorService = metricContributorService;
  }

  @Override public List<MetricContributor> construct( MetricContributors contributors ) {
    List<MetricContributor> metricContributors = null;
    if ( contributors == null ) {
      contributors = metricContributorService.getDefaultMetricContributors();
    }
    metricContributors = contributors.getMetricContributors();
    if ( metricContributors == null ) {
      metricContributors = new ArrayList<MetricContributor>();
    } else {
      metricContributors = new ArrayList<MetricContributor>( metricContributors );
    }
    List<MetricManagerContributor> metricManagerContributors = contributors.getMetricManagerContributors();
    if ( metricManagerContributors != null ) {
      metricContributors.add( new MetricManagerBasedMetricContributor( metricManagerContributors ) );
    }
    return metricContributors;
  }
}
