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

package org.pentaho.plugin.util;

import org.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Class for turning a Kettle row into a list of dataSourceFieldValues
 */
public class DataSourceFieldValueCreator {
  public static final String UNABLE_TO_ADD_FIELD = "Unable to add field ";
  private static final Logger LOGGER = LoggerFactory.getLogger( DataSourceFieldValueCreator.class );

  /**
   * Converts the fields from the Kettle row to DataSourceFieldValues and adds them to the output list
   * @param outputList the output list
   * @param rowMetaInterface the row meta interface
   * @param objects the row
   */
  public void createDataSourceFields( List<DataSourceFieldValue> outputList, RowMetaInterface rowMetaInterface,
                                      Object[] objects ) {
    int index = 0;
    for ( ValueMetaInterface valueMetaInterface : rowMetaInterface.getValueMetaList() ) {
      try {
        DataSourceFieldValue dataSourceFieldValue =
          new DataSourceFieldValue( valueMetaInterface.getNativeDataType( objects[ index++ ] ) );
        String name = valueMetaInterface.getName();
        dataSourceFieldValue.setLogicalName( name );
        dataSourceFieldValue.setPhysicalName( name );
        outputList.add( dataSourceFieldValue );
      } catch ( KettleValueException e ) {
        LOGGER.error( UNABLE_TO_ADD_FIELD + valueMetaInterface.getName(), e );
      }
    }
  }
}
