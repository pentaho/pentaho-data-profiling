'use-strict';

define(["common-ui/angular", "com.pentaho.profiling.services.webview.services", "com.pentaho.profiling.notification.service"], function(angular, services, notification){
  var appControllers = angular.module('appControllers', ['NotificationServiceModule']);
  appControllers.controller('AppController', ['$scope', '$routeParams', 'Profile', 'DataSourceService', 'NotificationService',
    function($scope, $routeParams, Profile, DataSourceService, NotificationService) {
      $scope.showTemplate = false;

      $scope.updateProfile = function(profile) {
        $scope.profileStatus = profile["profileStatus"];
        $scope.updateDataSource(profile["profileStatus"]["dataSourceReference"]);
      };

      $scope.updateDataSource = function(dataSourceReference) {
        if (!$scope.dataSourceReference || $scope.dataSourceReference.id != dataSourceReference.id) {
          if (!$scope.dataSourceReference || $scope.dataSourceReference.dataSourceProvider != dataSourceReference.dataSourceProvider) {
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
            });
          } else {
            $scope.dataSourceReference = dataSourceReference;
          }
        }
      }

      NotificationService.register("com.pentaho.profiling.model.ProfileNotificationProvider", [$routeParams.profileId], function(id) {
        Profile.get({profileId: id }, function(profile){
          $scope.updateProfile(profile);
        });
      });
    }
  ]);
  return appControllers;
});