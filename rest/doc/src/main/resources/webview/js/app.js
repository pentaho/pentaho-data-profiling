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

define([
    "require",
    "common-ui/angular",
    "common-ui/properties-parser",
    "common-ui/angular-route",
    "common-ui/angular-translate",
    "common-ui/angular-translate-loader-static",
    "./controllers"
  ], function(require, angular, propertiesParser) {

  var profileApp = angular.module('docApp', [
      'ngRoute',
      'appControllers',
      'pascalprecht.translate'
    ]);

  profileApp.config([
    '$translateProvider',
    function($translateProvider) {
      $translateProvider
        .useStaticFilesLoader({
          prefix: '../cxf/i18n/data-profiling/com.pentaho.profiling.hdfs.text.appmaster.service.messages/',
          suffix: ''
        })
        .preferredLanguage('en')
        .fallbackLanguage('en');
    }
  ]);

  profileApp.directive('doctable', function() {
    return {
      restrict: 'E',
      scope: { tableData: '=data' },
      templateUrl: 'templates/table.html'
    }
  });

  profileApp.value('$translatePropertiesFormatAdapter', propertiesParser);

  return {
    app: profileApp,

    init: function() {
      angular.element(document).ready(function() {
        angular.bootstrap(document.getElementById('appBody'), ['docApp']);
      });
    }
  };
});