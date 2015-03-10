/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2015 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */

package com.pentaho.model.metrics.contributor.metricManager.impl;

import com.pentaho.model.metrics.contributor.Constants;
import com.pentaho.profiling.api.MessageUtils;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricContributorUtils;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by mhall on 28/01/15.
 */
public class RegexAddressMetricContributor extends BaseMetricManagerContributor implements MetricManagerContributor {
  public static final String KEY_PATH =
    MessageUtils.getId( Constants.KEY, RegexAddressMetricContributor.class );
  public static final String EMAIL_ADDRESS_KEY = "com.pentaho.str.email_address";
  public static final ProfileFieldProperty EMAIL_ADDRESS_COUNT =
    MetricContributorUtils.createMetricProperty( KEY_PATH, "EmailAddressMetricContributor", EMAIL_ADDRESS_KEY );
  private static final String EMAIL_PATTERN =
    "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
  private Pattern pattern = Pattern.compile( EMAIL_PATTERN );
  private String regex = EMAIL_PATTERN;
  private ProfileFieldProperty profileFieldProperty = EMAIL_ADDRESS_COUNT;
  private String[] pathToValue = MetricContributorUtils.removeType( EMAIL_ADDRESS_COUNT.getPathToProperty() );
  private Set<String> supportedTypes = new HashSet<String>( Arrays.asList( String.class.getCanonicalName() ) );

  public ProfileFieldProperty getProfileFieldProperty() {
    return profileFieldProperty;
  }

  public void setProfileFieldProperty( ProfileFieldProperty profileFieldProperty ) {
    this.profileFieldProperty = profileFieldProperty;
    if ( profileFieldProperty != null ) {
      this.pathToValue = MetricContributorUtils.removeType( profileFieldProperty.getPathToProperty() );
    } else {
      this.pathToValue = null;
    }
  }

  public String getRegex() {
    return regex;
  }

  public void setRegex( String regex ) {
    this.regex = regex;
    pattern = Pattern.compile( regex );
  }

  @Override public Set<String> supportedTypes() {
    return new HashSet<String>( supportedTypes );
  }

  public Set<String> getSupportedTypes() {
    return supportedTypes;
  }

  public void setSupportedTypes( Set<String> supportedTypes ) {
    this.supportedTypes = supportedTypes;
  }

  @Override
  public void process( DataSourceMetricManager dataSourceMetricManager, DataSourceFieldValue dataSourceFieldValue )
    throws ProfileActionException {
    boolean match = pattern.matcher( dataSourceFieldValue.getFieldValue().toString() ).matches();
    if ( match ) {
      Long existingCount = dataSourceMetricManager.getValue( 0L, pathToValue );
      existingCount++;
      dataSourceMetricManager.setValue( existingCount, pathToValue );
    }
  }

  @Override public void merge( DataSourceMetricManager into, DataSourceMetricManager from )
    throws MetricMergeException {
    Long intoCount = into.getValue( (Number) 0L, pathToValue ).longValue();
    Long fromCount = from.getValue( (Number) 0L, pathToValue ).longValue();
    into.setValue( intoCount + fromCount, pathToValue );
  }

  @Override public List<ProfileFieldProperty> profileFieldProperties() {
    if ( profileFieldProperty == null ) {
      return null;
    }
    return Arrays.asList( profileFieldProperty );
  }

  @Override public void clear( DataSourceMetricManager dataSourceMetricManager ) {
    dataSourceMetricManager.clear( Arrays.<String[]>asList( pathToValue ) );
  }
}
