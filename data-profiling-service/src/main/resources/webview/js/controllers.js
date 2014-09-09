'use-strict';

define([
    "require",
    "common-ui/angular",
    "common-ui/properties-parser",
    "common-ui/angular-route",
    "common-ui/angular-translate",
    "common-ui/angular-translate-loader-partial",
    "com.pentaho.profiling.services.webview/services",
    "com.pentaho.profiling.notification.service"
  ], function(require, angular/*, services, notification*/) {

  var appControllers = angular.module('appControllers', [
    'NotificationServiceModule',
    'ngRoute',
    'pascalprecht.translate']);

  appControllers.controller('AppController', [
    '$scope',
    '$routeParams',
    'Profile',
    'DataSourceService',
    'NotificationService',
    '$translate',
    '$translatePartialLoader',
    function($scope, $routeParams, profileService, dataSourceService, notificationService, $translate, $translatePartialLoader) {
      $scope.showTemplate    = false;

      $scope.orderByField    = 'name';
      $scope.isOrderReversed = false;

      $scope.stopCurrentOperation = function() {
        profileService.stop({profileIdWrapper: {profileId: $scope.profileId}});
      };

      $scope.startOperation = function(operationId) {
        profileService.start({profileOperationWrapper: {profileId: $scope.profileId, operationId: operationId}});
      };

      $scope.updateProfile = function(profileStatus) {
        $scope.profileId = profileStatus.id;
        profileService.getOperations({profileId: profileStatus.id}, function(operations){
          $scope.operations = operations.profileOperation;
        });
        var fieldMap = {};
        if ( profileStatus.profileFieldDefinition ) {
          if (!Array.isArray(profileStatus.profileFieldDefinition)) {
            profileStatus.profileFieldDefinition = [profileStatus.profileFieldDefinition];
          }
          var index = 0;
          profileStatus.profileFieldDefinition.forEach(function(definition){
            if (!$translatePartialLoader.isPartAvailable(definition.namePath)) {
              $translatePartialLoader.addPart(definition.namePath);
            }
            definition.index = index++;
            definition.isDefinition = true;
            definition.stringifiedPath = JSON.stringify(definition.pathToProperty);
            var currentMap = fieldMap;
            for (var i = 0; i < definition.pathToProperty.length - 1; i++) {
              var pathElement = definition.pathToProperty[i];
              if (!(pathElement in currentMap)) {
                currentMap[pathElement] = {};
              }
              currentMap = currentMap[pathElement];
            }
            currentMap[definition.pathToProperty[definition.pathToProperty.length - 1]] = definition;
          });
        }
        var fieldHeaders = [];
        var addedFields = { };
        if(profileStatus.fields) {
          if (!Array.isArray(profileStatus.fields)) {
            profileStatus.fields = [profileStatus.fields];
          }
          profileStatus.fields.forEach(function(field) {
            var indices = $scope.getAddedFields(fieldMap, field);
            for (var i = 0; i < indices.length; i++) {
              addedFields[indices[i]] = true;
            }
          });
          profileStatus.fields.forEach(function(field) {

          });
          for ( var key in addedFields ) {
            fieldHeaders.push(profileStatus.profileFieldDefinition[key]);
          }
        }
        var fieldRows = $scope.getRows(fieldMap, profileStatus.fields);
        $scope.updateDataSource(profileStatus.dataSourceReference);
        $scope.currentOperation = profileStatus.currentOperation;
        $scope.currentOperationVariables = profileStatus.currentOperationVariables;
        $scope.fieldHeaders = fieldHeaders;
        $scope.fieldRows = fieldRows;
      };

      $scope.getCell = function(fieldHeader, fieldRow) {
        for(var i = 0; i < fieldHeader.pathToProperty.length; i++) {
          var pathElement = fieldHeader.pathToProperty[i];
          if (pathElement in fieldRow) {
            fieldRow = fieldRow[pathElement];
          } else {
            return null;
          }
        }
        return fieldRow;
      }

      $scope.getAddedFields = function(pathMap, fieldMap) {
        var result = [];
        if (Array.isArray(fieldMap)) {
          fieldMap.forEach(function(entry){
            result = result.concat($scope.getAddedFields(pathMap, entry));
          });
        } else {
          for ( var key in fieldMap ) {
            if ( key in pathMap ) {
              if ( pathMap[key].isDefinition ) {
                result.push(pathMap[key].index);
              } else {
                result = result.concat($scope.getAddedFields(pathMap[key], fieldMap[key]));
              }
            }
          }
        }
        return result;
      }

      $scope.getRows = function(pathMap, fieldMap) {
        var flatten = function(currentPathMap, currentFieldMap, result) {
          if (Array.isArray(currentFieldMap)) {
            var newResult = [];
            var length = currentFieldMap.length;
            for(var i = 0; i < length - 1; i++) {
              newResult = newResult.concat(flatten(currentPathMap, currentFieldMap[i], result.map(function(item){
                return angular.copy(item);
              })));
            }
            if ( length >= 1) {
              result = newResult.concat(flatten(currentPathMap, currentFieldMap[length - 1], result));
            }
          } else if (currentFieldMap) {
            for (var key in currentPathMap) {
              if(key in currentFieldMap) {
                var nextPathMap = currentPathMap[key];
                if (nextPathMap.isDefinition) {
                  result.forEach(function(item){
                    item[nextPathMap.stringifiedPath] = currentFieldMap[key];
                  });
                } else {
                  result = flatten(currentPathMap[key], currentFieldMap[key], result);
                }
              }
            }
          }
          return result;
        };
        return flatten(pathMap, fieldMap, [{}]);
      }

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

      $scope.onOrderByField = function(fieldHeader) {
        // Cycles through: Ascending -> Descending -> Unsorted

        if($scope.orderByField === fieldHeader.stringifiedPath) {
          if($scope.isOrderReversed) {
            $scope.orderByField = '';
          } else {
            $scope.isOrderReversed = true;
          }
        } else {
          $scope.orderByField = fieldHeader.stringifiedPath;
          $scope.isOrderReversed = false;
        }
      };

      $scope.orderByPredicate = function(row) {
        return row[$scope.orderByField];
      }

      notificationService.register(
        /* notifType */
        "com.pentaho.profiling.model.ProfileNotificationProvider",
        /* ids */
        [$routeParams.profileId],
        /* cb */
        function(changedProfileId) {
          // Get a ProfileStatus of this profile.
          profileService.query({profileId: changedProfileId}, function(profileStatusWrapper) {
            $scope.updateProfile(profileStatusWrapper.profileStatus);
          });
        });
    }
  ]);

  return appControllers;
});