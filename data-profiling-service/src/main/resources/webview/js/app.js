'use-strict';

define(["common-ui/angular", "common-ui/angular-route", "com.pentaho.profiling.services.webview.controllers", "com.pentaho.profiling.services.webview.services"],
       function(angular, angularRoute, controllers, services){
  var provide = null;
  var controllerProvider = null;
  var profileApp = angular.module('profileApp', [
    'ngRoute',
    'appServices',
    'appControllers'
  ]);
  profileApp.config(['$routeProvider', '$provide', '$controllerProvider',
    function($routeProvider, $provide, $controllerProvider) {
      provide = $provide;
      controllerProvider = $controllerProvider;
      $routeProvider.when('/:profileId', {
        templateUrl: 'partials/default-view.html',
        controller: 'AppController'
      }).
      otherwise({
        redirectTo: '/'
      });
    }]);
    return {
      getProvide: function() {
        return provide;
      },
      getControllerProvider: function() {
        return controllerProvider;
      },
      app: profileApp,
      init: function() {
        angular.element(document).ready(function(){
          angular.bootstrap(document.getElementById('profileView'), ['profileApp']);
        });
      }
    };
});