package com.pentaho.profiling.api;

import com.pentaho.profiling.api.datasource.DataSourceReference;

/**
 * Created by bryan on 2/9/15.
 */
public interface ProfileFactory {
  public boolean accepts( DataSourceReference dataSourceReference );

  public Profile create( DataSourceReference dataSourceReference,
                         ProfileStatusManager profileStatusManager );
}
