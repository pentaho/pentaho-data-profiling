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

package com.pentaho.profiling.api.operations;

import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.action.ProfileAction;
import com.pentaho.profiling.api.action.ProfileActionExecutor;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by bryan on 9/8/14.
 */
@XmlRootElement
public abstract class ProfileOperationImpl implements ProfileOperation {
  private final AtomicBoolean running;
  private String id;
  private String namePath;
  private String nameKey;
  private ProfileStatusManager profileStatusManager;

  public ProfileOperationImpl() {
    this( null, null, null, null );
  }

  public ProfileOperationImpl( String id, String namePath, String nameKey, ProfileStatusManager profileStatusManager ) {
    this.id = id;
    this.namePath = namePath;
    this.nameKey = nameKey;
    this.profileStatusManager = profileStatusManager;
    running = new AtomicBoolean( false );
  }

  @Override public String getNamePath() {
    return namePath;
  }

  public void setNamePath( String namePath ) {
    this.namePath = namePath;
  }

  @Override public String getNameKey() {
    return nameKey;
  }

  public void setNameKey( String nameKey ) {
    this.nameKey = nameKey;
  }

  @Override public String getId() {
    return id;
  }

  public void setId( String id ) {
    this.id = id;
  }

  @Override public boolean isRunning() {
    return running.get();
  }

  @Override public void start( ProfileActionExecutor profileActionExecutor ) {
    running.set( true );
    final ProfileAction action = getNext();
    profileStatusManager.write( new ProfileStatusWriteOperation<Void>() {
      @Override public Void write( MutableProfileStatus profileStatus ) {
        profileStatus.setOperationError( null );
        profileStatus.setCurrentOperationMessage( action.getCurrentOperationMessage() );
        return null;
      }
    } );
    profileActionExecutor.submit( action,
        new ProfileOperationActionExecutionCallback( this, profileActionExecutor, profileStatusManager, running ) );
  }

  @Override public void stop() {
    profileStatusManager.write( new ProfileStatusWriteOperation<Void>() {
      @Override public Void write( MutableProfileStatus profileStatus ) {
        profileStatus.setCurrentOperationMessage( null );
        running.set( false );
        return null;
      }
    } );
    resetState();
  }

  protected abstract ProfileAction getNext();

  protected abstract void resetState();
}
