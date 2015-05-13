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

package com.pentaho.plugin.integration.extension;

import com.pentaho.profiling.api.AggregateProfileService;
import com.pentaho.profiling.api.ProfileCreationException;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfilingService;
import com.pentaho.profiling.api.StreamingProfileService;
import com.pentaho.profiling.api.configuration.ProfileConfiguration;
import com.pentaho.profiling.api.configuration.core.AggregateProfileMetadata;
import com.pentaho.profiling.api.configuration.core.StreamingProfileMetadata;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransListener;
import org.pentaho.di.trans.step.StepMetaDataCombi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by bryan on 5/13/15.
 */
@ExtensionPoint( id = "profileTransformationPrepareExecution", extensionPointId = "TransformationStartThreads",
  description = "This will register row listeners for streaming profiles on the specified steps" )
public class ProfileTransformationPrepareExecution implements ExtensionPointInterface {
  public static final String PROFILE_STEPS = "ProfileSteps";
  private static final Logger LOGGER = LoggerFactory.getLogger( ProfileTransformationPrepareExecution.class );
  private final ProfilingService profilingService;
  private final StreamingProfileService streamingProfileService;
  private final AggregateProfileService aggregateProfileService;
  private final ConcurrentMap<String, AtomicLong> runNumbers;

  public ProfileTransformationPrepareExecution( ProfilingService profilingService,
                                                StreamingProfileService streamingProfileService,
                                                AggregateProfileService aggregateProfileService ) {
    this.profilingService = profilingService;
    this.streamingProfileService = streamingProfileService;
    this.aggregateProfileService = aggregateProfileService;
    runNumbers = new ConcurrentHashMap<String, AtomicLong>();
  }

  private String handleStepMetaDataCombi( String transName, StepMetaDataCombi stepMetaDataCombi )
    throws ProfileCreationException {
    ProfileStatusManager profileStatusManager = profilingService.create( new ProfileConfiguration(
      new StreamingProfileMetadata( transName + "." + stepMetaDataCombi.stepname + "[" + stepMetaDataCombi.copy + "]" ),
      null, null ) );
    String profileId = profileStatusManager.getId();
    stepMetaDataCombi.step.addRowListener(
      new ProfileTransformationRowListener( streamingProfileService.getStreamingProfile( profileId ) ) );
    return profileId;
  }

  @Override public void callExtensionPoint( LogChannelInterface logChannelInterface, Object o ) throws KettleException {
    Trans trans = (Trans) o;
    String[] parameters = trans.listParameters();
    if ( parameters == null ) {
      return;
    }
    boolean hasParam = false;
    for ( String parameter : parameters ) {
      if ( PROFILE_STEPS.equals( parameter ) ) {
        hasParam = true;
        break;
      }
    }
    if ( !hasParam ) {
      return;
    }
    Set<String> steps =
      new HashSet<String>( Arrays.asList( trans.getParameterValue( PROFILE_STEPS ).split( "," ) ) );
    if ( steps.size() == 0 ) {
      return;
    }
    String transName = trans.getName();
    runNumbers.putIfAbsent( transName, new AtomicLong( 1L ) );
    String transNameWithRun = transName + "[" + runNumbers.get( transName ).getAndIncrement() + "]";
    Map<String, List<StepMetaDataCombi>> stepMetaDataCombiMap = new HashMap<String, List<StepMetaDataCombi>>();
    for ( StepMetaDataCombi stepMetaDataCombi : trans.getSteps() ) {
      if ( steps.contains( stepMetaDataCombi.stepname ) ) {
        List<StepMetaDataCombi> stepMetaDataCombis = stepMetaDataCombiMap.get( stepMetaDataCombi.stepname );
        if ( stepMetaDataCombis == null ) {
          stepMetaDataCombis = new ArrayList<StepMetaDataCombi>();
          stepMetaDataCombiMap.put( stepMetaDataCombi.stepname, stepMetaDataCombis );
        }
        stepMetaDataCombis.add( stepMetaDataCombi );
      }
    }
    final List<String> allIds = new ArrayList<String>();
    for ( Map.Entry<String, List<StepMetaDataCombi>> stepMetaDataCombisEntry : stepMetaDataCombiMap.entrySet() ) {
      List<StepMetaDataCombi> stepMetaDataCombis = stepMetaDataCombisEntry.getValue();
      Collections.sort( stepMetaDataCombis, new Comparator<StepMetaDataCombi>() {
        @Override public int compare( StepMetaDataCombi o1, StepMetaDataCombi o2 ) {
          return o1.copy - o2.copy;
        }
      } );
      List<String> streamingIds = new ArrayList<String>();
      for ( StepMetaDataCombi stepMetaDataCombi : stepMetaDataCombis ) {
        try {
          streamingIds.add( handleStepMetaDataCombi( transNameWithRun, stepMetaDataCombi ) );
        } catch ( ProfileCreationException e ) {
          LOGGER.error( "Unable to create streaming profile for " + stepMetaDataCombi, e );
        }
      }
      allIds.addAll( streamingIds );
      String stepProfileId;
      if ( streamingIds.size() == 1 ) {
        stepProfileId = streamingIds.get( 0 );
      } else {
        try {
          stepProfileId = profilingService.create(
            new ProfileConfiguration( new AggregateProfileMetadata( stepMetaDataCombisEntry.getKey() ), null, null ) )
            .getId();
          allIds.add( stepProfileId );
          for ( String streamingId : streamingIds ) {
            aggregateProfileService.addChild( stepProfileId, streamingId );
          }
        } catch ( ProfileCreationException e ) {
          LOGGER.error( "Unable to create aggregate profile for " + stepMetaDataCombisEntry.getKey(), e );
        }
      }
      // TODO: Set these ids somewhere so we can retrieve them
    }
    trans.addTransListener( new TransListener() {
      @Override public void transStarted( Trans trans ) throws KettleException {

      }

      @Override public void transActive( Trans trans ) {

      }

      @Override public void transFinished( Trans trans ) throws KettleException {
        for ( String id : allIds ) {
          profilingService.stop( id );
        }
      }
    } );
  }
}
