package com.pentaho.profiling.api.metrics;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by bryan on 2/5/15.
 */
public class MetricMergeExceptionTest {

  @Test
  public void testConstructor() {
    String message = "test-message";
    Exception cause = mock( Exception.class );
    MetricMergeException metricMergeException = new MetricMergeException( message, cause );
    assertEquals( message, metricMergeException.getMessage() );
    assertEquals( cause, metricMergeException.getCause() );
  }
}
