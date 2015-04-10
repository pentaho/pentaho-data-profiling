/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2015 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */

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
  "com.pentaho.profiling.services.webview/controllers/tabularViewController",
  "com.pentaho.profiling.services.webview/controllers/profileManagementViewController",
  "com.pentaho.profiling.services.webview/controllers/fieldOverviewViewController",
  "com.pentaho.profiling.services.webview/controllers/metricConfigViewController",
  "com.pentaho.profiling.services.webview/controllers/defaultMetricConfigViewController",
  "com.pentaho.profiling.services.webview/controllers/treeViewController",
  "com.pentaho.profiling.services.webview/controllers/mongoHostViewController",
  "com.pentaho.profiling.services.webview/controllers/hdfsTextHostViewController",
  "com.pentaho.profiling.services.webview/controllers/streamingProfilerViewController",
  "com.pentaho.profiling.services.webview/controllers/createProfilerViewController",
  "com.pentaho.profiling.services.webview/services/services",
  "com.pentaho.profiling.services.webview/services/profileService",
  "com.pentaho.profiling.services.webview/services/dataSourceService",
  "com.pentaho.profiling.services.webview/services/profileAppService",
  "com.pentaho.profiling.services.webview/services/treeViewService",
  "com.pentaho.profiling.services.webview/services/profileManagementViewService",
  "com.pentaho.profiling.services.webview/services/metricConfigViewService",
  "com.pentaho.profiling.services.webview/services/defaultMetricConfigViewService",
  "com.pentaho.profiling.services.webview/services/fieldOverviewViewService",
  "com.pentaho.profiling.services.webview/services/tabularViewService",
  "com.pentaho.profiling.services.webview/services/mongoHostViewService",
  "com.pentaho.profiling.services.webview/services/hdfsTextHostViewService",
  "com.pentaho.profiling.services.webview/services/streamingProfilerViewService",
  "com.pentaho.profiling.services.webview/services/createProfilerViewService",
  "com.pentaho.profiling.services.webview/lib/angular.treeview"
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
          .when('/pentahoMongoProfiler', {
            templateUrl: 'partials/mongo-host-view.html',
            controller: 'MongoHostViewController'
          })
          .when('/pentahoHdfsTextProfiler', {
            templateUrl: 'partials/hdfs-text-host-view.html',
            controller: 'HdfsTextHostViewController'
          })
          .when('/pentahoStreamingProfiler', {
            templateUrl: 'partials/streaming-profiler-view.html',
            controller: 'StreamingProfilerViewController'
          })
          .when('/create', {
            templateUrl: 'partials/create-profiler-view.html',
            controller: 'CreateProfilerViewController'
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
        angular.bootstrap(document.getElementById('profileApp'), ['profileApp']);
      });
    }
  };
});