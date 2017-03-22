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

package org.pentaho.profiling.api.metrics.field;

import org.pentaho.profiling.api.core.test.BeanTester;
import org.junit.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSettersExcluding;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Created by mhall on 28/01/15.
 */
public class DataSourceFieldValueTest extends BeanTester {

  public DataSourceFieldValueTest() {
    super( DataSourceFieldValue.class );
  }

  @Test
  public void testNullField() {
    DataSourceFieldValue val = new DataSourceFieldValue();
    assertNull( val.getFieldValue() );
    assertEquals( 0, val.numMetadataElements() );
  }

  @Test
  public void testNonNullField() {
    DataSourceFieldValue val = new DataSourceFieldValue( "test" );
    assertEquals( "test", val.getFieldValue() );
    val.setFieldValue( 2L );
    assertEquals( 2L, val.getFieldValue() );
  }

  @Test
  public void testSetMetadata() {
    DataSourceFieldValue val = new DataSourceFieldValue();
    String metadataKey = "some.meta.data";
    int metadataVal = 50;
    val.setFieldMetatdata( metadataKey, metadataVal );
    assertNotNull( val.getFieldMetadata( metadataKey ) );
    assertEquals( metadataVal, (int) val.getFieldMetadata( metadataKey ) );
    val.clearFieldMetadata();
    assertNull( val.getFieldMetadata( metadataKey ) );
  }
}
