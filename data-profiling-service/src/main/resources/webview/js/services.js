'use-strict';

define(["common-ui/angular", "common-ui/angular-resource"], function(angular, angularResource){
  var appServices = angular.module('appServices', ['ngResource']);

  appServices.factory('Profile', ['$resource',
    function($resource) {
      return $resource('/cxf/profile/:profileId', {}, {
        query: {method: 'GET', params: {profileId:'profileId'}}
      });
  }]);

  appServices.factory('DataSourceService', ['$resource',
      function($resource) {
        return $resource('/cxf/data-profiling-service/dataSource/:id/:dataSourceProvider', {}, {
          query: {method: 'GET', params: {profileId:'profileId'}}
        });
    }]);
  return appServices;
});