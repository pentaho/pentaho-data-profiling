/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
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

package com.pentaho.profiling.services;

import com.pentaho.profiling.api.datasource.DataSourceReference;
import com.pentaho.profiling.services.api.ProfileDataSourceInclude;
import com.pentaho.profiling.services.api.ProfileDataSourceIncludeService;

import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by bryan on 8/6/14.
 */
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
@WebService
public class ProfileDataSourceIncludeWebserviceImpl implements ProfileDataSourceIncludeService {
  List<ProfileDataSourceIncludeService> includeServices;

  public List<ProfileDataSourceIncludeService> getIncludeServices() {
    return includeServices;
  }

  public void setIncludeServices( List<ProfileDataSourceIncludeService> includeServices ) {
    this.includeServices = includeServices;
  }

  @Override
  public ProfileDataSourceInclude getInclude( DataSourceReference dataSourceReference ) {
    ProfileDataSourceInclude result = null;
    for ( ProfileDataSourceIncludeService service : includeServices ) {
      result = service.getInclude( dataSourceReference );
      if ( result != null ) {
        break;
      }
    }
    return result;
  }

  @GET
  @Path("/{id}/{dataSourceProvider}")
  public ProfileDataSourceInclude getIncludeUrl( @PathParam("id") String id,
                                                 @PathParam("dataSourceProvider") String dataSourceProvider ) {
    DataSourceReference dataSourceReference = new DataSourceReference( id, dataSourceProvider );
    return getInclude( dataSourceReference );
  }
}
