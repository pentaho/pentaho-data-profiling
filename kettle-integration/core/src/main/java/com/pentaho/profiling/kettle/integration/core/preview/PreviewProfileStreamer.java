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

package com.pentaho.profiling.kettle.integration.core.preview;

import com.pentaho.profiling.api.AggregateProfile;
import com.pentaho.profiling.api.AggregateProfileService;
import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.ProfileCreationException;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.ProfilingService;
import com.pentaho.profiling.api.StreamingProfileService;
import com.pentaho.profiling.api.configuration.ProfileConfiguration;
import com.pentaho.profiling.api.configuration.core.AggregateProfileMetadata;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.pentaho.di.core.Const;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.debug.StepDebugMeta;
import org.pentaho.di.trans.debug.TransDebugMeta;
import org.pentaho.di.trans.debug.TransDebugMetaWrapper;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.spoon.SpoonUiExtenderPluginInterface;
import org.pentaho.di.ui.spoon.trans.TransGraph;
import org.pentaho.di.ui.spoon.trans.TransPreviewDialog;
import org.pentaho.osgi.i18n.LocalizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Created by bryan on 3/23/15.
 */
public class PreviewProfileStreamer implements SpoonUiExtenderPluginInterface {
  public static final String UNABLE_TO_CREATE_PROFILE = "Unable to create profile";
  protected static final String PROJECT_NAME = "kettle-integration-core";
  protected static final String PACKAGE_NAME = "com/pentaho/profiling/kettle/integration/core/preview/messages";
  protected static final String KEY_NAME = "PreviewProfileStreamer.Profile";
  private static final Logger LOGGER = LoggerFactory.getLogger( PreviewProfileStreamer.class );
  private final LocalizationService localizationService;
  private final ProfilingService profilingService;
  private final AggregateProfileService aggregateProfileService;
  private final StreamingProfileService streamingProfileService;
  private final Map<TransPreviewDialog, Browser> transPreviewDialogBrowserMap =
    new HashMap<TransPreviewDialog, Browser>();
  private final Map<StepDebugMeta, String> stepDebugMetaProfileIdMap = new HashMap<StepDebugMeta, String>();

  public PreviewProfileStreamer( LocalizationService localizationService, ProfilingService profilingService,
                                 AggregateProfileService aggregateProfileService,
                                 StreamingProfileService streamingProfileService ) {
    this.localizationService = localizationService;
    this.profilingService = profilingService;
    this.aggregateProfileService = aggregateProfileService;
    this.streamingProfileService = streamingProfileService;
  }

  @Override public Map<Class<?>, Set<String>> respondsTo() {
    Map<Class<?>, Set<String>> result = new HashMap<Class<?>, Set<String>>();
    result.put( TransDebugMetaWrapper.class, new HashSet<String>( Arrays.asList( TransGraph.PREVIEW_TRANS ) ) );
    result
      .put( TransPreviewDialog.class, new HashSet<String>( Arrays.asList( TransPreviewDialog.TRANS_PREVIEW_DIALOG ) ) );
    result
      .put( TransPreviewDialog.TransPreviewDialogSetDataWrapper.class,
        new HashSet<String>( Arrays.asList( TransPreviewDialog.TRANS_PREVIEW_DIALOG_SET_DATA ) ) );
    return result;
  }

  @Override public void uiEvent( Object o, final String s ) {
    if ( TransGraph.PREVIEW_TRANS.equals( s ) ) {
      TransDebugMetaWrapper transDebugMetaWrapper = (TransDebugMetaWrapper) o;
      final Trans trans = transDebugMetaWrapper.getTrans();
      TransDebugMeta transDebugMeta = transDebugMetaWrapper.getTransDebugMeta();
      Map<StepMeta, StepDebugMeta> stepDebugMetaMap = transDebugMeta.getStepDebugMetaMap();
      for ( final StepMeta stepMeta : stepDebugMetaMap.keySet() ) {
        final StepDebugMeta stepDebugMeta = stepDebugMetaMap.get( stepMeta );
        try {
          ProfileStatusManager profileStatusManager = profilingService.create(
            new ProfileConfiguration( new AggregateProfileMetadata(), null, null ) );
          String profileId = profileStatusManager.getId();
          stepDebugMetaProfileIdMap.put( stepDebugMeta, profileId );
          AggregateProfile aggregateProfile = aggregateProfileService.getAggregateProfile( profileId );
          PreviewProfileStreamerListener previewProfileStreamerListener =
            new PreviewProfileStreamerListener( profilingService, streamingProfileService, aggregateProfile.getId(),
              aggregateProfileService );
          transDebugMeta.addBreakPointListers( previewProfileStreamerListener );
          for ( StepInterface baseStep : trans.findBaseSteps( stepMeta.getName() ) ) {
            baseStep.addRowListener( previewProfileStreamerListener );
          }
          profileStatusManager.write( new ProfileStatusWriteOperation<Void>() {
            @Override public Void write( MutableProfileStatus profileStatus ) {
              profileStatus.setName( trans.getName() + "." + stepMeta.getName() );
              return null;
            }
          } );
        } catch ( ProfileCreationException e ) {
          LOGGER.error( UNABLE_TO_CREATE_PROFILE, e );
        }
      }
    } else if ( TransPreviewDialog.TRANS_PREVIEW_DIALOG.equals( s ) ) {
      final TransPreviewDialog transPreviewDialog = (TransPreviewDialog) o;

      CTabFolder tabFolder = transPreviewDialog.getTabFolder();

      CTabItem profileCTabItem = new CTabItem( tabFolder, SWT.NONE );
      ResourceBundle resourceBundle =
        localizationService.getResourceBundle( PROJECT_NAME, PACKAGE_NAME, Locale.getDefault() );
      profileCTabItem.setText( resourceBundle.getString( KEY_NAME ) );

      Browser profileBrowser = new Browser( tabFolder, SWT.NONE );
      transPreviewDialogBrowserMap.put( transPreviewDialog, profileBrowser );
      profileBrowser.addDisposeListener( new DisposeListener() {
        @Override public void widgetDisposed( DisposeEvent disposeEvent ) {
          transPreviewDialogBrowserMap.remove( transPreviewDialog );
        }
      } );
      FormData fdFields = new FormData();
      fdFields.left = new FormAttachment( 0, 0 );
      fdFields.top = new FormAttachment( transPreviewDialog.getWlFields(), Const.MARGIN );
      fdFields.right = new FormAttachment( 100, 0 );
      fdFields.bottom = new FormAttachment( 100, 0 );
      profileBrowser.setLayoutData( fdFields );
      PropsUI.getInstance().setLook( profileBrowser );
      profileCTabItem.setControl( profileBrowser );
    } else if ( TransPreviewDialog.TRANS_PREVIEW_DIALOG_SET_DATA.equals( s ) ) {
      TransPreviewDialog.TransPreviewDialogSetDataWrapper transPreviewDialogSetDataWrapper =
        (TransPreviewDialog.TransPreviewDialogSetDataWrapper) o;
      Browser browser = transPreviewDialogBrowserMap.get( transPreviewDialogSetDataWrapper.getTransPreviewDialog() );
      if ( browser != null && !browser.isDisposed() ) {
        String profileId = stepDebugMetaProfileIdMap.get( transPreviewDialogSetDataWrapper.getStepDebugMeta() );
        if ( profileId != null ) {
          String url = "http://localhost:8181/profileWebView/view.html#/tabular/" + profileId;
          if ( !url.equals( browser.getUrl() ) ) {
            browser.setUrl( url );
          }
        }
      }
    }
  }
}
