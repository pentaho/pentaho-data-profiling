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
