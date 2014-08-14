package com.pentaho.profiling.api.action;

import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.action.ExceptionProfileActionResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Created by bryan on 8/11/14.
 */
public class ExceptionProfileActionResultTest {
  @Test
  public void testExceptionProfileActionResultStoresException() {
    Exception exception = new Exception();
    ExceptionProfileActionResult exceptionProfileActionResult = new ExceptionProfileActionResult( exception );
    assertEquals( exception, exceptionProfileActionResult.getProfileException() );
  }

  @Test
  public void testExceptionProfileActionResultDoesNothingOnApply() {
    ProfileStatus status = mock( ProfileStatus.class );
    ExceptionProfileActionResult exceptionProfileActionResult = new ExceptionProfileActionResult( null );
    exceptionProfileActionResult.apply( status );
    verifyNoMoreInteractions( status );
  }
}
