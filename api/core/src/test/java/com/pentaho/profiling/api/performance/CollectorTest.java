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

package org.pentaho.profiling.api.performance;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by bryan on 3/19/15.
 */
public class CollectorTest {
  @Test
  public void testCollector() throws InterruptedException {
    Thread worker = new Thread( new Runnable() {
      @Override public void run() {
        try {
          Thread.sleep( 65 );
        } catch ( InterruptedException e ) {
          e.printStackTrace();
        }
      }
    } );
    worker.setName( "worker" );
    Collector collector = new Collector( worker );
    worker.start();
    collector.start();
    worker.join();
    collector.stop();
    Node rootNode = collector.getRootNode();
    assertNotNull( rootNode.getChildren() );
    assertEquals( 0, rootNode.getCount() );
    assertTrue( collector.getName().startsWith( worker.getName() ) );
  }
}
