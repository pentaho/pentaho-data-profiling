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

package org.pentaho.profiling.api.metrics;

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
  private static TestAppender instance = new TestAppender();
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

  public static TestAppender getInstance() {
    return instance;
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
