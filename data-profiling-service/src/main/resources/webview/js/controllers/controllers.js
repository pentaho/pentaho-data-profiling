'use-strict';

define([
  "common-ui/angular",
  "common-ui/angular-route",
  "common-ui/angular-translate",
  "common-ui/angular-translate-loader-partial",
  "../services/services",
  "com.pentaho.profiling.notification.service"
], function(angular) {

  return angular.module('appControllers', [
    'NotificationServiceModule',
    'ngRoute',
    'pascalprecht.translate'
  ]);
});