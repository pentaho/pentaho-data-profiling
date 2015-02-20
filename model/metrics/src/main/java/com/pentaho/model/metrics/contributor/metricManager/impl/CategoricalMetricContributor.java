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

package com.pentaho.model.metrics.contributor.metricManager.impl;

import com.pentaho.model.metrics.contributor.Constants;
import com.pentaho.profiling.api.MessageUtils;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricContributorUtils;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import com.pentaho.profiling.api.stats.Statistic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mhall on 28/01/15.
 */
public class CategoricalMetricContributor implements MetricManagerContributor {
  public static final String KEY_PATH =
    MessageUtils.getId( Constants.KEY, CategoricalMetricContributor.class );
  public static final ProfileFieldProperty CATEGORICAL_FIELD = MetricContributorUtils
    .createMetricProperty( KEY_PATH, "CategoricalMetricContributor", Statistic.FREQUENCY_DISTRIBUTION,
      MetricContributorUtils.CATEGORICAL );
  public static final List<String[]> CLEAR_PATHS =
    new ArrayList<String[]>( Arrays.<String[]>asList( new String[] { Statistic.FREQUENCY_DISTRIBUTION } ) );
  private static final Logger LOGGER = LoggerFactory.getLogger( CategoricalMetricContributor.class );

  @Override public Set<String> getTypes() {
    return new HashSet<String>( Arrays.asList( String.class.getCanonicalName() ) );
  }

  @Override
  public void process( DataSourceMetricManager dataSourceMetricManager, DataSourceFieldValue dataSourceFieldValue )
    throws ProfileActionException {
    Map<String, Object> categoricalMap = dataSourceMetricManager.getValueNoDefault( Statistic.FREQUENCY_DISTRIBUTION );
    Map<String, Integer> frequencyMap;

    if ( categoricalMap == null ) {
      categoricalMap = new HashMap<String, Object>( 5 );
      frequencyMap = new HashMap<String, Integer>( 100 );
      dataSourceMetricManager.setValue( categoricalMap, Statistic.FREQUENCY_DISTRIBUTION );
      categoricalMap.put( MetricContributorUtils.CATEGORIES, frequencyMap );
      categoricalMap.put( MetricContributorUtils.CATEGORICAL, true );
    } else {
      frequencyMap = (Map<String, Integer>) categoricalMap.get( MetricContributorUtils.CATEGORIES );
    }

    if ( (Boolean) categoricalMap.get( MetricContributorUtils.CATEGORICAL ) ) {
      String category = dataSourceFieldValue.getFieldValue().toString();

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

  @Override public void merge( DataSourceMetricManager into, DataSourceMetricManager from )
    throws MetricMergeException {
    Map<String, Object> firstCategoricalMap =
      into.getValueNoDefault( Statistic.FREQUENCY_DISTRIBUTION );
    Map<String, Object> secondCategoricalMap =
      from.getValueNoDefault( Statistic.FREQUENCY_DISTRIBUTION );
    if ( firstCategoricalMap == null ) {
      firstCategoricalMap = secondCategoricalMap;
    } else if ( secondCategoricalMap == null ) {
      LOGGER.debug( "First field had categorical map but second field didn't." );
    } else if ( (Boolean) firstCategoricalMap.get( MetricContributorUtils.CATEGORICAL ) ) {
      // Both are considered categorical, merge and determine if they still are
      if ( (Boolean) secondCategoricalMap.get( MetricContributorUtils.CATEGORICAL ) ) {
        Map<String, Integer> firstCategoryCountMap =
          (Map<String, Integer>) firstCategoricalMap.get( MetricContributorUtils.CATEGORIES );
        for ( Map.Entry<String, Integer> secondEntry : ( (Map<String, Integer>) secondCategoricalMap
          .get( MetricContributorUtils.CATEGORIES ) ).entrySet() ) {
          String secondCategoryKey = secondEntry.getKey();
          Integer firstValue = firstCategoryCountMap.get( secondCategoryKey );
          if ( firstValue == null ) {
            firstValue = 0;
          }
          firstCategoryCountMap.put( secondCategoryKey, firstValue + secondEntry.getValue() );
        }

        if ( firstCategoryCountMap.size() > 100 ) {
          firstCategoricalMap.remove( MetricContributorUtils.CATEGORIES );
          firstCategoricalMap.put( MetricContributorUtils.CATEGORICAL, false );
        }
      } else {
        // Second wasn't categorical.  This means the merged result won't be
        firstCategoricalMap = secondCategoricalMap;
      }
    }
    into.setValue( firstCategoricalMap, Statistic.FREQUENCY_DISTRIBUTION );
  }

  @Override public void setDerived( DataSourceMetricManager dataSourceMetricManager ) throws ProfileActionException {

  }

  @Override public void clear( DataSourceMetricManager dataSourceMetricManager ) {
    dataSourceMetricManager.clear( CLEAR_PATHS );
  }

  @Override public List<ProfileFieldProperty> getProfileFieldProperties() {
    return Arrays.asList( CATEGORICAL_FIELD );
  }
}
