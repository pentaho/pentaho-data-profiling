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

package com.pentaho.profiling.api.core.test;

import org.junit.Assume;
import org.junit.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by bryan on 1/9/15.
 */
public class BeanTester {
  private Class<?> clazz;
  private boolean testHasValidBeanConstructor;
  private boolean testHasValidGettersAndSetters;
  private boolean testHasValidBeanHashCode;
  private boolean testHasValidBeanEquals;
  private boolean testHasValidBeanToString;

  public BeanTester( Class<?> clazz ) {
    this( clazz, true, true, true, true, true );
  }

  public BeanTester( Class<?> clazz, boolean testHasValidBeanConstructor, boolean testHasValidGettersAndSetters,
                     boolean testHasValidBeanHashCode, boolean testHasValidBeanEquals,
                     boolean testHasValidBeanToString ) {
    this.clazz = clazz;
    this.testHasValidBeanConstructor = testHasValidBeanConstructor;
    this.testHasValidGettersAndSetters = testHasValidGettersAndSetters;
    this.testHasValidBeanHashCode = testHasValidBeanHashCode;
    this.testHasValidBeanEquals = testHasValidBeanEquals;
    this.testHasValidBeanToString = testHasValidBeanToString;
  }

  @Test
  public void testHasValidBeanConstructor() {
    Assume.assumeTrue( "Skipping hasValidBeanConstructor", testHasValidBeanConstructor );
    assertThat( clazz, hasValidBeanConstructor() );
  }

  @Test
  public void testHasValidGettersAndSetters() {
    Assume.assumeTrue( "Skipping hasValidGettersAndSetters", testHasValidGettersAndSetters );
    assertThat( clazz, hasValidGettersAndSetters() );
  }

  @Test
  public void testHasValidBeanHashCode() {
    Assume.assumeTrue( "Skipping testHasValidBeanHashCode", testHasValidBeanHashCode );
    assertThat( clazz, hasValidBeanHashCode() );
  }

  @Test
  public void testHasValidBeanEquals() {
    Assume.assumeTrue( "Skipping hasValidBeanEquals", testHasValidBeanEquals );
    assertThat( clazz, hasValidBeanEquals() );
  }

  @Test
  public void testHasValidBeanToString() {
    Assume.assumeTrue( "Skipping hasValidBeanToString", testHasValidBeanToString );
    assertThat( clazz, hasValidBeanToString() );
  }
}
