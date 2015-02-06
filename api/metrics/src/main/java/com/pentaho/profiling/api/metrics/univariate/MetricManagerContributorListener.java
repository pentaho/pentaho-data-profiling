package com.pentaho.profiling.api.metrics.univariate;

import com.pentaho.profiling.api.metrics.MetricContributor;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by bryan on 2/5/15.
 */
public class MetricManagerContributorListener {
  private final BundleContext bundleContext;
  private final Map<MetricManagerContributor, ServiceRegistration> contributorMap =
    new Hashtable<MetricManagerContributor, ServiceRegistration>();

  public MetricManagerContributorListener( BundleContext bundleContext ) {
    this.bundleContext = bundleContext;
  }

  public void implAdded( MetricManagerContributor metricManagerContributor, Map properties ) {
    MetricManagerBasedMetricContributor metricManagerBasedMetricContributor =
      new MetricManagerBasedMetricContributor( metricManagerContributor );
    ServiceRegistration serviceRegistration =
      bundleContext.registerService( MetricContributor.class, metricManagerBasedMetricContributor, new Hashtable() );
    contributorMap.put( metricManagerContributor, serviceRegistration );
  }

  public void implRemoved( MetricManagerContributor metricManagerContributor, Map properties ) {
    contributorMap.remove( metricManagerContributor ).unregister();
  }
}
