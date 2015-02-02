/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
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

package com.pentaho.model.metrics.contributor.impl;

import com.pentaho.metrics.api.MetricContributorUtils;
import com.pentaho.metrics.api.field.DataSourceField;
import com.pentaho.metrics.api.field.DataSourceFieldManager;
import com.pentaho.metrics.api.field.DataSourceFieldValue;
import com.pentaho.metrics.api.field.DataSourceMetricManager;
import com.pentaho.model.metrics.contributor.AbstractMetricContributor;
import com.pentaho.profiling.api.MessageUtils;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileStatusMessage;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.stats.Statistic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mhall on 28/01/15.
 */
public class CategoricalMetricContributor extends AbstractMetricContributor {

  public static final String KEY_PATH = MessageUtils.getId( KEY, CategoricalMetricContributor.class );
  public static final ProfileFieldProperty
      CATEGORICAL_FIELD =
      MetricContributorUtils.createMetricProperty( KEY_PATH, "MongoFieldCategorical", Statistic.FREQUENCY_DISTRIBUTION,
          MetricContributorUtils.CATEGORICAL );

  @Override public void processField( DataSourceFieldManager manager, DataSourceFieldValue fieldValue )
      throws ProfileActionException {
    if ( !(Boolean) fieldValue.getFieldMetadata( DataSourceFieldValue.LEAF ) || !( fieldValue
        .getFieldValue() instanceof String ) ) {
      return;
    }

    String path = fieldValue.getFieldMetadata( DataSourceFieldValue.PATH );

    DataSourceField dataSourceField = manager.getPathToDataSourceFieldMap().get( path );

    if ( dataSourceField == null ) {
      // throw an exception here because the appropriate preprocessor
      // should have created/updated a DataSourceField
      throw new ProfileActionException(
          new ProfileStatusMessage( KEY_PATH, MetricContributorUtils.FIELD_NOT_FOUND + path, null ), null );
    }

    DataSourceMetricManager
        metricManager =
        dataSourceField.getMetricManagerForType( fieldValue.getFieldValue().getClass().getCanonicalName() );

    if ( metricManager == null ) {
      // throw an exception here because the appropriate preprocessor
      // should have created a type entry for the type of this field value
      throw new ProfileActionException( new ProfileStatusMessage( KEY_PATH,
          "Was expecting type " + fieldValue.getFieldValue().getClass().getCanonicalName() + " to exist.", null ),
          null );
    }

    Map<String, Object> categoricalMap = metricManager.getValueNoDefault( Statistic.FREQUENCY_DISTRIBUTION );
    Map<String, Integer> frequencyMap;

    if ( categoricalMap == null ) {
      categoricalMap = new HashMap<String, Object>( 5 );
      frequencyMap = new HashMap<String, Integer>( 100 );
      metricManager.setValue( categoricalMap, Statistic.FREQUENCY_DISTRIBUTION );
      categoricalMap.put( MetricContributorUtils.CATEGORIES, frequencyMap );
      categoricalMap.put( MetricContributorUtils.CATEGORICAL, true );
    } else {
      frequencyMap = (Map<String, Integer>) categoricalMap.get( MetricContributorUtils.CATEGORIES );
    }

    if ( (Boolean) categoricalMap.get( MetricContributorUtils.CATEGORICAL ) ) {
      String category = fieldValue.getFieldValue().toString();

      Integer frequency = frequencyMap.get( category );
      if ( frequency == null ) {
        frequency = 1;
      } else {
        frequency += 1;
      }
      frequencyMap.put( category, frequency );
      if ( frequencyMap.size() > 100 ) {
        categoricalMap.remove( MetricContributorUtils.CATEGORIES );
        categoricalMap.put( MetricContributorUtils.CATEGORICAL, false );
      }
    }
  }

  @Override public List<ProfileFieldProperty> getProfileFieldProperties() {
    return Arrays.asList( CATEGORICAL_FIELD );
  }

  @Override protected Map<String, List<String[]>> getClearMap() {
    Map<String, List<String[]>> result = new HashMap<String, List<String[]>>();
    List<String[]>
        paths =
        Arrays
            .<String[]>asList( new String[] { Statistic.FREQUENCY_DISTRIBUTION, MetricContributorUtils.CATEGORICAL } );
    result.put( String.class.getCanonicalName(), paths );

    return result;
  }
}
