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

import org.eclipse.swt.browser.Browser;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.spoon.trans.SelectedStepListener;
import org.pentaho.di.ui.spoon.trans.TransGraph;

/**
 * Created by saslan on 1/6/2015.
 */
public class ExecutionResultsProfileTabStepListener implements SelectedStepListener {
  private final TransGraph transGraph;
  private final Browser transProfileBrowser;
  protected static final String ATTR_GROUP_NAME = "com.pentaho.dataprofiling";
  protected static final String ATTR_KEY = "profileId";
  protected static final String BASE_URL = "http://localhost:8181/profileWebView/view.html#/";

  public ExecutionResultsProfileTabStepListener( Browser transProfileBrowser, TransGraph transGraph ) {
    this.transProfileBrowser = transProfileBrowser;
    this.transGraph = transGraph;
  }

  @Override
  public void onSelect( StepMeta selectedStep ) {
    if ( !transProfileBrowser.isDisposed() ) {
      transProfileBrowser.setUrl( buildUrl( selectedStep ) );
    }
  }

  /**
   * This builds the profileUrl from the current step.
   */
  public String buildUrl( StepMeta currentStep ) {
    String profileUrl = "";
    if ( currentStep != null ) {
      String profileUid = currentStep.getAttribute( ATTR_GROUP_NAME, ATTR_KEY );

      if ( profileUid != null ) {
        profileUrl = BASE_URL + profileUid;
      }
    }
    return profileUrl;
  }
}
