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
