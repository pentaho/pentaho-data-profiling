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
    '$translate',
    'Profile',
    'DataSourceService',
    'NotificationService',
    function($scope, $routeParams, $translate, profileService, dataSourceService, notificationService) {
      // The first profile update does not bring fields.
      // If the second does not bring fields, we show the "no fields" message.
      var updateProfileCount = 0;
      $scope.isLoadingFields = true;
      $scope.isProcessing    = true;

      $translate('profiling.messages.profiling-data').then(function(text) { $scope.message = text; });

      $scope.orderByField    = 'name';
      $scope.isOrderReversed = false;

      function setMessage(text) {
        $scope.message = text;
      }

      $scope.updateProfile = function(profileStatus) {
        $scope.profileStatus = profileStatus;

        // TODO: Until there are real statistics, enhance fields with dummy statistics.
        if(profileStatus.fields) {
          profileStatus.fields.forEach(function(field) {
            field.countDistinct = Math.floor(1000 * Math.random());
          });
        } else {
          profileStatus.fields = [];
        }

        switch(updateProfileCount) {
          case 0:
            updateProfileCount++;
            // assume to still be loading fields.
            break;
          case 1:
            updateProfileCount++;
            $scope.isLoadingFields = $scope.isProcessing = false;

            if(!profileStatus.fields.length) {
              $translate('profiling.messages.no-fields').then(setMessage);
            } else {
              var entityCount = profileStatus.totalEntities;
              if(entityCount === 0) {
                $translate('profiling.messages.no-data').then(setMessage);
              } else if(entityCount > 0)
                $translate('profiling.messages.processed-all-rows', {count: entityCount}).then(setMessage);
            }
            break;
        }

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
        // Ascending -> Descending -> Ascending ...
        if($scope.orderByField === name) {
          $scope.isOrderReversed = !$scope.isOrderReversed;
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