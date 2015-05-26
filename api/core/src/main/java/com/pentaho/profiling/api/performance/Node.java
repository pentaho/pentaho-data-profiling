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

package com.pentaho.profiling.api.performance;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bryan on 2/27/15.
 */
public class Node {
  private final Map<String, Node> children;
  private long count = 0L;

  public Node() {
    this.children = new HashMap<String, Node>();
  }

  public Node locateNode( String stackElementString ) {
    Node result = children.get( stackElementString );
    if ( result == null ) {
      result = new Node();
      children.put( stackElementString, result );
    }
    return result;
  }

  public void incrementCount() {
    count++;
  }

  public long getCount() {
    return count;
  }

  public Map<String, Node> getChildren() {
    return children;
  }
}
