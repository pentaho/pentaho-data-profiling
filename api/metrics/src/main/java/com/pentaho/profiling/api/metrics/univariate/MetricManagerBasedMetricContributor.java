package com.pentaho.profiling.api.metrics.univariate;

import com.pentaho.profiling.api.metrics.MetricContributor;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceField;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldManager;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.action.ProfileActionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by bryan on 2/3/15.
 */
public class MetricManagerBasedMetricContributor implements MetricContributor {
  private static final Logger LOGGER = LoggerFactory.getLogger( MetricManagerBasedMetricContributor.class );
  private final MetricManagerContributor metricManagerContributor;

  public MetricManagerBasedMetricContributor( MetricManagerContributor metricManagerContributor ) {
    this.metricManagerContributor = metricManagerContributor;
  }

  @Override public void processFields( DataSourceFieldManager manager, List<DataSourceFieldValue> value )
    throws ProfileActionException {
    for ( DataSourceFieldValue dataSourceFieldValue : value ) {
      DataSourceField dataSourceField =
        manager.getPathToDataSourceFieldMap().get( dataSourceFieldValue.getPhysicalName() );
      if ( dataSourceField == null ) {
        LOGGER.warn( "Got DataSourceFieldValue for nonexistent field " + dataSourceFieldValue.getPhysicalName() );
      } else {
        Object fieldValue = dataSourceFieldValue.getFieldValue();
        String typeName = fieldValue == null ? "null" : fieldValue.getClass().getCanonicalName();
        if ( metricManagerContributor.getTypes().contains( typeName ) ) {
          DataSourceMetricManager metricManagerForType = dataSourceField.getMetricManagerForType( typeName );
          if ( metricManagerForType != null ) {
            metricManagerContributor.process( metricManagerForType, dataSourceFieldValue );
          }
        }
      }
    }
  }

  @Override public void merge( DataSourceFieldManager existing, DataSourceFieldManager update )
    throws MetricMergeException {
    for ( DataSourceField firstDataSourceField : existing.getDataSourceFields() ) {
      String physicalName = firstDataSourceField.getPhysicalName();
      DataSourceField secondDataSourceField = update.getPathToDataSourceFieldMap().get( physicalName );
      if ( secondDataSourceField != null ) {
        for ( String typeName : metricManagerContributor.getTypes() ) {
          DataSourceMetricManager secondMetricManager = secondDataSourceField.getMetricManagerForType( typeName );
          if ( secondMetricManager != null ) {
            metricManagerContributor
              .merge( firstDataSourceField.getMetricManagerForType( typeName, true ), secondMetricManager );
          }
        }
      }
    }
  }

  @Override public void clearProfileStatus( MutableProfileStatus mutableProfileStatus ) {
    DataSourceFieldManager dataSourceProfilingFieldManager =
      new DataSourceFieldManager( mutableProfileStatus.getFields() );
    for ( String typeName : metricManagerContributor.getTypes() ) {
      for ( DataSourceMetricManager dsf : dataSourceProfilingFieldManager.getPathToMetricManagerForTypeMap( typeName )
        .values() ) {
        metricManagerContributor.clear( dsf );
      }
    }
  }

  @Override public List<ProfileFieldProperty> getProfileFieldProperties() {
    return metricManagerContributor.getProfileFieldProperties();
  }
}
