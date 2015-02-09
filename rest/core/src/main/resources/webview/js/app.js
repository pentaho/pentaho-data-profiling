'use-strict';
// UTILITIES
var Pentaho = Pentaho || {};

Pentaho.utilities = {
  toArray: function (t) {
    return (t == null || Array.isArray(t)) ? t : [t];
  },
  append: function (a1, a2) {
    for (var i = 0, L = a2.length; i < L; i++) a1.push(a2[i]);
    return a1;
  },
  compare: function (a, b) {
    return a > b ? 1 : a < b ? -1 : 0;
  }
};
// -----
define([
  "require",
  "common-ui/angular",
  "common-ui/angular-route",
  "common-ui/angular-translate",
  "common-ui/angular-sanitize",
  "com.pentaho.profiling.services.webview/controllers/controllers",
  "com.pentaho.profiling.services.webview/controllers/profileAppController",
  "com.pentaho.profiling.services.webview/services/services",
  "com.pentaho.profiling.services.webview/services/profileService",
  "com.pentaho.profiling.services.webview/services/dataSourceService",
  "com.pentaho.profiling.services.webview/services/profileAppService",
  "com.pentaho.profiling.services.webview/services/tabularService"
], function (require, angular) {
  var provide = null,
      controllerProvider = null,
      profileApp = angular.module('profileApp', [
        'ngRoute',
        'ngSanitize', // for ngBindHtml
        'appServices',
        'appControllers',
        'pascalprecht.translate'
      ]);

  profileApp.filter('interpolateMessage', function () {
    return function (message, replacements) {
      if (replacements) {
        for (var i = 0; i < replacements.length; i++) {
          message = message.split("{" + i + "}").join(replacements[i]);
        }
      }
      return message;
    };
  });

  profileApp.factory('translationCustomAsyncLoader', function ($q, $http) {

    return function () {
      var deferred = $q.defer();

      $http.post('/cxf/i18n/wildcard',
          {
            "resourceBundleRequest":{
              "wildcards":[
                {"keyRegex":"mongo-profiling.*"},
                {"keyRegex":"data-profiling.*"},
                {"keyRegex":"profiling-metrics.*"}
              ],
              "locale":"en_us"
            }
          }).
          success(function (data, status, headers, config) {
            deferred.resolve(data);
          }).
          error(function (data, status, headers, config) {
            // ...
          });

      return deferred.promise;
    };
  });

  profileApp.config([
    '$routeProvider',
    '$provide',
    '$controllerProvider',
    '$translateProvider',
    function ($routeProvider, $provide, $controllerProvider, $translateProvider) {
      provide = $provide;
      controllerProvider = $controllerProvider;

      $routeProvider
          .when('/:profileId', {
            templateUrl: 'partials/default-view.html',
            controller: 'profileAppController'
          })
          .otherwise({
            redirectTo: '/'
          });

      $translateProvider
          .useLoader('translationCustomAsyncLoader')
        // TODO: SESSION_LOCALE - webcontext.js had this global variable
          .preferredLanguage('en')
          .fallbackLanguage('en');

      // TODO: must still improve composite locales support (ex: en_US)
      // to be able to connect directly to the browser's locale.
      // The following $translate methods support this,
      //  and would be used instead of preferredLanguage(),
      //  but require us to list every possible mapping...?!
      //.registerAvailableLanguageKeys(['en'], {
      //  'en_US': 'en',
      //  'en_UK': 'en'
      //})
      //.determinePreferredLanguage()
    }]);

  return {
    getProvide: function () {
      return provide;
    },

    getControllerProvider: function () {
      return controllerProvider;
    },

    app: profileApp,

    init: function () {
      angular.element(document).ready(function () {
        angular.bootstrap(document.getElementById('profileView'), ['profileApp']);
      });
    }
  };
});