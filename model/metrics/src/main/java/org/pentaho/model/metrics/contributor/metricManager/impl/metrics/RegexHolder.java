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
