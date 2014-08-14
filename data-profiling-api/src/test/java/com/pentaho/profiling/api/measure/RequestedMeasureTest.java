package com.pentaho.profiling.api.measure;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * Created by bryan on 8/14/14.
 */
public class RequestedMeasureTest {
  @Test
  public void testSetParameterNameToValueMap() {
    Map<String, String> parameterNameToValueMap = new HashMap<String, String>(  );
    parameterNameToValueMap.put( "KEY_1", "VALUE_1" );
    RequestedMeasure requestedMeasure = new RequestedMeasure();
    requestedMeasure.setParameterNameToValueMap( parameterNameToValueMap );
    assertEquals( parameterNameToValueMap, requestedMeasure.getParameterNameToValueMap() );
  }

  @Test
  public void testSetName() {
    String name = "NAME";
    RequestedMeasure requestedMeasure = new RequestedMeasure();
    requestedMeasure.setName( name );
    assertEquals( name, requestedMeasure.getName() );
  }
}
