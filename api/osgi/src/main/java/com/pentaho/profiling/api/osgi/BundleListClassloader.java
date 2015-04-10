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

package com.pentaho.profiling.api.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by bryan on 4/13/15.
 */
public class BundleListClassloader extends ClassLoader {
  private final BundleContext bundleContext;
  private final List<Class> servicesToTrack;
  private volatile List<Bundle> bundles;

  public BundleListClassloader( BundleContext bundleContext, List<Class> servicesToTrack ) {
    this.bundleContext = bundleContext;
    this.servicesToTrack = servicesToTrack;
    List<ServiceTracker> serviceTrackers = new ArrayList<ServiceTracker>( servicesToTrack.size() );
    for ( Class aClass : servicesToTrack ) {
      serviceTrackers.add( new ServiceTracker( bundleContext, aClass, new ServiceTrackerCustomizer() {
        @Override public Object addingService( ServiceReference reference ) {
          synchronized ( BundleListClassloader.this ) {
            bundles = null;
          }
          return reference;
        }

        @Override public void modifiedService( ServiceReference reference, Object service ) {
          synchronized ( BundleListClassloader.this ) {
            bundles = null;
          }
        }

        @Override public void removedService( ServiceReference reference, Object service ) {
          synchronized ( BundleListClassloader.this ) {
            bundles = null;
          }
        }
      } ) );
    }
    for ( final ServiceTracker serviceTracker : serviceTrackers ) {
      serviceTracker.open();
      bundleContext.addBundleListener( new BundleListener() {
        @Override public void bundleChanged( BundleEvent event ) {
          if ( event.getType() == BundleEvent.STOPPING ) {
            serviceTracker.close();
          }
        }
      } );
    }
  }

  private synchronized List<Bundle> getBundles() {
    if ( bundles != null ) {
      return bundles;
    }
    Set<Bundle> bundleSet = new HashSet<Bundle>();
    List<Bundle> bundleList = new ArrayList<Bundle>();
    for ( Class aClass : servicesToTrack ) {
      try {
        for ( Object serviceReferenceObj : bundleContext.getServiceReferences( aClass, null ) ) {
          ServiceReference serviceReference = (ServiceReference) serviceReferenceObj;
          Bundle bundle = serviceReference.getBundle();
          if ( bundleSet.add( bundle ) ) {
            bundleList.add( bundle );
          }
        }
      } catch ( InvalidSyntaxException e ) {
        e.printStackTrace();
      }
    }
    bundleList.add( bundleContext.getBundle() );
    this.bundles = bundleList;
    return bundleList;
  }

  @Override public Class<?> loadClass( String name ) throws ClassNotFoundException {
    for ( Bundle bundle : getBundles() ) {
      try {
        return bundle.loadClass( name );
      } catch ( ClassNotFoundException e ) {
        // Ignore
      }
    }
    return super.loadClass( name );
  }
}
