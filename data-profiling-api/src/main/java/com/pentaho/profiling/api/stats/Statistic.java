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

package com.pentaho.profiling.api.stats;

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

  public static final String SKEWNESS = "skewness";

  public static final String KURTOSIS = "kurtosis";

  public static final String FREQUENCY_DISTRIBUTION = "frequencyDistribution";

  public static final String STRING_LENGTH = "stringLength";

  public static final String MAX_WORDS = "maxWords";

  public static final String MIN_WORDS = "minWords";
}
