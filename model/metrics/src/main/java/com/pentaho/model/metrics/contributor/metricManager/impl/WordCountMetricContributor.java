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

import com.pentaho.profiling.api.metrics.MetricContributorUtils;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.NVL;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import com.pentaho.model.metrics.contributor.Constants;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.model.metrics.contributor.metricManager.NVLOperations;
import com.pentaho.profiling.api.MessageUtils;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.action.ProfileActionException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class WordCountMetricContributor implements MetricManagerContributor {

  public static final String KEY_PATH =
    MessageUtils.getId( Constants.KEY, WordCountMetricContributor.class );
  public static final String WORD_COUNT_MAX_LABEL = "WordCountMetricContributor.WordCountMax";
  public static final String WORD_COUNT_MIN_LABEL = "WordCountMetricContributor.WordCountMin";
  public static final String WORD_COUNT_SUM_LABEL = "WordCountMetricContributor.WordCountSum";
  public static final String WORD_COUNT_MEAN_LABEL = "WordCountMetricContributor.WordCountMean";
  public static final String WORD_COUNT_KEY_MAX = "com.pentaho.str.max_word_count";
  public static final String WORD_COUNT_KEY_MIN = "com.pentaho.str.min_word_count";
  public static final String WORD_COUNT_KEY_SUM = "com.pentaho.str.sum_word_count";
  public static final String WORD_COUNT_KEY_MEAN = "com.pentaho.str.mean_word_count";

  public static final ProfileFieldProperty WORD_COUNT_MAX =
      MetricContributorUtils.createMetricProperty( KEY_PATH, WORD_COUNT_MAX_LABEL, WORD_COUNT_KEY_MAX );
  public static final ProfileFieldProperty WORD_COUNT_MIN =
      MetricContributorUtils.createMetricProperty( KEY_PATH, WORD_COUNT_MIN_LABEL, WORD_COUNT_KEY_MIN );
  public static final ProfileFieldProperty WORD_COUNT_SUM =
      MetricContributorUtils.createMetricProperty( KEY_PATH, WORD_COUNT_SUM_LABEL, WORD_COUNT_KEY_SUM );
  public static final ProfileFieldProperty WORD_COUNT_MEAN =
      MetricContributorUtils.createMetricProperty( KEY_PATH, WORD_COUNT_MEAN_LABEL, WORD_COUNT_KEY_MEAN );

  public static final List<String[]> CLEAR_LIST =
      new ArrayList<String[]>( Arrays.asList( new String[] { WORD_COUNT_KEY_MIN }, new String[] { WORD_COUNT_KEY_MAX },
      new String[] { WORD_COUNT_KEY_SUM }, new String[] { WORD_COUNT_KEY_MEAN } ) );

  public static final String DELIMITERS = " \r\n\t.,;:'\"()?!";

  private final NVL nvl;

  public WordCountMetricContributor() {
    this( new NVL() );
  }

  public WordCountMetricContributor( NVL nvl ) {
    this.nvl = nvl;
  }

  private void setDerived( DataSourceMetricManager metricsForFieldType, Long newSumStat ) {
    Long count = metricsForFieldType.getValueNoDefault( MetricContributorUtils.COUNT );
    metricsForFieldType.setValue( ( newSumStat.doubleValue() / count.doubleValue() ), WORD_COUNT_KEY_MEAN );
  }

  @Override public Set<String> getTypes() {
    return new HashSet<String>( Arrays.asList( String.class.getCanonicalName() ) );
  }

  @Override
  public void process( DataSourceMetricManager dataSourceMetricManager, DataSourceFieldValue dataSourceFieldValue )
    throws ProfileActionException {
    String value = (String) dataSourceFieldValue.getFieldValue();
    StringTokenizer tokenizer = new StringTokenizer( value, DELIMITERS );
    long numWords = tokenizer.countTokens();
    nvl.performAndSet( NVLOperations.LONG_MIN, dataSourceMetricManager, numWords, WORD_COUNT_KEY_MIN );
    nvl.performAndSet( NVLOperations.LONG_MAX, dataSourceMetricManager, numWords, WORD_COUNT_KEY_MAX );
    Long newSumStat = nvl.performAndSet( NVLOperations.LONG_SUM, dataSourceMetricManager, numWords,
        WORD_COUNT_KEY_SUM );
    setDerived( dataSourceMetricManager, newSumStat );
  }

  @Override public void merge( DataSourceMetricManager into, DataSourceMetricManager from )
    throws MetricMergeException {
    nvl.performAndSet( NVLOperations.LONG_MIN, into, from, WORD_COUNT_KEY_MIN );
    nvl.performAndSet( NVLOperations.LONG_MAX, into, from, WORD_COUNT_KEY_MAX );
    Long newSumStat = nvl.performAndSet( NVLOperations.LONG_SUM, into, from,
        WORD_COUNT_KEY_SUM );
    setDerived( into, newSumStat );
  }

  @Override public void setDerived( DataSourceMetricManager dataSourceMetricManager ) throws ProfileActionException {

  }

  @Override public List<ProfileFieldProperty> getProfileFieldProperties() {
    return Arrays.asList( WORD_COUNT_MIN, WORD_COUNT_MAX, WORD_COUNT_SUM, WORD_COUNT_MEAN );
  }

  @Override public void clear( DataSourceMetricManager dataSourceMetricManager ) {
    dataSourceMetricManager.clear( CLEAR_LIST );
  }
}
