package com.pentaho.profiling.api.action;

import com.pentaho.profiling.api.ProfileStatus;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * Created by bryan on 8/11/14.
 */
public class DefaultProfileActionResultTest {
  @Test
  public void testDefaultProfileActionExceptionNull() {
    DefaultProfileActionResult defaultProfileActionResult = new DefaultProfileActionResult() {
      @Override public void apply( ProfileStatus status ) {

      }
    };
    assertEquals( null, defaultProfileActionResult.getProfileException() );
  }
}
