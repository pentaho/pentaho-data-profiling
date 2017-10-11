/*******************************************************************************
 * Pentaho Data Profiling
 * <p/>
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 * <p/>
 * ******************************************************************************
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.pentaho.plugin.integration.extension;

import org.pentaho.profiling.api.AggregateProfileService;
import org.pentaho.profiling.api.ProfileCreationException;
import org.pentaho.profiling.api.ProfileStatusManager;
import org.pentaho.profiling.api.ProfilingService;
import org.pentaho.profiling.api.StreamingProfileService;
import org.pentaho.profiling.api.configuration.ProfileConfiguration;
import org.pentaho.profiling.api.configuration.core.AggregateProfileMetadata;
import org.pentaho.profiling.api.configuration.core.StreamingProfileMetadata;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransListener;
import org.pentaho.di.trans.step.StepMetaDataCombi;

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
 * This is an example extension point that illustrates how to attach to a transformation and profile the rows that come
 * off of a given step
 * <p/>
 * The extension point annotation gives Kettle the information it needs to register us as a plugin (with the
 * pdi-osgi-bridge's help)
 */
@ExtensionPoint( id = "profileTransformationPrepareExecution", extensionPointId = "TransformationStartThreads",
  description = "This will register row listeners for streaming profiles on the specified steps" )
public class ProfileTransformationPrepareExecution implements ExtensionPointInterface {
  public static final String PROFILE_STEPS = "ProfileSteps";
  private final ProfilingService profilingService;
  private final StreamingProfileService streamingProfileService;
  private final AggregateProfileService aggregateProfileService;
  private final ConcurrentMap<String, AtomicLong> runNumbers;

  /**
   * This constructor is called via the blueprint.xml and injects the extension point's dependencies
   *
   * @param profilingService        the profiling service
   * @param streamingProfileService the streaming profile service
   * @param aggregateProfileService the aggregate profile service
   */
  public ProfileTransformationPrepareExecution( ProfilingService profilingService,
                                                StreamingProfileService streamingProfileService,
                                                AggregateProfileService aggregateProfileService ) {
    this.profilingService = profilingService;
    this.streamingProfileService = streamingProfileService;
    this.aggregateProfileService = aggregateProfileService;
    runNumbers = new ConcurrentHashMap<String, AtomicLong>();
  }

  /**
   * Create the streaming profile for the step copy (because multiple copies of the step could be running, we need a
   * profile for each, we will create an aggregate of those)
   *
   * @param logChannelInterface the log channel
   * @param transName           the transformation name
   * @param stepMetaDataCombi   the stepMetaDataCombi
   * @return the profileId
   * @throws ProfileCreationException
   */
  private String handleStepMetaDataCombi( LogChannelInterface logChannelInterface, String transName,
                                          StepMetaDataCombi stepMetaDataCombi )
    throws ProfileCreationException {
    ProfileStatusManager profileStatusManager = profilingService.create( new ProfileConfiguration(
      new StreamingProfileMetadata( transName + "." + stepMetaDataCombi.stepname + "[" + stepMetaDataCombi.copy + "]" ),
      null, null ) );
    String profileId = profileStatusManager.getId();
    stepMetaDataCombi.step.addRowListener(
      new ProfileTransformationRowListener( logChannelInterface,
        streamingProfileService.getStreamingProfile( profileId ) ) );
    return profileId;
  }

  /**
   * Extension point interface method.  This will be called with the logChannelInterface we're supposed to log to as
   * well as a reference to the trans
   *
   * @param logChannelInterface the logChannelInterface we're supposed to log to
   * @param o                   the trans
   * @throws KettleException
   */
  @Override public void callExtensionPoint( LogChannelInterface logChannelInterface, Object o ) throws KettleException {
    Trans trans = (Trans) o;
    String[] parameters = trans.listParameters();
    if ( parameters == null ) {
      return;
    }
    boolean hasParam = false;
    // Here we're allowing the user to specify which steps to profile via a parameter called "ProfileSteps", this
    // could use any other logic to figure out what's worth profiling
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
      // Create streamingProfiles for all copies of all steps we want to profile
      for ( StepMetaDataCombi stepMetaDataCombi : stepMetaDataCombis ) {
        try {
          streamingIds.add( handleStepMetaDataCombi( logChannelInterface, transNameWithRun, stepMetaDataCombi ) );
        } catch ( ProfileCreationException e ) {
          logChannelInterface.logError( "Unable to create streaming profile for " + stepMetaDataCombi, e );
        }
      }
      allIds.addAll( streamingIds );
      String stepProfileId;
      if ( streamingIds.size() == 1 ) {
        stepProfileId = streamingIds.get( 0 );
      } else {
        // Create an aggregate if there was more than one copy and add all the copies to it as children
        try {
          stepProfileId = profilingService.create(
            new ProfileConfiguration( new AggregateProfileMetadata( stepMetaDataCombisEntry.getKey() ), null, null ) )
            .getId();
          allIds.add( stepProfileId );
          for ( String streamingId : streamingIds ) {
            aggregateProfileService.addChild( stepProfileId, streamingId );
          }
        } catch ( ProfileCreationException e ) {
          logChannelInterface.logError( "Unable to create aggregate profile for " + stepMetaDataCombisEntry.getKey(),
            e );
        }
      }
      // You have all the profile ids here, you could put them somewhere and open up the profiling application for
      // the given profiles elsewhere.
    }
    // Stop all the profiles when the trans is done
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
