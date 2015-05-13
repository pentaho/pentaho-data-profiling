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

import java.util.Date;

/**
 * Created by bryan on 4/30/15.
 */
public class DateHolder implements ValueTypeMetrics {
  private Date min;
  private Date max;

  public DateHolder() {
  }

  public void offer( Date newDate ) {
    if ( newDate == null ) {
      return;
    }
    if ( min == null ) {
      min = newDate;
    }
    if ( max == null ) {
      max = newDate;
    }
    min = new Date( Math.min( min.getTime(), newDate.getTime() ) );
    max = new Date( Math.max( max.getTime(), newDate.getTime() ) );
  }

  public Date getMin() {
    return min;
  }

  public void setMin( Date min ) {
    this.min = min;
  }

  public Date getMax() {
    return max;
  }

  public void setMax( Date max ) {
    this.max = max;
  }

  @Override public Object clone() {
    DateHolder result = new DateHolder();
    result.min = new Date( min.getTime() );
    result.max = new Date( max.getTime() );
    return result;
  }
}
