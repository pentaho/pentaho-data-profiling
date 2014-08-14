package com.pentaho.profiling.api.action;

import com.pentaho.profiling.api.action.DefaultProfileAction;
import com.pentaho.profiling.api.action.ProfileActionResult;
import com.pentaho.profiling.api.action.ThenAlreadyRequestedException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by bryan on 8/11/14.
 */
public class DefaultProfileActionTest {
  @Test
  public void testDefaultConstructorNullThen() {
    DefaultProfileAction defaultProfileAction = new DefaultProfileAction() {
      @Override public ProfileActionResult execute() {
        return null;
      }
    };
    assertEquals( null, defaultProfileAction.then() );
  }

  @Test( expected = ThenAlreadyRequestedException.class )
  public void testExceptionThrownIfThenSetAfterRequested() throws ThenAlreadyRequestedException {
    DefaultProfileAction defaultProfileAction = new DefaultProfileAction() {
      @Override public ProfileActionResult execute() {
        return null;
      }
    };
    defaultProfileAction.then();
    defaultProfileAction.setThen( null );
  }
}
