'use-strict';

define([
    "common-ui/angular",
    "common-ui/angular-route",
    "common-ui/angular-translate",
    "common-ui/angular-translate-loader-partial",
    "common-ui/angular-sanitize",
    "com.pentaho.profiling.services.webview/controllers",
    "com.pentaho.profiling.services.webview/services"
  ], function(angular) {

  var provide = null,
      controllerProvider = null,
      profileApp = angular.module('profileApp', [
        'ngRoute',
        'ngSanitize', // for ngBindHtml
        'appServices',
        'appControllers',
        'pascalprecht.translate'
      ]);

  profileApp.filter('interpolateMessage', function() {
    return function(message, replacements) {
      if (replacements) {
        for (var i = 0; i < replacements.length; i++) {
          message = message.split("{" + i + "}").join(replacements[i]);
        }
      }
      return message;
    };
   });

  profileApp.config([
    '$routeProvider',
    '$provide',
    '$controllerProvider',
    '$translateProvider',
    '$translatePartialLoaderProvider',
    function($routeProvider, $provide, $controllerProvider, $translateProvider, $translatePartialLoaderProvider) {
      provide = $provide;
      controllerProvider = $controllerProvider;

      $routeProvider
        .when('/:profileId', {
          templateUrl: 'partials/default-view.html',
          controller:  'AppController'
        })
        .otherwise({
          redirectTo: '/'
        });


      $translatePartialLoaderProvider.addPart("data-profiling/com.pentaho.profiling.services.messages");

      $translateProvider
        .useLoader('$translatePartialLoader', {
          urlTemplate: '/cxf/i18n/{part}/{lang}'
        })
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
    getProvide: function() {
      return provide;
    },

    getControllerProvider: function() {
      return controllerProvider;
    },

    app: profileApp,

    init: function() {
      angular.element(document).ready(function() {
        angular.bootstrap(document.getElementById('profileView'), ['profileApp']);
      });
    }
  };
});