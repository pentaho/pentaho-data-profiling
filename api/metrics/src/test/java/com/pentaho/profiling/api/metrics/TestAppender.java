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

package com.pentaho.profiling.api.metrics;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bryan on 2/3/15.
 */
public class TestAppender implements Appender {
  private static TestAppender instance;
  private final List<LoggingEvent> events;

  public TestAppender() {
    this( new ArrayList<LoggingEvent>() );
  }

  public TestAppender( List<LoggingEvent> events ) {
    this.events = events;
  }

  public static void setInstance( TestAppender testAppender ) {
    instance = testAppender;
  }

  public static void clearInstance() {
    instance = null;
  }

  @Override public void addFilter( Filter newFilter ) {

  }

  @Override public Filter getFilter() {
    return null;
  }

  @Override public void clearFilters() {

  }

  @Override public void close() {

  }

  @Override public void doAppend( LoggingEvent event ) {
    instance.events.add( event );
  }

  @Override public String getName() {
    return null;
  }

  @Override public void setErrorHandler( ErrorHandler errorHandler ) {

  }

  @Override public ErrorHandler getErrorHandler() {
    return null;
  }

  @Override public void setLayout( Layout layout ) {

  }

  @Override public Layout getLayout() {
    return null;
  }

  @Override public void setName( String name ) {

  }

  @Override public boolean requiresLayout() {
    return false;
  }
}
