package com.pentaho.model.metrics.contributor.metricManager.impl.metrics;

import com.pentaho.profiling.api.ValueTypeMetrics;

/**
 * Created by bryan on 5/1/15.
 */
public class RegexHolder implements ValueTypeMetrics {
  private Long count;

  public RegexHolder() {
  }

  public RegexHolder( Long count ) {
    this.count = count;
  }

  public void add( long amount ) {
    if ( count == null ) {
      count = 0L;
    }
    count += amount;
  }

  public void increment() {
    add( 1L );
  }

  public Long getCount() {
    return count;
  }

  public void setCount( Long count ) {
    this.count = count;
  }

  public boolean hasCount() {
    return count != null;

  }

  @Override public Object clone() {
    return new RegexHolder( count );
  }
}
