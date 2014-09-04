'use-strict';

define([
    "common-ui/angular",
    "com.pentaho.profiling.services.webview/services",
    "com.pentaho.profiling.notification.service"
  ], function(angular/*, services, notification*/) {

  var appControllers = angular.module('appControllers', ['NotificationServiceModule']);

  appControllers.controller('AppController', [
    '$scope',
    '$routeParams',
    'Profile',
    'DataSourceService',
    'NotificationService',
    function($scope, $routeParams, profileService, dataSourceService, notificationService) {
      $scope.showTemplate    = false;

      $scope.orderByField    = 'name';
      $scope.isOrderReversed = false;

      $scope.updateProfile = function(profileStatus) {
        $scope.profileStatus = profileStatus;

        // TODO: Until there are real statistics, enhance fields with dummy statistics.
        if(profileStatus.fields) profileStatus.fields.forEach(function(field) {
          field.countDistinct = Math.floor(1000 * Math.random());
        });

        $scope.updateDataSource(profileStatus.dataSourceReference);
      };

      $scope.updateDataSource = function(dataSourceReference) {
        var oldDsr    = $scope.dataSourceReference,
            newDsId   = dataSourceReference.id,
            newDsProv = dataSourceReference.dataSourceProvider;

        if(!oldDsr || oldDsr.id != newDsId) {
          $scope.dataSourceReference = dataSourceReference;

          if(!oldDsr || oldDsr.dataSourceProvider != newDsProv) {
            dataSourceService.get({id: newDsId, dataSourceProvider: newDsProv}, function(dsIncludeWrapper) {

              var dsInclude = dsIncludeWrapper.profileDataSourceInclude;
              if(dsInclude.require) {
                require([dsInclude.require], function() {
                  $scope.dataSourceUrl = dsInclude.url;
                  $scope.$apply();
                });
              } else {
                $scope.dataSourceUrl = dsInclude.url;
                // $scope.$apply() already within an apply, when in a resource callback.
              }
            });
          }
        }
      };

      $scope.onOrderByField = function(name) {
        // Cycles through: Ascending -> Descending -> Unsorted

        if($scope.orderByField === name) {
          if($scope.isOrderReversed) {
            $scope.orderByField = '';
          } else {
            $scope.isOrderReversed = true;
          }
        } else {
          $scope.orderByField = name;
          $scope.isOrderReversed = false;
        }
      };

      notificationService.register(
        /* notifType */
        "com.pentaho.profiling.model.ProfileNotificationProvider",
        /* ids */
        [$routeParams.profileId],
        /* cb */
        function(changedProfileId) {
          // Get a ProfileStatus of this profile.
          profileService.get({profileId: changedProfileId}, function(profileStatusWrapper) {
            $scope.updateProfile(profileStatusWrapper.profileStatus);
          });
        });
    }
  ]);

  return appControllers;
});