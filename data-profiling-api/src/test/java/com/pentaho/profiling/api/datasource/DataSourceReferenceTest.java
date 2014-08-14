package com.pentaho.profiling.api.datasource;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by bryan on 8/11/14.
 */
public class DataSourceReferenceTest {
  @Test
  public void testNoArgConstructorSetsNullValues() {
    DataSourceReference dataSourceReference = new DataSourceReference(  );
    assertEquals( null, dataSourceReference.getId() );
    assertEquals( null, dataSourceReference.getDataSourceProvider() );
  }

  @Test
  public void testTwoArgConstructorSetsValues() {
    String id = "VALUE_1";
    String dataSourceProvider = "VALUE_2";
    DataSourceReference dataSourceReference = new DataSourceReference( id, dataSourceProvider );
    assertEquals( id, dataSourceReference.getId() );
    assertEquals( dataSourceProvider, dataSourceReference.getDataSourceProvider() );
  }

  @Test
  public void testSetIdRoundTrip() {
    String id = "VALUE_1";
    DataSourceReference dataSourceReference = new DataSourceReference(  );
    dataSourceReference.setId( id );
    assertEquals( id, dataSourceReference.getId() );
  }

  @Test
  public void testSetDataSourceProviderRoundTrip() {
    String dataSourceId = "VALUE_1";
    DataSourceReference dataSourceReference = new DataSourceReference(  );
    dataSourceReference.setDataSourceProvider( dataSourceId );
    assertEquals( dataSourceId, dataSourceReference.getDataSourceProvider() );
  }

  @Test
  public void testSetId() {
    String id = "VALUE_1";
    DataSourceReference dataSourceReference = new DataSourceReference(  );
    dataSourceReference.setId( id );
    assertEquals( id, dataSourceReference.getId() );
  }

  @Test
  public void testSetDataSourceProvider() {
    String dataSourceProvider = "VALUE_1";
    DataSourceReference dataSourceReference = new DataSourceReference(  );
    dataSourceReference.setDataSourceProvider( dataSourceProvider );
    assertEquals( dataSourceProvider, dataSourceReference.getDataSourceProvider() );
  }
}
