/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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

package org.pentaho.profiling.core.integration.tests.utils;

import com.sun.jersey.api.client.UniformInterfaceException;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by bryan on 3/26/15.
 */
public class DataProfilingService {
  private static final AtomicBoolean started = new AtomicBoolean( false );
  private RestClient restClient;
  private Process karafProcess;
  private File directory =
    new File( "target/dependencies/pentaho-data-profiling-assemblies-karaf/apache-karaf-2.3.5/" );

  public RestClient getRestClient() {
    return restClient;
  }

  /**
   * This method starts a the Karaf container and waits until the profiling webservice is available
   * <p/>
   * If you get FileNotFound exceptions, run mvn clean install from the command line (to ensure karaf is staged and then
   * set your working directory to the module dir
   * <p/>
   * http://stackoverflow.com/questions/5637765/how-to-deal-with-relative-path-in-junits-between-maven-and-intellij In
   * Run->Edit configuration->Defaults->JUnit->Working directory set the value $MODULE_DIR$ and Intellij will set the
   * relative path in all junits just like Maven.
   *
   * @throws Exception
   */
  public void start() throws Exception {
    if ( started.getAndSet( true ) ) {
      throw new IllegalStateException( "Illegal to have 2 karaf instances running at once" );
    }
    System.out.println( directory.getAbsolutePath() );
    File metricContributorsJson = new File( directory.getAbsolutePath() + "/etc/metricContributors.json" );
    // Delete existing metric contributors json
    if ( metricContributorsJson.exists() ) {
      int tries = 0;
      while( tries < 10 ) {
        if ( metricContributorsJson.delete() ) {
          break;
        }
        Thread.sleep( 500 );
        tries++;
      }
    }
    ProcessBuilder processBuilder = new ProcessBuilder().directory( directory ).command(
      SystemUtils.IS_OS_WINDOWS ? "bin/karaf.bat" : "bin/karaf" ).inheritIO();
    /*processBuilder.environment()
      .put( "JAVA_OPTS", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005" );*/
    karafProcess = processBuilder.start();
    restClient = new RestClient();
    Set<Class<?>> acceptableExceptions = new HashSet<Class<?>>();
    acceptableExceptions.add( ConnectException.class );
    acceptableExceptions.add( UniformInterfaceException.class );
    while ( true ) {
      try {
        restClient.performGet( List.class, "cxf/profile" );
        return;
      } catch ( Exception che ) {
        Exception e = (Exception) che.getCause();
        if ( e == null || acceptableExceptions.contains( e.getClass() ) ) {
          System.out.println( "Waiting for Karaf Webservices" );
          try {
            Thread.sleep( 1000 );
          } catch ( InterruptedException e1 ) {
            //ignore
          }
        } else {
          throw e;
        }
      }
    }
  }

  public void stop() throws IOException {
    if ( !started.getAndSet( false ) ) {
      throw new IllegalStateException( "Illegal to have 2 karaf instances running at once" );
    }
    new ProcessBuilder().directory( directory ).command( "bin/stop" ).inheritIO().start();
    try {
      karafProcess.waitFor();
    } catch ( InterruptedException e ) {
      e.printStackTrace();
    }
  }
}
