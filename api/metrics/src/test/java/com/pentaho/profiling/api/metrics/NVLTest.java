package com.pentaho.profiling.api.metrics;

import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by bryan on 2/5/15.
 */
public class NVLTest {
  private NVL nvl;
  private NVLOperation<String> constantStringOperation;
  private final String constString = "don't return me";

  @Before
  public void setup() {
    nvl = new NVL();
    constantStringOperation = new NVLOperation<String>() {
      @Override public String perform( String first, String second ) {
        return constString;
      }
    };
  }

  @Test
  public void testPerformNullFirstAndSecond() {
    assertNull( nvl.perform( constantStringOperation, null, null ) );
  }

  @Test
  public void testPerformNullFirst() {
    String second = "second";
    assertEquals( second, nvl.perform( constantStringOperation, null, second ) );
  }

  @Test
  public void testPerformNullSecond() {
    String first = "first";
    assertEquals( first, nvl.perform( constantStringOperation, first, null ) );
  }

  @Test
  public void testPerformBoth() {
    String first = "first";
    String second = "second";
    assertEquals( constString, nvl.perform( constantStringOperation, first, second ) );
  }

  @Test
  public void testPerformAndSetValue() {
    DataSourceMetricManager dataSourceMetricManager = new DataSourceMetricManager();
    String testPath = "testPath";
    dataSourceMetricManager.setValue( "dummy", testPath );
    assertEquals( constString,
      nvl.performAndSet( constantStringOperation, dataSourceMetricManager, "fake", testPath ) );
    assertEquals( constString, dataSourceMetricManager.getValueNoDefault( testPath ) );
  }

  @Test
  public void testPerformAndSetManager() {
    DataSourceMetricManager dataSourceMetricManager = new DataSourceMetricManager();
    DataSourceMetricManager dataSourceMetricManager2 = new DataSourceMetricManager();
    String testPath = "testPath";
    dataSourceMetricManager.setValue( "dummy", testPath );
    dataSourceMetricManager2.setValue( "fake", testPath );
    assertEquals( constString,
      nvl.performAndSet( constantStringOperation, dataSourceMetricManager, dataSourceMetricManager2, testPath ) );
    assertEquals( constString, dataSourceMetricManager.getValueNoDefault( testPath ) );
  }
}
