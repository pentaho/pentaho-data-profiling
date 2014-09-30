'use-strict';

define([
  "common-ui/angular"
], function(angular) {

  var translateModule = angular.module('pascalprecht.translate', []);

  translateModule.factory('$translate', [
    '$timeout',
    function($timeout) {

      function translate() {
      }

      translate.refresh = function() {
        return $timeout(function() { return "irrelevant"; });
      };

      translate.instant = function(key/*, params*/) {
        return key;
      };

      return translate;
    }
  ]);

  return translateModule;

});
