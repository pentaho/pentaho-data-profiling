package com.pentaho.profiling.api.metrics.univariate;

import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricContributor;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceField;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldManager;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import com.pentaho.profiling.api.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by bryan on 2/3/15.
 */
public class MetricManagerBasedMetricContributor implements MetricContributor {
  private static final Logger LOGGER = LoggerFactory.getLogger( MetricManagerBasedMetricContributor.class );
  private final List<MetricManagerContributor> metricManagerContributors = new ArrayList<MetricManagerContributor>();
  private final Map<String, List<MetricManagerContributor>> metricManagerContributorMap = new HashMap<String,
    List<MetricManagerContributor>>();
  private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

  @Override public void processFields( DataSourceFieldManager manager, List<DataSourceFieldValue> value )
    throws ProfileActionException {
    Map<String, List<Pair<DataSourceField, DataSourceFieldValue>>> valuesByType =
      new HashMap<String, List<Pair<DataSourceField, DataSourceFieldValue>>>();
    for ( DataSourceFieldValue dataSourceFieldValue : value ) {
      DataSourceField dataSourceField =
        manager.getPathToDataSourceFieldMap().get( dataSourceFieldValue.getPhysicalName() );
      if ( dataSourceField == null ) {
        LOGGER.warn( "Got DataSourceFieldValue for nonexistent field " + dataSourceFieldValue.getPhysicalName() );
      } else {
        Object fieldValue = dataSourceFieldValue.getFieldValue();
        String typeName = fieldValue == null ? "null" : fieldValue.getClass().getCanonicalName();
        List<Pair<DataSourceField, DataSourceFieldValue>> values = valuesByType.get( typeName );
        if ( values == null ) {
          values = new ArrayList<Pair<DataSourceField, DataSourceFieldValue>>();
          valuesByType.put( typeName, values );
        }
        values.add( Pair.of( dataSourceField, dataSourceFieldValue ) );
      }
    }
    Lock readLock = readWriteLock.readLock();
    readLock.lock();
    try {
      for ( Map.Entry<String, List<Pair<DataSourceField, DataSourceFieldValue>>> entry : valuesByType.entrySet() ) {
        String typeName = entry.getKey();
        List<MetricManagerContributor> metricManagerContributors = metricManagerContributorMap.get( typeName );
        if ( metricManagerContributors != null ) {
          for ( Pair<DataSourceField, DataSourceFieldValue> dataSourceFieldAndValue : entry.getValue() ) {
            for ( MetricManagerContributor metricManagerContributor : metricManagerContributors ) {
              DataSourceMetricManager metricManagerForType = dataSourceFieldAndValue.getFirst().getMetricManagerForType(
                typeName );
              if ( metricManagerForType != null ) {
                metricManagerContributor.process( metricManagerForType, dataSourceFieldAndValue.getSecond() );
              }
            }
          }
        }
      }
    } finally {
      readLock.unlock();
    }
  }

  @Override public void merge( DataSourceFieldManager existing, DataSourceFieldManager update )
    throws MetricMergeException {
    for ( DataSourceField firstDataSourceField : existing.getDataSourceFields() ) {
      String physicalName = firstDataSourceField.getPhysicalName();
      DataSourceField secondDataSourceField = update.getPathToDataSourceFieldMap().get( physicalName );
      if ( secondDataSourceField != null ) {
        Lock readLock = readWriteLock.readLock();
        readLock.lock();
        try {
          for ( MetricManagerContributor metricManagerContributor : metricManagerContributors ) {
            for ( String typeName : metricManagerContributor.getTypes() ) {
              DataSourceMetricManager secondMetricManager = secondDataSourceField.getMetricManagerForType( typeName );
              if ( secondMetricManager != null ) {
                metricManagerContributor
                  .merge( firstDataSourceField.getMetricManagerForType( typeName, true ), secondMetricManager );
              }
            }
          }
        } finally {
          readLock.unlock();
        }
      }
    }
  }

  @Override public void clearProfileStatus( MutableProfileStatus mutableProfileStatus ) {
    DataSourceFieldManager dataSourceProfilingFieldManager =
      new DataSourceFieldManager( mutableProfileStatus.getFields() );
    Lock readLock = readWriteLock.readLock();
    readLock.lock();
    try {
      for ( MetricManagerContributor metricManagerContributor : metricManagerContributors ) {
        for ( String typeName : metricManagerContributor.getTypes() ) {
          for ( DataSourceMetricManager dsf : dataSourceProfilingFieldManager
            .getPathToMetricManagerForTypeMap( typeName )
            .values() ) {
            metricManagerContributor.clear( dsf );
          }
        }
      }
    } finally {
      readLock.unlock();
    }
  }

  @Override public List<ProfileFieldProperty> getProfileFieldProperties() {
    Set<ProfileFieldProperty> addedProperties = new HashSet<ProfileFieldProperty>();
    List<ProfileFieldProperty> result = new ArrayList<ProfileFieldProperty>();
    Lock readLock = readWriteLock.readLock();
    readLock.lock();
    try {
      for ( MetricManagerContributor metricManagerContributor : metricManagerContributors ) {
        for ( ProfileFieldProperty profileFieldProperty : metricManagerContributor.getProfileFieldProperties() ) {
          if ( addedProperties.add( profileFieldProperty ) ) {
            result.add( profileFieldProperty );
          }
        }
      }
    } finally {
      readLock.unlock();
    }
    return result;
  }

  public void implAdded( MetricManagerContributor metricManagerContributor, Map properties ) {
    Lock writeLock = readWriteLock.writeLock();
    writeLock.lock();
    try {
      metricManagerContributors.add( metricManagerContributor );
      for ( String typeName : metricManagerContributor.getTypes() ) {
        List<MetricManagerContributor> metricManagerContributorsForType = metricManagerContributorMap.get( typeName );
        if ( metricManagerContributorsForType == null ) {
          metricManagerContributorsForType = new ArrayList<MetricManagerContributor>();
          metricManagerContributorMap.put( typeName, metricManagerContributorsForType );
        }
        metricManagerContributorsForType.add( metricManagerContributor );
      }
    } finally {
      writeLock.unlock();
    }
  }

  public void implRemoved( MetricManagerContributor metricManagerContributor, Map properties ) {
    Lock writeLock = readWriteLock.writeLock();
    writeLock.lock();
    try {
      metricManagerContributors.remove( metricManagerContributor );
      for ( String typeName : metricManagerContributor.getTypes() ) {
        List<MetricManagerContributor> metricManagerContributorsForType = metricManagerContributorMap.get( typeName );
        if ( metricManagerContributorsForType != null ) {
          metricManagerContributorsForType.remove( metricManagerContributor );
          if ( metricManagerContributorsForType.size() == 0 ) {
            metricManagerContributorMap.remove( typeName );
          }
        }
      }
    } finally {
      writeLock.unlock();
    }
  }
}
