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

package org.pentaho.profiling.api.stats;

/**
 * Base class for statistics
 *
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 */
public interface Statistic {
  public static final String KEY_PATH = "data-profiling-api/com.pentaho.profiling.api.stats";

  public static final String COUNT = "count";

  public static final String MIN = "min";

  public static final String MAX = "max";

  public static final String SUM = "sum";

  public static final String SUM_OF_SQUARES = "sumOfSquares";

  public static final String MEAN = "mean";

  public static final String VARIANCE = "variance";

  public static final String STANDARD_DEVIATION = "standardDeviation";

  public static final String MEDIAN = "median";

  public static final String PERCENTILE = "percentile";

  public static final String CARDINALITY = "cardinality";

  public static final String SKEWNESS = "skewness";

  public static final String KURTOSIS = "kurtosis";

  public static final String FREQUENCY_DISTRIBUTION = "frequencyDistribution";

  public static final String STRING_LENGTH = "stringLength";

  public static final String MAX_WORDS = "maxWords";

  public static final String MIN_WORDS = "minWords";
}
