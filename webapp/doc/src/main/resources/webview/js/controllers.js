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

'use-strict';

define(["common-ui/angular"], function (angular) {

  var appControllers = angular.module('appControllers', []);

  appControllers.controller('AppController', [
    '$scope',
    '$http',
    '$window',
    '$location',
    '$anchorScroll',
    function ($scope, $http, $window, $location, $anchorScroll) {
      $scope.data = [];

      $scope.scroll = function(id) {
        var orig = $location.hash();
        $location.hash(id);
        $anchorScroll();
        $location.hash('');
      }

      $http.get('../cxf/doc').
          success(function (endpoints, status, headers, config) {
            var turnIntoTable = function (caption, headers, keys, data) {
              if (data && data.length > 0) {
                return {
                  shouldRender: true,
                  caption: caption,
                  colNames: headers,
                  colKeys: keys,
                  colRows: data
                }
              } else {
                return {
                  shouldRender: false
                }
              }
            }
            endpoints.forEach(function (endpoint) {
              endpoint.endpointPathParametersTable = turnIntoTable('Path Parameters', ['Name', 'Type', 'Description'], ['name', 'type', 'description'], endpoint.endpointPathParameters);
              endpoint.endpointQueryParametersTable = turnIntoTable('Query Parameters', ['Name', 'Type', 'Description'], ['name', 'type', 'description'], endpoint.endpointQueryParameters);
              var endpointBodyParameter = endpoint.endpointBodyParameter;
              if (endpointBodyParameter) {
                endpointBodyParameter = [endpointBodyParameter];
              }
              endpoint.endpointBodyParameterTable = turnIntoTable('Body Parameters', ['Name', 'Type', 'Description'], ['name', 'type', 'description'], endpointBodyParameter);
              var endpointResponse = endpoint.endpointResponse;
              if (endpointResponse) {
                endpointResponse = [endpointResponse];
              }
              endpoint.endpointResponseTable = turnIntoTable('Response', ['Type', 'Description'], ['type', 'description'], endpointResponse);
              var errorCodes = endpoint.errorCodes;
              if (!errorCodes) {
                errorCodes = [];
              }
              if (endpoint.successResponseCode) {
                errorCodes.unshift({status: endpoint.successResponseCode, reason: 'Request successfully processed.'})
              }
              endpoint.errorCodesTable = turnIntoTable('Response Codes', ['Code', 'Reason'], ['status', 'reason'], endpoint.errorCodes);
              if (endpoint.endpointExamples) {
                endpoint.endpointExamples.forEach(function(endpointExample) {
                  var exampleBody = endpointExample.body;
                  if (exampleBody === undefined || exampleBody === null) {
                    // Ignore
                  } else {
                    endpointExample.body = JSON.stringify(JSON.parse(exampleBody), null, 2);
                  }
                  var exampleReturn = endpointExample.exampleReturn;
                  if (exampleReturn === undefined || exampleReturn === null) {
                    // Ignore
                  } else {
                    endpointExample.exampleReturn = JSON.stringify(JSON.parse(exampleReturn), null, 2);
                  }
                  endpointExample.show = false;
                });
                endpoint.expandAll = function() {
                  endpoint.endpointExamples.forEach(function(endpointExample) {
                    endpointExample.show = true;
                  });
                }
                endpoint.hideAll = function() {
                  endpoint.endpointExamples.forEach(function(endpointExample) {
                    endpointExample.show = false;
                  });
                }
              }
            });
            $scope.data = endpoints;
          }).error(function (data, status, headers, config) {
            //Do nothing....use defaults
          });
    }
  ])

  return appControllers;
})
;