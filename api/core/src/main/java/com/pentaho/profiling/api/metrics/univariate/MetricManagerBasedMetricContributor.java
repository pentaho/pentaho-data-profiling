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

package org.pentaho.profiling.api.metrics.univariate;

import org.pentaho.profiling.api.MutableProfileField;
import org.pentaho.profiling.api.MutableProfileFieldValueType;
import org.pentaho.profiling.api.MutableProfileStatus;
import org.pentaho.profiling.api.ProfileField;
import org.pentaho.profiling.api.ProfileFieldProperty;
import org.pentaho.profiling.api.ProfileFieldValueType;
import org.pentaho.profiling.api.ProfileStatus;
import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.metrics.MetricContributor;
import org.pentaho.profiling.api.metrics.MetricManagerContributor;
import org.pentaho.profiling.api.metrics.MetricMergeException;
import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.pentaho.profiling.api.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bryan on 2/3/15.
 */
public class MetricManagerBasedMetricContributor implements MetricContributor {
  private static final Logger LOGGER = LoggerFactory.getLogger( MetricManagerBasedMetricContributor.class );
  private final List<MetricManagerContributor> metricManagerContributors;
  private final Map<String, List<MetricManagerContributor>> metricManagerContributorMap;

  public MetricManagerBasedMetricContributor( List<MetricManagerContributor> metricManagerContributors ) {
    this.metricManagerContributors = new ArrayList<MetricManagerContributor>( metricManagerContributors );
    this.metricManagerContributorMap = new HashMap<String, List<MetricManagerContributor>>();
    for ( MetricManagerContributor metricManagerContributor : this.metricManagerContributors ) {
      for ( String typeName : metricManagerContributor.supportedTypes() ) {
        List<MetricManagerContributor> metricManagerContributorsForType = metricManagerContributorMap.get( typeName );
        if ( metricManagerContributorsForType == null ) {
          metricManagerContributorsForType = new ArrayList<MetricManagerContributor>();
          metricManagerContributorMap.put( typeName, metricManagerContributorsForType );
        }
        metricManagerContributorsForType.add( metricManagerContributor );
      }
    }
  }

  @Override public void processFields( MutableProfileStatus mutableProfileStatus, List<DataSourceFieldValue> value )
    throws ProfileActionException {
    Map<String, List<Pair<MutableProfileField, DataSourceFieldValue>>> valuesByType =
      new HashMap<String, List<Pair<MutableProfileField, DataSourceFieldValue>>>();
    for ( DataSourceFieldValue dataSourceFieldValue : value ) {
      MutableProfileField mutableProfileField = mutableProfileStatus
        .getOrCreateField( dataSourceFieldValue.getPhysicalName(), dataSourceFieldValue.getLogicalName() );
      String typeName = dataSourceFieldValue.getFieldTypeName();
      List<Pair<MutableProfileField, DataSourceFieldValue>> values = valuesByType.get( typeName );
      if ( values == null ) {
        values = new ArrayList<Pair<MutableProfileField, DataSourceFieldValue>>();
        valuesByType.put( typeName, values );
      }
      values.add( Pair.of( mutableProfileField, dataSourceFieldValue ) );
    }
    for ( Map.Entry<String, List<Pair<MutableProfileField, DataSourceFieldValue>>> entry : valuesByType.entrySet() ) {
      String typeName = entry.getKey();
      List<MetricManagerContributor> metricManagerContributors = metricManagerContributorMap.get( typeName );
      if ( metricManagerContributors != null ) {
        for ( Pair<MutableProfileField, DataSourceFieldValue> dataSourceFieldAndValue : entry.getValue() ) {
          MutableProfileFieldValueType valueTypeMetrics =
            dataSourceFieldAndValue.getFirst().getValueTypeMetrics( typeName );
          if ( valueTypeMetrics != null ) {
            for ( MetricManagerContributor metricManagerContributor : metricManagerContributors ) {
              metricManagerContributor.process( valueTypeMetrics, dataSourceFieldAndValue.getSecond() );
            }
          }
        }
      }
    }
  }

  @Override public void setDerived( MutableProfileStatus mutableProfileStatus ) throws ProfileActionException {
    for ( Map.Entry<String, List<MetricManagerContributor>> metricManagerContributorsForType
      : metricManagerContributorMap.entrySet() ) {
      for ( MutableProfileField mutableProfileField : mutableProfileStatus
        .getMutableFieldMap().values() ) {
        MutableProfileFieldValueType valueTypeMetrics =
          mutableProfileField.getValueTypeMetrics( metricManagerContributorsForType.getKey() );
        if ( valueTypeMetrics != null ) {
          for ( MetricManagerContributor metricManagerContributor : metricManagerContributorsForType.getValue() ) {
            metricManagerContributor.setDerived( valueTypeMetrics );
          }
        }
      }
    }
  }

  @Override public void merge( MutableProfileStatus into, ProfileStatus from )
    throws MetricMergeException {
    Set<String> processedNames = new HashSet<String>();
    for ( MutableProfileField mutableProfileField : into.getMutableFieldMap().values() ) {
      String physicalName = mutableProfileField.getPhysicalName();
      processedNames.add( physicalName );
      ProfileField secondDataSourceField = from.getField( physicalName );
      if ( secondDataSourceField != null ) {
        for ( MetricManagerContributor metricManagerContributor : metricManagerContributors ) {
          for ( String typeName : metricManagerContributor.supportedTypes() ) {
            ProfileFieldValueType secondMetricManager = secondDataSourceField.getType( typeName );
            if ( secondMetricManager != null ) {
              MutableProfileFieldValueType firstMetricManager = mutableProfileField.getValueTypeMetrics( typeName );
              if ( firstMetricManager != null ) {
                metricManagerContributor.merge( firstMetricManager, secondMetricManager );
              } else {
                mutableProfileField.putValueTypeMetrics( typeName, secondMetricManager );
              }
            }
          }
        }
      }
    }
    for ( ProfileField profileField : from.getFields() ) {
      if ( !processedNames.contains( profileField.getPhysicalName() ) ) {
        into.addField( profileField );
      }
    }
  }

  @Override public List<ProfileFieldProperty> getProfileFieldProperties() {
    Set<ProfileFieldProperty> addedProperties = new HashSet<ProfileFieldProperty>();
    List<ProfileFieldProperty> result = new ArrayList<ProfileFieldProperty>();
    for ( MetricManagerContributor metricManagerContributor : metricManagerContributors ) {
      for ( ProfileFieldProperty profileFieldProperty : metricManagerContributor.profileFieldProperties() ) {
        if ( addedProperties.add( profileFieldProperty ) ) {
          result.add( profileFieldProperty );
        }
      }
    }
    return result;
  }
}
