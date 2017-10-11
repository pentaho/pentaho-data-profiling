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

import org.eclipse.swt.graphics.Image;
import org.pentaho.di.ui.spoon.Spoon;

/**
 * Created by saslan on 1/8/2015.
 */
public class ExecutionResultsProfileTabIcon {
  private Image tabIcon;
  private boolean isInitialized = false;

  //For unit testing
  protected void setInitialized( boolean bool ) {
    isInitialized = bool;
  }

  //For unit testing
  protected Image initTabIcon() {
    return new Image( Spoon.getInstance().getDisplay(),
      ExecutionResultsProfileTabImpl.class.getClassLoader().getResourceAsStream( "images/show_profile.png" ) );
  }

  public Image getTabIcon() {
    if ( !isInitialized ) {
      tabIcon = initTabIcon();
      isInitialized = true;
    }
    return tabIcon;
  }
}
