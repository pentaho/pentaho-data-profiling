package com.pentaho.plugin.integration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.SpoonUiExtenderPluginInterface;
import org.pentaho.di.ui.spoon.trans.SelectedStepListener;
import org.pentaho.di.ui.spoon.trans.TransGraph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by saslan on 12/15/2014.
 */
public class ExecutionResultsProfileTabImpl implements SpoonUiExtenderPluginInterface, SelectedStepListener {
  private static Class<?> PKG = Spoon.class; // for i18n purposes, needed by Translator2!!

  private StepMeta selectedStep;

  protected CTabItem transProfileTab;

  private TransGraph transGraph;

  protected Browser transProfileBrowser;

  protected static final String ATTR_GROUP_NAME = "com.pentaho.dataprofiling";

  protected static final String ATTR_KEY = "profileId";

  @Override
  public Map<Class<?>, Set<String>> respondsTo() {
    Map<Class<?>, Set<String>> result = new HashMap<Class<?>, Set<String>>();
    result.put( TransGraph.class, new HashSet<String>( Arrays.asList( "loadTab" ) ) );
    return result;
  }

  @Override
  public void onSelect( StepMeta aSelectedStep ) {
    selectedStep = aSelectedStep;
    refreshView( aSelectedStep );
  }

  @Override
  public void uiEvent( Object subject, String event ) {
    transGraph = (TransGraph) subject;
    if ( transGraph.extraViewComposite == null || transGraph.extraViewComposite.isDisposed() ) {
      transGraph.addExtraView();
    }
    transGraph.addSelectedStepListener( this );
    transProfileTab = createCTabItem( transGraph.extraViewTabFolder, SWT.NONE );
    //    transProfileTab.setImage( new Image( PropsUI.getDisplay(), "images/show_profile.png" ) );
    transProfileTab.setText( "Profile" );

    refreshView( transGraph.getCurrentStep() );
  }

  /**
   * This builds the profileUrl from the current step.
   */
  public String buildUrl( StepMeta currentStep ) {
    String profileUrl = "";
    if ( currentStep != null ) {
      String profileUid = currentStep.getAttribute( ATTR_GROUP_NAME, ATTR_KEY );

      if ( profileUid != null ) {
        profileUrl = "http://localhost:8181/profileWebView/view.html#/" + profileUid;
      }
    }
    return profileUrl;
  }

  /**
   * This refresh is driven by outside influenced using listeners and so on.
   */
  public synchronized void refreshView( StepMeta selectedStep ) {
    if ( transGraph != null && !transGraph.isDisposed() ) {
      transProfileBrowser = createBrowser( transGraph.extraViewTabFolder, SWT.NONE );
      transProfileTab.setControl( transProfileBrowser );
      transProfileBrowser.setUrl( buildUrl( selectedStep ) );
    }
  }

  public CTabItem createCTabItem( CTabFolder cTabFolder, int i ) {
    return new CTabItem( cTabFolder, i );
  }

  public Browser createBrowser( CTabFolder cTabFolder, int i ) {
    return new Browser( cTabFolder, i );
  }
}
