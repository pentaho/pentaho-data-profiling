/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

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
  "org.pentaho.profiling.services.webview/controllers/controllers",
  "org.pentaho.profiling.services.webview/controllers/profileAppController",
  "org.pentaho.profiling.services.webview/controllers/tabularViewController",
  "org.pentaho.profiling.services.webview/controllers/profileManagementViewController",
  "org.pentaho.profiling.services.webview/controllers/fieldOverviewViewController",
  "org.pentaho.profiling.services.webview/controllers/metricConfigViewController",
  "org.pentaho.profiling.services.webview/controllers/defaultMetricConfigViewController",
  "org.pentaho.profiling.services.webview/controllers/treeViewController",
  "org.pentaho.profiling.services.webview/controllers/createProfilerViewController",
  "org.pentaho.profiling.services.webview/services/services",
  "org.pentaho.profiling.services.webview/services/profileService",
  "org.pentaho.profiling.services.webview/services/dataSourceService",
  "org.pentaho.profiling.services.webview/services/profileAppService",
  "org.pentaho.profiling.services.webview/services/treeViewService",
  "org.pentaho.profiling.services.webview/services/profileManagementViewService",
  "org.pentaho.profiling.services.webview/services/metricConfigViewService",
  "org.pentaho.profiling.services.webview/services/defaultMetricConfigViewService",
  "org.pentaho.profiling.services.webview/services/fieldOverviewViewService",
  "org.pentaho.profiling.services.webview/services/tabularViewService",
  "org.pentaho.profiling.services.webview/services/createProfilerViewService",
  "org.pentaho.profiling.services.webview/lib/angular.treeview"
], function (require, angular) {
  var provide = null,
      controllerProvider = null,
      profileApp = angular.module('profileApp', [
        'ngRoute',
        'ngSanitize', // for ngBindHtml
        'AppServices',
        'AppControllers',
        'pascalprecht.translate',
        'angularTreeview'
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
          .when('/tabular/:profileId', {
            templateUrl: 'partials/tabular-view.html',
            controller: 'TabularViewController'
          })
          .when('/profileManagement', {
            templateUrl: 'partials/profile-management-view.html',
            controller: 'ProfileManagementViewController'
          })
          .when('/fieldOverview/:profileId/:physicalName', {
            templateUrl: 'partials/field-overview-view.html',
            controller: 'FieldOverviewViewController'
          })
          .when('/metricConfig', {
            templateUrl: 'partials/metric-config-view.html',
            controller: 'MetricConfigViewController'
          })
          .when('/defaultMetricConfig', {
            templateUrl: 'partials/default-metric-config-view.html',
            controller: 'DefaultMetricConfigViewController'
          })
          .when('/pentahoStreamingProfiler', {
            templateUrl: 'partials/streaming-profiler-view.html',
            controller: 'StreamingProfilerViewController'
          })
          .when('/create', {
            templateUrl: 'partials/create-profiler-view.html',
            controller: 'CreateProfilerViewController'
          })
          .otherwise('/profileManagement', {
            templateUrl: 'partials/profile-management-view.html',
            controller: 'ProfileManagementViewController'
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
        angular.bootstrap(document.getElementById('profileApp'), ['profileApp']);
      });
    }
  };
});
