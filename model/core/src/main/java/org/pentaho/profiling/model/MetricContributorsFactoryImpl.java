/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
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

package org.pentaho.profiling.model;

import org.pentaho.profiling.api.metrics.MetricContributor;
import org.pentaho.profiling.api.metrics.MetricContributorService;
import org.pentaho.profiling.api.metrics.MetricContributors;
import org.pentaho.profiling.api.metrics.MetricContributorsFactory;
import org.pentaho.profiling.api.metrics.MetricManagerContributor;
import org.pentaho.profiling.api.metrics.univariate.MetricManagerBasedMetricContributor;

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
      contributors =
        metricContributorService.getDefaultMetricContributors( MetricContributorService.DEFAULT_CONFIGURATION );
    }
    metricContributors = contributors.getMetricContributors();
    if ( metricContributors == null ) {
      metricContributors = new ArrayList<MetricContributor>();
    } else {
      metricContributors = new ArrayList<MetricContributor>( metricContributors );
    }
    List<MetricManagerContributor> metricManagerContributors = contributors.getMetricManagerContributors();
    if ( metricManagerContributors != null && metricManagerContributors.size() > 0 ) {
      metricContributors.add( new MetricManagerBasedMetricContributor( metricManagerContributors ) );
    }
    return metricContributors;
  }
}
