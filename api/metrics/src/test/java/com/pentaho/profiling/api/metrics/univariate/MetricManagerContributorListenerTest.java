package com.pentaho.profiling.api.metrics.univariate;

import com.pentaho.profiling.api.metrics.MetricContributor;
import com.pentaho.profiling.api.metrics.MetricManagerContributor;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Dictionary;
import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 2/5/15.
 */
public class MetricManagerContributorListenerTest {
  @Test
  public void testAddedRemoved() {
    BundleContext bundleContext = mock( BundleContext.class );
    MetricManagerContributorListener metricManagerContributorListener =
      new MetricManagerContributorListener( bundleContext );
    MetricManagerContributor metricManagerContributor = mock( MetricManagerContributor.class );
    ServiceRegistration serviceRegistration = mock( ServiceRegistration.class );
    Class<MetricContributor> metricContributorClass = MetricContributor.class;
    when( bundleContext
      .registerService( eq( metricContributorClass ), any( MetricContributor.class ), any( Dictionary.class ) ) )
      .thenReturn(
        serviceRegistration );
    metricManagerContributorListener.implAdded( metricManagerContributor, new HashMap() );
    metricManagerContributorListener.implRemoved( metricManagerContributor, new HashMap() );
    verify( serviceRegistration ).unregister();
  }
}
