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
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.osgi.framework.BundleContext;
import org.pentaho.di.ui.spoon.SpoonUiExtenderPluginInterface;
import org.pentaho.di.ui.spoon.trans.TransGraph;
import org.pentaho.osgi.i18n.LocalizationService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Created by saslan on 12/15/2014.
 */
public class ExecutionResultsProfileTabImpl implements SpoonUiExtenderPluginInterface {
  protected static final String PROJECT_NAME = "kettle-integration-core";
  protected static final String PACKAGE_NAME = "org/pentaho/plugin/integration/messages";
  protected static final String PROPERTIES_KEY = "kettle-integration-core.org.pentaho.plugin.integration.messages";
  protected static final String KEY_NAME = "Spoon.TransGraph.ProfileTab.Name";
  private final LocalizationService localizationService;
  private final ExecutionResultsProfileTabIcon executionResultsProfileTabIcon;
  private final BundleContext bundleContext;

  public ExecutionResultsProfileTabImpl( LocalizationService localizationService,
                                         ExecutionResultsProfileTabIcon executionResultsProfileTabIcon,
                                         BundleContext bundleContext ) {
    this.localizationService = localizationService;
    this.executionResultsProfileTabIcon = executionResultsProfileTabIcon;
    this.bundleContext = bundleContext;
  }

  @Override
  public Map<Class<?>, Set<String>> respondsTo() {
    Map<Class<?>, Set<String>> result = new HashMap<Class<?>, Set<String>>();
    result.put( TransGraph.class, new HashSet<String>( Arrays.asList( TransGraph.LOAD_TAB ) ) );
    return result;
  }


  @Override
  public void uiEvent( Object subject, String event ) {
    final TransGraph transGraph = (TransGraph) subject;
    if ( transGraph.extraViewComposite == null || transGraph.extraViewComposite.isDisposed() ) {
      transGraph.addExtraView();
    }
    ResourceBundle resourceBundle =
      localizationService.getResourceBundle( PROPERTIES_KEY, Locale.getDefault() );
    String tabText = resourceBundle.getString( KEY_NAME );

    CTabItem transProfileTab = createCTabItem( transGraph.extraViewTabFolder, SWT.NONE );
    transProfileTab.setImage( executionResultsProfileTabIcon.getTabIcon() );
    transProfileTab.setText( tabText );

    Browser transProfileBrowser = createBrowser( transGraph.extraViewTabFolder, SWT.NONE );
    transProfileTab.setControl( transProfileBrowser );

    ExecutionResultsProfileTabStepListener selectedStepListener =
      createExecutionResultsProfileTabStepListener( transProfileBrowser, transGraph );
    transGraph.addSelectedStepListener( selectedStepListener );
    selectedStepListener.onSelect( transGraph.getCurrentStep() );
  }

  //For unit testing purposes
  protected CTabItem createCTabItem( CTabFolder cTabFolder, int i ) {
    return new CTabItem( cTabFolder, i );
  }

  //For unit testing purposes
  protected Browser createBrowser( CTabFolder cTabFolder, int i ) {
    return new Browser( cTabFolder, i );
  }

  //For unit testing purposes
  protected ExecutionResultsProfileTabStepListener createExecutionResultsProfileTabStepListener(
    Browser transProfileBrowser, TransGraph transGraph ) {
    return new ExecutionResultsProfileTabStepListener( transProfileBrowser, transGraph, bundleContext );
  }
}
