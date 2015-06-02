/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
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
  protected static final String ATTR_GROUP_NAME = "org.pentaho.dataprofiling";
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
