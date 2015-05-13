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

package com.pentaho.model.metrics.contributor.metricManager.impl.metrics;

import com.pentaho.profiling.api.ValueTypeMetrics;

/**
 * Created by bryan on 5/1/15.
 */
public class WordCountHolder implements ValueTypeMetrics {
  private Long min;
  private Long max;
  private Long sum;
  private Double mean;

  public WordCountHolder( Long min, Long max, Long sum, Double mean ) {
    this.min = min;
    this.max = max;
    this.sum = sum;
    this.mean = mean;
  }

  public WordCountHolder() {

  }

  public void offer( long value ) {
    if ( min == null ) {
      min = value;
    }
    if ( max == null ) {
      max = value;
    }
    if ( sum == null ) {
      sum = 0L;
    }
    min = Math.min( min, value );
    max = Math.max( max, value );
    sum = sum + value;
  }

  public void offer( WordCountHolder other ) {
    if ( min == null ) {
      min = other.min;
    } else if ( other.min != null ) {
      min = Math.min( min, other.min );
    }
    if ( max == null ) {
      max = other.max;
    } else if ( other.max != null ) {
      max = Math.max( max, other.max );
    }
    if ( sum == null ) {
      sum = other.sum;
    } else if ( other.sum != null ) {
      sum = sum + other.sum;
    }
  }

  public void calculateMean( long count ) {
    if ( sum != null ) {
      mean = sum.doubleValue() / count;
    }
  }

  public Long getMin() {
    return min;
  }

  public void setMin( Long min ) {
    this.min = min;
  }

  public Long getMax() {
    return max;
  }

  public void setMax( Long max ) {
    this.max = max;
  }

  public Long getSum() {
    return sum;
  }

  public void setSum( Long sum ) {
    this.sum = sum;
  }

  public Double getMean() {
    return mean;
  }

  public void setMean( Double mean ) {
    this.mean = mean;
  }

  @Override public Object clone() {
    return new WordCountHolder( min, max, sum, mean );
  }
}
