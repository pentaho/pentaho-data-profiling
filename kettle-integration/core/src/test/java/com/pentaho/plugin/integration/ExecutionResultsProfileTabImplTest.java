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

package com.pentaho.plugin.integration;

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
      .getResourceBundle( executionResultsProfileTabImpl.PROJECT_NAME, executionResultsProfileTabImpl.PACKAGE_NAME,
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