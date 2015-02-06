/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
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
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
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
  protected LocalizationService localizationService;
  protected ExecutionResultsProfileTabIcon executionResultsProfileTabIcon;
  protected static final String PROJECT_NAME = "kettle-integration-core";
  protected static final String PACKAGE_NAME = "com/pentaho/plugin/integration/messages";
  protected static final String KEY_NAME = "Spoon.TransGraph.ProfileTab.Name";
  protected static final String RESPONDS_TO_NAME = "loadTab";

  public ExecutionResultsProfileTabImpl( LocalizationService localizationService,
                                         ExecutionResultsProfileTabIcon executionResultsProfileTabIcon ) {
    this.localizationService = localizationService;
    this.executionResultsProfileTabIcon = executionResultsProfileTabIcon;
  }

  @Override
  public Map<Class<?>, Set<String>> respondsTo() {
    Map<Class<?>, Set<String>> result = new HashMap<Class<?>, Set<String>>();
    result.put( TransGraph.class, new HashSet<String>( Arrays.asList( RESPONDS_TO_NAME ) ) );
    return result;
  }


  @Override
  public void uiEvent( Object subject, String event ) {
    final TransGraph transGraph = (TransGraph) subject;
    if ( transGraph.extraViewComposite == null || transGraph.extraViewComposite.isDisposed() ) {
      transGraph.addExtraView();
    }
    ResourceBundle resourceBundle =
        localizationService.getResourceBundle( PROJECT_NAME, PACKAGE_NAME, Locale.getDefault() );
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
    return new ExecutionResultsProfileTabStepListener( transProfileBrowser, transGraph );
  }
}
