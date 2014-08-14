package com.pentaho.profiling.api.measure;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * Created by bryan on 8/14/14.
 */
public class MeasureMetadataTest {
  @Test
  public void testSetParameterNameToTypeMap() {
    Map<String, ParameterType> parameterTypeMap = new HashMap<String, ParameterType>(  );
    parameterTypeMap.put( "TEST", ParameterType.LONG );
    MeasureMetadata measureMetadata = new MeasureMetadata();
    measureMetadata.setParameterNameToTypeMap( parameterTypeMap );
    assertEquals( parameterTypeMap, measureMetadata.getParameterNameToTypeMap() );
  }

  @Test
  public void testSetMeasureName() {
    String measureName = "VALUE_1";
    MeasureMetadata measureMetadata = new MeasureMetadata();
    measureMetadata.setMeasureName( measureName );
    assertEquals( measureName, measureMetadata.getMeasureName() );
  }
}
