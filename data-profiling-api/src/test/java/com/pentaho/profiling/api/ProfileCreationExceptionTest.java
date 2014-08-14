package com.pentaho.profiling.api;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by bryan on 8/14/14.
 */
public class ProfileCreationExceptionTest {
  @Test
  public void testMessageConstructor() {
    String message = "MESSAGE";
    assertEquals( message, new ProfileCreationException( message ).getMessage() );
  }

  @Test
  public void testThrowableConstructor() {
    Throwable throwable = new Throwable( "MESSAGE_2" );
    assertEquals( throwable, new ProfileCreationException( throwable ).getCause() );
  }

  @Test
  public  void testMessageAndThrowableConstructor() {
    String message = "MESSAGE";
    Throwable throwable = new Throwable( "MESSAGE_2" );
    ProfileCreationException profileCreationException = new ProfileCreationException( message, throwable );
    assertEquals( message, profileCreationException.getMessage() );
    assertEquals( throwable, profileCreationException.getCause() );
  }
}
