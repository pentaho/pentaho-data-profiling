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

package org.pentaho.model.metrics.contributor.metricManager.impl.metrics;

import org.pentaho.profiling.api.ValueTypeMetrics;

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
