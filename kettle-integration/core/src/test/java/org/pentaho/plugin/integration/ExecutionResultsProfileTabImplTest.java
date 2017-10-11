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

package org.pentaho.plugin.integration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.spoon.trans.TransGraph;
import org.pentaho.osgi.i18n.LocalizationService;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ExecutionResultsProfileTabImplTest {
  private ExecutionResultsProfileTabImpl executionResultsProfileTabImpl;
  private Image tabIcon;
  private BundleContext bundleContext;
  private LocalizationService localizationServiceMock;
  private ExecutionResultsProfileTabIcon executionResultsProfileTabIconMock;

  @Before
  public void setUp() throws Exception {
    localizationServiceMock = mock( LocalizationService.class );
    executionResultsProfileTabIconMock = new ExecutionResultsProfileTabIcon();
    executionResultsProfileTabIconMock.setInitialized( true );
    bundleContext = mock( BundleContext.class );
    executionResultsProfileTabImpl =
      spy( new ExecutionResultsProfileTabImpl( localizationServiceMock, executionResultsProfileTabIconMock,
        bundleContext ) );
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
  public void testUiEvent() throws Exception {
    TransGraph mockTransGraph = mock( TransGraph.class );

    CTabItem mockTransProfileTab = mock( CTabItem.class );
    doReturn( mockTransProfileTab ).when( executionResultsProfileTabImpl ).createCTabItem(
      mockTransGraph.extraViewTabFolder,
      SWT.NONE );

    Browser mockTransProfileBrowser = mock( Browser.class );
    doReturn( mockTransProfileBrowser ).when( executionResultsProfileTabImpl )
      .createBrowser( mockTransGraph.extraViewTabFolder, SWT.NONE );

    ExecutionResultsProfileTabStepListener stepListener =
      new ExecutionResultsProfileTabStepListener( mockTransProfileBrowser, mockTransGraph, bundleContext );

    StepMeta mockStepMeta = mock( StepMeta.class );
    doReturn( mockStepMeta ).when( mockTransGraph ).getCurrentStep();
    doReturn( "12345" ).when( mockStepMeta ).getAttribute( stepListener.ATTR_GROUP_NAME,
      stepListener.ATTR_KEY );

    final ResourceBundle resourceBundle = new ListResourceBundle() {
      @Override protected Object[][] getContents() {
        return new Object[][] {
          { executionResultsProfileTabImpl.KEY_NAME, "Profile" }
        };
      }
    };
    doReturn( resourceBundle ).when( localizationServiceMock )
      .getResourceBundle( executionResultsProfileTabImpl.PROPERTIES_KEY,
        Locale.getDefault() );

    executionResultsProfileTabImpl.uiEvent( mockTransGraph, "" );

    verify( mockTransProfileBrowser )
      .setUrl( stepListener.PROFILE_VIEW_URL + "12345" );
    verify( mockTransProfileTab )
      .setImage( executionResultsProfileTabIconMock.getTabIcon() );
    verify( mockTransProfileTab )
      .setText( resourceBundle.getString( executionResultsProfileTabImpl.KEY_NAME ) );
  }
}