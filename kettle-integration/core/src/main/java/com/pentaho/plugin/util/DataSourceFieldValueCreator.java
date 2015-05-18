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

package com.pentaho.plugin.util;

import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by bryan on 5/13/15.
 */
public class DataSourceFieldValueCreator {
  public static final String UNABLE_TO_ADD_FIELD = "Unable to add field ";
  private static final Logger LOGGER = LoggerFactory.getLogger( DataSourceFieldValueCreator.class );

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
