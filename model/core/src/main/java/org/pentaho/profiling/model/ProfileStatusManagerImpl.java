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

package org.pentaho.profiling.model;

import org.pentaho.profiling.api.IllegalTransactionException;
import org.pentaho.profiling.api.MutableProfileStatus;
import org.pentaho.profiling.api.ProfileField;
import org.pentaho.profiling.api.ProfileFieldProperty;
import org.pentaho.profiling.api.ProfileState;
import org.pentaho.profiling.api.ProfileStatus;
import org.pentaho.profiling.api.ProfileStatusManager;
import org.pentaho.profiling.api.ProfileStatusMessage;
import org.pentaho.profiling.api.ProfileStatusReadOperation;
import org.pentaho.profiling.api.ProfileStatusWriteOperation;
import org.pentaho.profiling.api.action.ProfileActionExceptionWrapper;
import org.pentaho.profiling.api.configuration.ProfileConfiguration;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by bryan on 9/29/14.
 */
public class ProfileStatusManagerImpl implements ProfileStatusManager {
  private final ProfilingServiceImpl profilingService;
  private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
  private final Semaphore transactionSemaphore = new Semaphore( 1 );
  private MutableProfileStatus transaction;
  private ProfileStatusImpl profileStatus;

  public ProfileStatusManagerImpl( String id, String name, ProfileConfiguration profileConfiguration,
                                   ProfilingServiceImpl profilingService ) {
    this.profilingService = profilingService;
    profileStatus = new ProfileStatusImpl( id, name, profileConfiguration );
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
    transactionSemaphore.acquireUninterruptibly();
    readWriteLock.writeLock().lock();
    try {
      MutableProfileStatusImpl newStatus = new MutableProfileStatusImpl( profileStatus );
      T result = profileStatusWriteOperation.write( newStatus );
      profileStatus = new ProfileStatusImpl( newStatus );
      profilingService.notify( profileStatus );
      return result;
    } finally {
      readWriteLock.writeLock().unlock();
      transactionSemaphore.release();
    }
  }

  @Override public MutableProfileStatus startTransaction() throws IllegalTransactionException {
    transactionSemaphore.acquireUninterruptibly();
    if ( transaction != null ) {
      throw new IllegalTransactionException(
        "Tried to create transaction when there was already an active one: " + transaction );
    }
    transaction = read( new ProfileStatusReadOperation<MutableProfileStatus>() {
      @Override public MutableProfileStatus read( ProfileStatus profileStatus ) {
        return new MutableProfileStatusImpl( profileStatus );
      }
    } );
    return transaction;
  }

  @Override public void commit( MutableProfileStatus mutableProfileStatus ) throws IllegalTransactionException {
    readWriteLock.writeLock().lock();
    try {
      if ( transaction == null || transaction != mutableProfileStatus ) {
        throw new IllegalTransactionException( "Tried to commit non-active transaction: " + mutableProfileStatus );
      }
      profileStatus = new ProfileStatusImpl( transaction );
      transaction = null;
      profilingService.notify( profileStatus );
      transactionSemaphore.release();
    } finally {
      readWriteLock.writeLock().unlock();
    }
  }

  @Override public void abort( MutableProfileStatus mutableProfileStatus ) throws IllegalTransactionException {
    if ( transaction == mutableProfileStatus ) {
      transaction = null;
      transactionSemaphore.release();
    } else {
      throw new IllegalTransactionException( "Tried to abort non-active transaction: " + mutableProfileStatus );
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

  @Override public String getName() {
    return profileStatus.getName();
  }

  @Override public ProfileConfiguration getProfileConfiguration() {
    readWriteLock.readLock().lock();
    try {
      return profileStatus.getProfileConfiguration();
    } finally {
      readWriteLock.readLock().unlock();
    }
  }

  @Override public List<ProfileField> getFields() {
    readWriteLock.readLock().lock();
    try {
      return profileStatus.getFields();
    } finally {
      readWriteLock.readLock().unlock();
    }
  }

  @Override public ProfileField getField( String physicalName ) {
    readWriteLock.readLock().lock();
    try {
      return profileStatus.getField( physicalName );
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
