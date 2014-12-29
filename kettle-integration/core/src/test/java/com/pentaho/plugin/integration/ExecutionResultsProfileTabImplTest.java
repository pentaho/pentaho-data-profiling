package com.pentaho.plugin.integration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.spoon.trans.TransGraph;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ExecutionResultsProfileTabImplTest {
  private ExecutionResultsProfileTabImpl executionResultsProfileTabImpl;

  @Before
  public void setUp() throws Exception {
    executionResultsProfileTabImpl = spy( new ExecutionResultsProfileTabImpl() );
  }

  @After
  public void tearDown() throws Exception {
    executionResultsProfileTabImpl = null;
  }

  @Test
  public void testRespondsTo() throws Exception {
    Map<Class<?>, Set<String>> result = executionResultsProfileTabImpl.respondsTo();
    assertEquals( result.size(), 1 );
  }

  @Test
  public void testOnSelect() throws Exception {
    StepMeta mockStepMeta = mock( StepMeta.class );
    doNothing().when( executionResultsProfileTabImpl ).refreshView(mockStepMeta );
    executionResultsProfileTabImpl.onSelect(mockStepMeta);

  }

  @Test
  public void testUiEvent() throws Exception {
    TransGraph mockTransGraph = mock( TransGraph.class );
    mockTransGraph.extraViewTabFolder = mock( CTabFolder.class );
    executionResultsProfileTabImpl.transProfileTab = mock( CTabItem.class );
    executionResultsProfileTabImpl.transProfileBrowser = mock(Browser.class);
    StepMeta mockStepMeta = mock( StepMeta.class );
    doReturn(executionResultsProfileTabImpl.transProfileTab ).when( executionResultsProfileTabImpl ).createCTabItem(mockTransGraph.extraViewTabFolder,
      SWT.NONE );
    doReturn( executionResultsProfileTabImpl.transProfileBrowser ).when( executionResultsProfileTabImpl )
      .createBrowser( mockTransGraph.extraViewTabFolder, SWT.NONE);
    doReturn( mockStepMeta ).when( mockTransGraph ).getCurrentStep();
    doReturn( "1234" ).when( mockStepMeta )
      .getAttribute( executionResultsProfileTabImpl.ATTR_GROUP_NAME, executionResultsProfileTabImpl.ATTR_KEY );
    executionResultsProfileTabImpl.uiEvent( mockTransGraph, "" );

    verify( executionResultsProfileTabImpl.transProfileBrowser )
      .setUrl( "http://localhost:8181/profileWebView/view.html#/1234");
  }
}