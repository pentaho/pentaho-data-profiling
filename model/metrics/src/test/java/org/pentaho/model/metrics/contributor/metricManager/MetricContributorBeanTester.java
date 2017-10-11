/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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

package org.pentaho.model.metrics.contributor.metricManager;

import org.junit.Assume;
import org.junit.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEqualsExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCodeExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToStringExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSettersExcluding;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by bryan on 3/19/15.
 */
public class MetricContributorBeanTester {
  private final Class clazz;

  public MetricContributorBeanTester( Class clazz ) {
    this.clazz = clazz;
  }

  @Test
  public void testHasValidBeanConstructor() {
    assertThat( clazz, hasValidBeanConstructor() );
  }

  @Test
  public void testHasValidGettersAndSetters() {
    assertThat( clazz, hasValidGettersAndSettersExcluding( "derived", "name" ) );
  }

  @Test
  public void testHasValidBeanHashCode() {
    assertThat( clazz, hasValidBeanHashCodeExcluding( "derived", "name" ) );
  }

  @Test
  public void testHasValidBeanEquals() {
    assertThat( clazz, hasValidBeanEqualsExcluding( "derived", "name" ) );
  }

  @Test// Noop
  public void testHasValidBeanToString() {
    assertThat( clazz, hasValidBeanToStringExcluding( "derived", "name" ) );
  }
}
