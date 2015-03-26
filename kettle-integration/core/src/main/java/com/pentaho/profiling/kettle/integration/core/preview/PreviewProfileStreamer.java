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

package com.pentaho.profiling.kettle.integration.core.preview;

import com.pentaho.profiling.api.AggregateProfile;
import com.pentaho.profiling.api.AggregateProfileService;
import com.pentaho.profiling.api.ProfileCreateRequest;
import com.pentaho.profiling.api.ProfileCreationException;
import com.pentaho.profiling.api.ProfilingService;
import com.pentaho.profiling.api.StreamingProfileService;
import com.pentaho.profiling.api.datasource.DataSourceReference;
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
import java.util.UUID;

/**
 * Created by bryan on 3/23/15.
 */
public class PreviewProfileStreamer implements SpoonUiExtenderPluginInterface {
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

  @Override public void uiEvent( Object o, String s ) {
    if ( TransGraph.PREVIEW_TRANS.equals( s ) ) {
      TransDebugMetaWrapper transDebugMetaWrapper = (TransDebugMetaWrapper) o;
      Trans trans = transDebugMetaWrapper.getTrans();
      TransDebugMeta transDebugMeta = transDebugMetaWrapper.getTransDebugMeta();
      Map<StepMeta, StepDebugMeta> stepDebugMetaMap = transDebugMeta.getStepDebugMetaMap();
      for ( final StepMeta stepMeta : stepDebugMetaMap.keySet() ) {
        final StepDebugMeta stepDebugMeta = stepDebugMetaMap.get( stepMeta );
        try {
          String profileId = profilingService.create(
            new ProfileCreateRequest( new DataSourceReference( UUID.randomUUID().toString(),
              AggregateProfile.AGGREGATE_PROFILE ), null ) ).getId();
          stepDebugMetaProfileIdMap.put( stepDebugMeta, profileId );
          AggregateProfile aggregateProfile = aggregateProfileService.getAggregateProfile( profileId );
          PreviewProfileStreamerListener previewProfileStreamerListener =
            new PreviewProfileStreamerListener( profilingService, streamingProfileService, aggregateProfile.getId(),
              aggregateProfileService );
          transDebugMeta.addBreakPointListers( previewProfileStreamerListener );
          for ( StepInterface baseStep : trans.findBaseSteps( stepMeta.getName() ) ) {
            baseStep.addRowListener( previewProfileStreamerListener );
          }
        } catch ( ProfileCreationException e ) {
          e.printStackTrace();
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
          String url = "http://localhost:8181/profileWebView/view.html#/" + profileId;
          if ( !url.equals( browser.getUrl() ) ) {
            browser.setUrl( url );
          }
        }
      }
    }
  }
}
