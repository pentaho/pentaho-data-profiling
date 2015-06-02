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

package org.pentaho.profiling.api.configuration;

import org.pentaho.profiling.api.metrics.MetricContributors;

/**
 * Created by bryan on 4/10/15.
 */
public class ProfileConfiguration {
  private DataSourceMetadata dataSourceMetadata;
  private String configName;
  private MetricContributors metricContributors;

  public ProfileConfiguration() {
    this( null, null, null );
  }

  public ProfileConfiguration( DataSourceMetadata dataSourceMetadata,
                               String configName, MetricContributors metricContributors ) {
    this.dataSourceMetadata = dataSourceMetadata;
    this.configName = configName;
    this.metricContributors = metricContributors;
  }

  public DataSourceMetadata getDataSourceMetadata() {
    return dataSourceMetadata;
  }

  public void setDataSourceMetadata( DataSourceMetadata dataSourceMetadata ) {
    this.dataSourceMetadata = dataSourceMetadata;
  }

  public MetricContributors getMetricContributors() {
    return metricContributors;
  }

  public void setMetricContributors( MetricContributors metricContributors ) {
    this.metricContributors = metricContributors;
  }

  public String getConfigName() {
    return configName;
  }

  public void setConfigName( String configName ) {
    this.configName = configName;
  }

  //OperatorWrap isn't helpful for autogenerated methods
  //CHECKSTYLE:OperatorWrap:OFF

  @Override public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    ProfileConfiguration that = (ProfileConfiguration) o;

    if ( dataSourceMetadata != null ? !dataSourceMetadata.equals( that.dataSourceMetadata ) :
      that.dataSourceMetadata != null ) {
      return false;
    }
    if ( configName != null ? !configName.equals( that.configName ) : that.configName != null ) {
      return false;
    }
    return !( metricContributors != null ? !metricContributors.equals( that.metricContributors ) :
      that.metricContributors != null );

  }

  @Override public int hashCode() {
    int result = dataSourceMetadata != null ? dataSourceMetadata.hashCode() : 0;
    result = 31 * result + ( configName != null ? configName.hashCode() : 0 );
    result = 31 * result + ( metricContributors != null ? metricContributors.hashCode() : 0 );
    return result;
  }

  @Override public String toString() {
    return "ProfileConfiguration{" +
      "dataSourceMetadata=" + dataSourceMetadata +
      ", configName='" + configName + '\'' +
      ", metricContributors=" + metricContributors +
      '}';
  }
  //CHECKSTYLE:OperatorWrap:ON
}
