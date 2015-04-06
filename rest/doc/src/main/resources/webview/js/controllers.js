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
          success(function (data, status, headers, config) {
            var endpoints = data[1];
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
              endpoint.endpointPathParametersTable = turnIntoTable('Path Parameters', ['Name', 'Type', 'Description'], ['name', 'type', 'description'], endpoint.endpointPathParameters[1]);
              endpoint.endpointQueryParametersTable = turnIntoTable('Query Parameters', ['Name', 'Type', 'Description'], ['name', 'type', 'description'], endpoint.endpointQueryParameters[1]);
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
              var errorCodes = endpoint.errorCodes[1];
              if (!errorCodes) {
                errorCodes = [];
              }
              if (endpoint.successResponseCode) {
                errorCodes.unshift({status: endpoint.successResponseCode, reason: 'Request successfully processed'})
              }
              endpoint.errorCodesTable = turnIntoTable('Response Codes', ['Code', 'Reason'], ['status', 'reason'], endpoint.errorCodes[1]);
              if (endpoint.endpointExamples) {
                endpoint.endpointExamples[1].forEach(function(endpointExample) {
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
                  endpoint.endpointExamples[1].forEach(function(endpointExample) {
                    endpointExample.show = true;
                  });
                }
                endpoint.hideAll = function() {
                  endpoint.endpointExamples[1].forEach(function(endpointExample) {
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