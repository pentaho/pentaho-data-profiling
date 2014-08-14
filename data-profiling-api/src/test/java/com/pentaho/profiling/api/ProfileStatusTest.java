package com.pentaho.profiling.api;

import com.pentaho.profiling.api.datasource.DataSourceReference;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * Created by bryan on 8/14/14.
 */
public class ProfileStatusTest {
  @Test
  public void testSetId() {
    String id = "ID_VALUE";
    ProfileStatus profileStatus = new ProfileStatus();
    profileStatus.setId( id );
    assertEquals( id, profileStatus.getId() );
  }

  @Test
  public void testSetDataSourceReference() {
    String id = "ID_VALUE";
    String dataSourcePovider = "PROVIDER_VALUE";
    ProfileStatus profileStatus = new ProfileStatus();
    DataSourceReference dataSourceReference = new DataSourceReference( id, dataSourcePovider );
    profileStatus.setDataSourceReference( dataSourceReference );
    assertEquals( dataSourceReference, profileStatus.getDataSourceReference() );
  }

  @Test
  public void testSetFields() {
    ProfilingField profilingField = new ProfilingField();
    profilingField.setName( "TEST" );
    List<ProfilingField> fields = new ArrayList<ProfilingField>( Arrays.asList( profilingField ) );
    ProfileStatus profileStatus = new ProfileStatus();
    profileStatus.setFields( fields );
    assertEquals( fields, profileStatus.getFields() );
  }

  @Test
  public void testSetTotalEntries() {
    Long totalEntries = 101L;
    ProfileStatus profileStatus = new ProfileStatus();
    profileStatus.setTotalEntities( totalEntries );
    assertEquals( totalEntries, profileStatus.getTotalEntities() );
  }
}
