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

package com.pentaho.profiling.model;

import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileState;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusMessage;
import com.pentaho.profiling.api.ProfileStatusReadOperation;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.ProfilingField;
import com.pentaho.profiling.api.action.ProfileActionExceptionWrapper;
import com.pentaho.profiling.api.datasource.DataSourceReference;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by bryan on 9/29/14.
 */
public class ProfileStatusManagerImpl implements ProfileStatusManager {
  private final ProfilingServiceImpl profilingService;
  private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
  private ProfileStatusImpl profileStatus;

  public ProfileStatusManagerImpl( String id, DataSourceReference dataSourceReference,
                                   ProfilingServiceImpl profilingService ) {
    this.profilingService = profilingService;
    profileStatus = new ProfileStatusImpl( id, dataSourceReference );
  }

  @Override public <T> T read( ProfileStatusReadOperation<T> profileStatusReadOperation ) {
    readWriteLock.readLock().lock();
    try {
      return profileStatusReadOperation.read( profileStatus );
    } finally {
      readWriteLock.readLock().unlock();
    }
  }

  @Override public <T> T write( ProfileStatusWriteOperation<T> profileStatusWriteOperation ) {
    readWriteLock.writeLock().lock();
    try {
      MutableProfileStatusImpl newStatus = new MutableProfileStatusImpl( profileStatus );
      T result = profileStatusWriteOperation.write( newStatus );
      profileStatus = new ProfileStatusImpl( newStatus );
      profilingService.notify( profileStatus );
      return result;
    } finally {
      readWriteLock.writeLock().unlock();
    }
  }

  @Override public ProfileState getProfileState() {
    readWriteLock.readLock().lock();
    try {
      return profileStatus.getProfileState();
    } finally {
      readWriteLock.readLock().unlock();
    }
  }

  @Override public String getId() {
    readWriteLock.readLock().lock();
    try {
      return profileStatus.getId();
    } finally {
      readWriteLock.readLock().unlock();
    }
  }

  @Override public DataSourceReference getDataSourceReference() {
    readWriteLock.readLock().lock();
    try {
      return profileStatus.getDataSourceReference();
    } finally {
      readWriteLock.readLock().unlock();
    }
  }

  @Override public List<ProfilingField> getFields() {
    readWriteLock.readLock().lock();
    try {
      return profileStatus.getFields();
    } finally {
      readWriteLock.readLock().unlock();
    }
  }

  @Override public Long getTotalEntities() {
    readWriteLock.readLock().lock();
    try {
      return profileStatus.getTotalEntities();
    } finally {
      readWriteLock.readLock().unlock();
    }
  }

  @Override public List<ProfileStatusMessage> getStatusMessages() {
    readWriteLock.readLock().lock();
    try {
      return profileStatus.getStatusMessages();
    } finally {
      readWriteLock.readLock().unlock();
    }
  }

  @Override public ProfileActionExceptionWrapper getOperationError() {
    readWriteLock.readLock().lock();
    try {
      return profileStatus.getOperationError();
    } finally {
      readWriteLock.readLock().unlock();
    }
  }

  @Override public List<ProfileFieldProperty> getProfileFieldProperties() {
    readWriteLock.readLock().lock();
    try {
      return profileStatus.getProfileFieldProperties();
    } finally {
      readWriteLock.readLock().unlock();
    }
  }

  @Override public long getSequenceNumber() {
    readWriteLock.readLock().lock();
    try {
      return profileStatus.getSequenceNumber();
    } finally {
      readWriteLock.readLock().unlock();
    }
  }
}
