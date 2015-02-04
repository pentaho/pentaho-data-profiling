package com.pentaho.profiling.api.metrics;

import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.action.ProfileActionException;

import java.util.List;
import java.util.Set;

/**
 * Interface to allow for easy implementation of metric contributors that only care about one field (and one type) at a
 * time
 */
public interface MetricManagerContributor {
  /**
   * Get the types that this contributor can process
   *
   * @return the types that this contributor can process
   */
  public Set<String> getTypes();

  /**
   * Integrate the DataSourceFieldValue into the metric calculated in the DataSourceMetricManager
   *
   * @param dataSourceMetricManager the DataSourceFieldValue
   * @param dataSourceFieldValue    the DataSourceMetricManager
   * @throws ProfileActionException
   */
  public void process( DataSourceMetricManager dataSourceMetricManager, DataSourceFieldValue dataSourceFieldValue )
    throws ProfileActionException;

  /**
   * Clears state created by this contributor
   *
   * @param dataSourceMetricManager the DataSourceMetricManager to clear
   */
  public void clear( DataSourceMetricManager dataSourceMetricManager );

  /**
   * Merges the DataSourceMetricManager from into the DataSourceMetricManager into (This operation modifies the into
   * argument)
   *
   * @param into the DataSourceMetricManager to merge into
   * @param from the DataSourceMetricManager to merge from
   * @throws MetricMergeException
   */
  public void merge( DataSourceMetricManager into, DataSourceMetricManager from ) throws MetricMergeException;

  /**
   * Gets the ProfileFieldProperty objects that describe what the contributor is contributing
   * @return the ProfileFieldProperty objects that describe what the contributor is contributing
   */
  public List<ProfileFieldProperty> getProfileFieldProperties();
}
