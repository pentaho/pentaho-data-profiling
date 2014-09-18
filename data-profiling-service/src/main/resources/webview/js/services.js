'use-strict';

define(["common-ui/angular", "common-ui/angular-resource"], function(angular) {
  var appServices = angular.module('appServices', ['ngResource']);

  appServices.factory('Profile', ['$resource',
    function($resource) {
      return $resource('/cxf/profile/:profileId', {}, {
        query:         {method: 'GET', params: {profileId:'profileId'}},
        stop:          {method: 'PUT', url: '/cxf/profile/stop' },
        start:         {method: 'PUT', url: '/cxf/profile/start' },
        getOperations: {method: 'GET', params: {profileId:'profileId'}, url: '/cxf/profile/operations/:profileId', isArray: true }
      });
  }]);

  appServices.factory('DataSourceService', ['$resource',
    function($resource) {
      return $resource('/cxf/data-profiling-service/dataSource/include/:id/:dataSourceProvider', {}, {
        getInclude: {method: 'GET', params: {id: 'id', dataSourceProvider: 'dataSourceProvider'}},
        getCreate:  {method: 'GET', params: {id: 'id', dataSourceProvider: 'dataSourceProvider'}, url: '/cxf/data-profiling-service/dataSource/create/:id/:dataSourceProvider' }
      });
  }]);

  return appServices;
});