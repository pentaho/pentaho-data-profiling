/*******************************************************************************
 * Pentaho Data Profiling
 * <p/>
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
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

import org.pentaho.plugin.util.DataSourceFieldValueCreator;
import org.pentaho.profiling.api.StreamingProfile;
import org.pentaho.profiling.api.action.ProfileActionException;
import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.RowListener;

import java.util.ArrayList;
import java.util.List;

/**
 * RowListener that is responsible for converting Kettle rows to lists of DataSourceFieldValues
 */
public class ProfileTransformationRowListener implements RowListener {
  public static final String UNABLE_TO_PROCESS_RECORD = "Unable to process record ";
  private final LogChannelInterface logChannelInterface;
  private final StreamingProfile streamingProfile;
  private final List<DataSourceFieldValue> list;
  private final DataSourceFieldValueCreator dataSourceFieldValueCreator;

  /**
   * Creates the row listener for the given logChannelInterface and streamingProfile
   *
   * @param logChannelInterface
   * @param streamingProfile
   */
  public ProfileTransformationRowListener( LogChannelInterface logChannelInterface,
                                           StreamingProfile streamingProfile ) {
    this( logChannelInterface, streamingProfile, new DataSourceFieldValueCreator(),
      new ArrayList<DataSourceFieldValue>() );
  }

  /**
   * Creates the row listener for the given logChannelInterface and streamingProfile allowing the user to specify logic
   * used to create the dataSourceFieldValues as well as the list to hold them
   *
   * @param logChannelInterface
   * @param streamingProfile
   * @param dataSourceFieldValueCreator
   * @param list
   */
  public ProfileTransformationRowListener( LogChannelInterface logChannelInterface,
                                           StreamingProfile streamingProfile,
                                           DataSourceFieldValueCreator dataSourceFieldValueCreator,
                                           List<DataSourceFieldValue> list ) {
    this.logChannelInterface = logChannelInterface;
    this.streamingProfile = streamingProfile;
    this.dataSourceFieldValueCreator = dataSourceFieldValueCreator;
    this.list = list;
  }

  @Override public void rowReadEvent( RowMetaInterface rowMeta, Object[] row ) throws KettleStepException {

  }

  /**
   * Transforms the row into a list of dataSourceFieldValues and sends them through the streaming profile
   *
   * @param rowMeta the row meta
   * @param row     the row
   * @throws KettleStepException
   */
  @Override public synchronized void rowWrittenEvent( RowMetaInterface rowMeta, Object[] row )
    throws KettleStepException {
    list.clear();
    dataSourceFieldValueCreator.createDataSourceFields( list, rowMeta, row );
    try {
      streamingProfile.processRecord( list );
    } catch ( ProfileActionException e ) {
      logChannelInterface.logError( UNABLE_TO_PROCESS_RECORD + list, e );
    }
  }

  @Override public void errorRowWrittenEvent( RowMetaInterface rowMeta, Object[] row ) throws KettleStepException {

  }
}
