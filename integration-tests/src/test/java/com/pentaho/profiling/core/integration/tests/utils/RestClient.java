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

package org.pentaho.profiling.core.integration.tests.utils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * Created by bryan on 3/26/15.
 */
public class RestClient {
  private static Client createClient() {
    ClientConfig clientConfig = new DefaultClientConfig();
    clientConfig.getFeatures().put( JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE );
    Client client = Client.create( clientConfig );
    //client.addFilter( new LoggingFilter( System.out ) );
    return client;
  }

  public <ReturnType> ReturnType performGet( Class<ReturnType> clazz, String relativePath ) {
    ClientConfig clientConfig = new DefaultClientConfig();
    clientConfig.getFeatures().put( JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE );

    Client client = Client.create( clientConfig );
    WebResource webResource = client.resource( "http://localhost:8181/" + relativePath );
    return webResource.accept( "application/json" ).get( clazz );
  }

  public <ArgumentType, ReturnType> ReturnType post( ArgumentType argument, Class<ReturnType> returnTypeClazz,
                                                     String relativePath )

    throws IOException {
    return post( argument, returnTypeClazz, relativePath, null );
  }

  public <ArgumentType, ReturnType> ReturnType post( ArgumentType argument, Class<ReturnType> returnTypeClazz,
                                                     String relativePath, ObjectMapper objectMapper )
    throws IOException {
    WebResource webResource = createClient().resource( "http://localhost:8181/" + relativePath );
    ClientResponse clientResponse =
      webResource.type( "application/json" ).accept( "application/json" ).post( ClientResponse.class, argument );
    if ( objectMapper == null ) {
      return clientResponse.getEntity( returnTypeClazz );
    } else {
      return objectMapper.readValue( clientResponse.getEntity( String.class ), returnTypeClazz );
    }
  }
}
