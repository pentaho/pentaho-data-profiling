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

import org.eclipse.swt.browser.Browser;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.spoon.trans.SelectedStepListener;
import org.pentaho.di.ui.spoon.trans.TransGraph;

/**
 * Created by saslan on 1/6/2015.
 */
public class ExecutionResultsProfileTabStepListener implements SelectedStepListener {
  protected static final String ATTR_GROUP_NAME = "com.pentaho.dataprofiling";
  protected static final String ATTR_KEY = "profileId";
  protected final String BASE_URL;
  protected final String NO_PROFILE_VIEW_URL;
  protected final String PROFILE_VIEW_URL;
  private final TransGraph transGraph;
  private final Browser transProfileBrowser;
  private final BundleContext bundleContext;

  public ExecutionResultsProfileTabStepListener( Browser transProfileBrowser, TransGraph transGraph,
                                                 BundleContext bundleContext ) {
    this.transProfileBrowser = transProfileBrowser;
    this.transGraph = transGraph;
    this.bundleContext = bundleContext;
    ServiceReference httpServiceRef = bundleContext.getServiceReference( "org.osgi.service.http.HttpService" );
    String port = "8181";
    if ( httpServiceRef != null ) {
      Object portObj = httpServiceRef.getProperty( "org.osgi.service.http.port" );
      if ( portObj != null ) {
        port = portObj.toString();
      }
    }
    BASE_URL = "http://localhost:" + port;
    NO_PROFILE_VIEW_URL = BASE_URL + "/noProfileWebView/noprofile.html";
    PROFILE_VIEW_URL = BASE_URL + "/profileWebView/view.html#/tabular/";
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
    String profileUrl = NO_PROFILE_VIEW_URL;
    if ( currentStep != null ) {
      String profileUid = currentStep.getAttribute( ATTR_GROUP_NAME, ATTR_KEY );

      if ( profileUid != null ) {
        profileUrl = PROFILE_VIEW_URL + profileUid;
      }
    }
    return profileUrl;
  }
}
