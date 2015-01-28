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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class WordCountMetricContributor extends AbstractMetricContributor {

  public static final String KEY_PATH = MessageUtils.getId( KEY, WordCountMetricContributor.class );
  public static final String WORD_COUNT_MAX_LABEL = "WordCountMax";
  public static final String WORD_COUNT_MIN_LABEL = "WordCountMin";
  public static final String WORD_COUNT_SUM_LABEL = "WordCountSum";
  public static final String WORD_COUNT_MEAN_LABEL = "WordCountMean";
  public static final String WORD_COUNT_KEY_MAX = "com.pentaho.str.max_word_count";
  public static final String WORD_COUNT_KEY_MIN = "com.pentaho.str.min_word_count";
  public static final String WORD_COUNT_KEY_SUM = "com.pentaho.str.sum_word_count";
  public static final String WORD_COUNT_KEY_MEAN = "com.pentaho.str.mean_word_count";

  public static final ProfileFieldProperty
      WORD_COUNT_MAX =
      MetricContributorUtils.createMetricProperty( KEY_PATH, WORD_COUNT_MAX_LABEL, WORD_COUNT_KEY_MAX );
  public static final ProfileFieldProperty
      WORD_COUNT_MIN =
      MetricContributorUtils.createMetricProperty( KEY_PATH, WORD_COUNT_MIN_LABEL, WORD_COUNT_KEY_MIN );
  public static final ProfileFieldProperty
      WORD_COUNT_SUM =
      MetricContributorUtils.createMetricProperty( KEY_PATH, WORD_COUNT_SUM_LABEL, WORD_COUNT_KEY_SUM );
  public static final ProfileFieldProperty
      WORD_COUNT_MEAN =
      MetricContributorUtils.createMetricProperty( KEY_PATH, WORD_COUNT_MEAN_LABEL, WORD_COUNT_KEY_MEAN );

  public static final String DELIMITERS = " \r\n\t.,;:'\"()?!";

  public static void updateType( DataSourceMetricManager metricsForFieldType, long numWords ) {
    Long existingMinStat = metricsForFieldType.getValue( numWords, WORD_COUNT_KEY_MIN );
    metricsForFieldType.setValue( Math.min( numWords, existingMinStat ), WORD_COUNT_KEY_MIN );

    Long existingMaxStat = metricsForFieldType.getValue( numWords, WORD_COUNT_KEY_MAX );
    metricsForFieldType.setValue( Math.max( numWords, existingMaxStat ), WORD_COUNT_KEY_MAX );

    Long existingSumStat = metricsForFieldType.getValue( 0L, WORD_COUNT_KEY_SUM );
    Long newSumStat = existingSumStat + numWords;
    metricsForFieldType.setValue( newSumStat, WORD_COUNT_KEY_SUM );

    Long count = metricsForFieldType.getValueNoDefault( MetricContributorUtils.COUNT );
    metricsForFieldType.setValue( ( newSumStat.doubleValue() / count.doubleValue() ), WORD_COUNT_KEY_MEAN );
  }

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

    String value = (String) fieldValue.getFieldValue();
    StringTokenizer tokenizer = new StringTokenizer( value, DELIMITERS );
    long numWords = tokenizer.countTokens();
    updateType( metricManager, numWords );
  }

  @Override public List<ProfileFieldProperty> getProfileFieldProperties() {
    return Arrays.asList( WORD_COUNT_MIN, WORD_COUNT_MAX, WORD_COUNT_SUM, WORD_COUNT_MEAN );
  }

  @Override protected Map<String, List<String[]>> getClearMap() {
    Map<String, List<String[]>> result = new HashMap<String, List<String[]>>();
    List<String[]> paths =
        Arrays.<String[]>asList( new String[] { WORD_COUNT_KEY_MIN }, new String[] { WORD_COUNT_KEY_MAX },
            new String[] { WORD_COUNT_KEY_SUM }, new String[] { WORD_COUNT_KEY_MEAN } );

    result.put( String.class.getCanonicalName(), paths );

    return result;
  }
}
