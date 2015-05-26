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

package com.pentaho.model.metrics.contributor.metricManager.impl;

import com.pentaho.model.metrics.contributor.Constants;
import com.pentaho.model.metrics.contributor.metricManager.impl.metrics.WordCountHolder;
import com.pentaho.profiling.api.MessageUtils;
import com.pentaho.profiling.api.MutableProfileFieldValueType;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileFieldValueType;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricContributorUtils;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class WordCountMetricContributor extends BaseMetricManagerContributor implements MetricManagerContributor {

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

  public static final String SIMPLE_NAME = WordCountMetricContributor.class.getSimpleName();
  public static final ProfileFieldProperty WORD_COUNT_MAX =
    MetricContributorUtils.createMetricProperty( KEY_PATH, WORD_COUNT_MAX_LABEL, SIMPLE_NAME, "max" );
  public static final ProfileFieldProperty WORD_COUNT_MIN =
    MetricContributorUtils.createMetricProperty( KEY_PATH, WORD_COUNT_MIN_LABEL, SIMPLE_NAME, "min" );
  public static final ProfileFieldProperty WORD_COUNT_SUM =
    MetricContributorUtils.createMetricProperty( KEY_PATH, WORD_COUNT_SUM_LABEL, SIMPLE_NAME, "sum" );

  public static final ProfileFieldProperty WORD_COUNT_MEAN =
    MetricContributorUtils.createMetricProperty( KEY_PATH, WORD_COUNT_MEAN_LABEL, SIMPLE_NAME, "mean" );

  public static final List<String[]> CLEAR_LIST =
    new ArrayList<String[]>( Arrays.asList( new String[] { WORD_COUNT_KEY_MIN }, new String[] { WORD_COUNT_KEY_MAX },
      new String[] { WORD_COUNT_KEY_SUM }, new String[] { WORD_COUNT_KEY_MEAN } ) );
  public static final String DELIMITERS = " \r\n\t.,;:'\"()?!";

  private WordCountHolder getOrCreateWordCountHolder( MutableProfileFieldValueType mutableProfileFieldValueType ) {
    WordCountHolder result = (WordCountHolder) mutableProfileFieldValueType.getValueTypeMetrics( SIMPLE_NAME );
    if ( result == null ) {
      result = new WordCountHolder();
      mutableProfileFieldValueType.setValueTypeMetrics( SIMPLE_NAME, result );
    }
    return result;
  }

  @Override public void setDerived( MutableProfileFieldValueType mutableProfileFieldValueType ) {
    getOrCreateWordCountHolder( mutableProfileFieldValueType ).calculateMean( mutableProfileFieldValueType.getCount() );
  }

  @Override public Set<String> supportedTypes() {
    return new HashSet<String>( Arrays.asList( String.class.getCanonicalName() ) );
  }

  @Override
  public void process( MutableProfileFieldValueType mutableProfileFieldValueType,
                       DataSourceFieldValue dataSourceFieldValue )
    throws ProfileActionException {
    String value = (String) dataSourceFieldValue.getFieldValue();
    StringTokenizer tokenizer = new StringTokenizer( value, DELIMITERS );
    long numWords = tokenizer.countTokens();
    getOrCreateWordCountHolder( mutableProfileFieldValueType ).offer( numWords );
  }

  @Override public void merge( MutableProfileFieldValueType into, ProfileFieldValueType from )
    throws MetricMergeException {
    WordCountHolder fromHolder = (WordCountHolder) from.getValueTypeMetrics( SIMPLE_NAME );
    if ( fromHolder != null ) {
      getOrCreateWordCountHolder( into ).offer( fromHolder );
    }
  }

  @Override public List<ProfileFieldProperty> profileFieldProperties() {
    return Arrays.asList( WORD_COUNT_MIN, WORD_COUNT_MAX, WORD_COUNT_SUM, WORD_COUNT_MEAN );
  }

  @Override public boolean equals( Object obj ) {
    return obj != null && obj.getClass().equals( WordCountMetricContributor.class );
  }
}
