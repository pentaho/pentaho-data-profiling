'use-strict';

define(["angular", "com.pentaho.profiling.services.webview.services"], function(angular, services){
  var appControllers = angular.module('appControllers', []);
  appControllers.controller('AppController', ['$scope', '$routeParams', 'Profile', 'DataSourceService',
    function($scope, $routeParams, Profile, DataSourceService) {
      $scope.showTemplate = false;
      Profile.get({profileId: $routeParams.profileId}, function(profile){
        var profileStatus = profile["profileStatus"];
        var dataSourceReference = profile["profileStatus"]["dataSourceReference"]
        $scope.dataSourceReference = dataSourceReference;
        DataSourceService.get({id: dataSourceReference["id"], dataSourceProvider: dataSourceReference["dataSourceProvider"]}, function(profileDataSourceInclude) {
          var requiredComponent = profileDataSourceInclude["profileDataSourceInclude"]["require"];
          var dataSourceUrl = profileDataSourceInclude["profileDataSourceInclude"]["url"];
          if (requiredComponent) {
            require([requiredComponent], function(requiredComponent){
              $scope.dataSourceUrl = dataSourceUrl
              $scope.$apply();
            });
          } else {
            $scope.dataSourceUrl = dataSourceUrl;
            $scope.apply();
          }
        })
        $scope.profileStatus = profileStatus;
      });
    }
  ]);
  return appControllers;
});