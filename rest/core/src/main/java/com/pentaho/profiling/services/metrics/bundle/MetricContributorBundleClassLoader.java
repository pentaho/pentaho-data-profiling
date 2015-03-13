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

package com.pentaho.profiling.services.metrics.bundle;

import com.pentaho.profiling.api.metrics.bundle.MetricContributorBundle;

import java.util.List;

/**
 * Created by bryan on 3/9/15.
 */
public class MetricContributorBundleClassLoader extends ClassLoader {
  private final List<MetricContributorBundle> metricContributorBundles;

  public MetricContributorBundleClassLoader( List<MetricContributorBundle> metricContributorBundles ) {
    this.metricContributorBundles = metricContributorBundles;
  }

  @Override public Class<?> loadClass( String name ) throws ClassNotFoundException {
    for ( MetricContributorBundle metricContributorBundle : metricContributorBundles ) {
      for ( Class<?> metricClass : metricContributorBundle.getMetricContributorClasses() ) {
        String canonicalName = metricClass.getCanonicalName();
        if ( canonicalName.equals( name ) ) {
          Class.forName( canonicalName, true, metricClass.getClassLoader() );
          return metricClass;
        }
      }
    }
    return MetricContributorBundleClassLoader.class.getClassLoader().loadClass( name );
  }
}
