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
