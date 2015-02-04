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
