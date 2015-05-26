/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package com.pentaho.model.metrics.contributor.metricManager.impl;

import com.pentaho.model.metrics.contributor.Constants;
import com.pentaho.model.metrics.contributor.metricManager.impl.metrics.RegexHolder;
import com.pentaho.profiling.api.MessageUtils;
import com.pentaho.profiling.api.MutableProfileFieldValueType;
import com.pentaho.profiling.api.ProfileFieldProperty;
import com.pentaho.profiling.api.ProfileFieldValueType;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;

import java.util.ArrayList;
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
  public static final String EMAIL_ADDRESS_KEY = "EmailAddressMetricContributor";
  private static final String EMAIL_PATTERN =
    "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
  private Pattern pattern = Pattern.compile( EMAIL_PATTERN );
  private String regex = EMAIL_PATTERN;
  private String metricName;
  private String namePath = KEY_PATH;
  private String nameKey = EMAIL_ADDRESS_KEY;
  private Set<String> supportedTypes = new HashSet<String>( Arrays.asList( String.class.getCanonicalName() ) );

  public RegexAddressMetricContributor() {
    updateMetricName();
  }

  public String getRegex() {
    return regex;
  }

  public void setRegex( String regex ) {
    this.regex = regex;
    pattern = regex == null ? null : Pattern.compile( regex );
  }

  @Override public Set<String> supportedTypes() {
    return new HashSet<String>( supportedTypes );
  }

  public String getNamePath() {
    return namePath;
  }

  public void setNamePath( String namePath ) {
    this.namePath = namePath;
    updateMetricName();

  }

  public String getNameKey() {
    return nameKey;
  }

  public void setNameKey( String nameKey ) {
    this.nameKey = nameKey;
    updateMetricName();
  }

  public String metricName() {
    return metricName;
  }

  private void updateMetricName() {
    this.metricName = namePath + ":" + nameKey;
  }

  public Set<String> getSupportedTypes() {
    return supportedTypes;
  }

  public void setSupportedTypes( Set<String> supportedTypes ) {
    this.supportedTypes = supportedTypes;
  }

  private RegexHolder getOrCreateRegexHolder( MutableProfileFieldValueType mutableProfileFieldValueType ) {
    RegexHolder result = (RegexHolder) mutableProfileFieldValueType.getValueTypeMetrics( metricName );
    if ( result == null ) {
      result = new RegexHolder();
      mutableProfileFieldValueType.setValueTypeMetrics( metricName, result );
    }
    return result;
  }

  @Override
  public void process( MutableProfileFieldValueType mutableProfileFieldValueType,
                       DataSourceFieldValue dataSourceFieldValue )
    throws ProfileActionException {
    boolean match = pattern.matcher( dataSourceFieldValue.getFieldValue().toString() ).matches();
    if ( match ) {
      getOrCreateRegexHolder( mutableProfileFieldValueType ).increment();
    }
  }

  @Override public void merge( MutableProfileFieldValueType into, ProfileFieldValueType from )
    throws MetricMergeException {
    RegexHolder fromHolder = (RegexHolder) from.getValueTypeMetrics( metricName );
    if ( fromHolder != null && fromHolder.hasCount() ) {
      getOrCreateRegexHolder( into ).add( fromHolder.getCount() );
    }
  }

  @Override public List<ProfileFieldProperty> profileFieldProperties() {
    return Arrays.asList(
      new ProfileFieldProperty( namePath, nameKey,
        new ArrayList<String>( Arrays.asList( "types", "valueTypeMetricsMap", metricName, "count" ) ) ) );
  }

  //OperatorWrap isn't helpful for autogenerated methods
  //CHECKSTYLE:OperatorWrap:OFF

  @Override public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    RegexAddressMetricContributor that = (RegexAddressMetricContributor) o;

    if ( regex != null ? !regex.equals( that.regex ) : that.regex != null ) {
      return false;
    }
    if ( namePath != null ? !namePath.equals( that.namePath ) : that.namePath != null ) {
      return false;
    }
    if ( nameKey != null ? !nameKey.equals( that.nameKey ) : that.nameKey != null ) {
      return false;
    }
    return !( supportedTypes != null ? !supportedTypes.equals( that.supportedTypes ) : that.supportedTypes != null );

  }

  @Override public int hashCode() {
    int result = regex != null ? regex.hashCode() : 0;
    result = 31 * result + ( namePath != null ? namePath.hashCode() : 0 );
    result = 31 * result + ( nameKey != null ? nameKey.hashCode() : 0 );
    result = 31 * result + ( supportedTypes != null ? supportedTypes.hashCode() : 0 );
    return result;
  }

  @Override public String toString() {
    return "RegexAddressMetricContributor{" +
      "regex='" + regex + '\'' +
      ", namePath='" + namePath + '\'' +
      ", nameKey='" + nameKey + '\'' +
      ", supportedTypes=" + supportedTypes +
      "} " + super.toString();
  }
  //CHECKSTYLE:OperatorWrap:ON
}
