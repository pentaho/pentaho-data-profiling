package com.pentaho.profiling.api.metrics;

import com.pentaho.profiling.api.MessageUtils;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.metrics.field.DataSourceField;
import com.pentaho.profiling.api.stats.Statistic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Created by bryan on 2/9/15.
 */
public class ProfileFieldProperties {
  public static final String KEY = "profiling-metrics-api";
  public static final String KEY_PATH = MessageUtils.getId( KEY, ProfileFieldProperties.class );
  public static final ProfileFieldProperty LOGICAL_NAME = new ProfileFieldProperty( KEY_PATH,
    DataSourceField.LOGICAL_NAME, Arrays.asList( DataSourceField.LOGICAL_NAME ) );
  public static final ProfileFieldProperty PHYSICAL_NAME = new ProfileFieldProperty( KEY_PATH,
    DataSourceField.PHYSICAL_NAME, Arrays.asList( DataSourceField.PHYSICAL_NAME ) );
  public static final ProfileFieldProperty FIELD_TYPE =
    new ProfileFieldProperty( KEY_PATH, DataSourceField.TYPE_NAME, Arrays.asList( DataSourceField.TYPE, DataSourceField.TYPE_NAME ) );
  public static final ProfileFieldProperty COUNT_FIELD = new ProfileFieldProperty( KEY_PATH, Statistic.COUNT,
    Arrays.asList( DataSourceField.TYPE, Statistic.COUNT ) );
}
