'use-strict';

define([
  "common-ui/angular-translate"
], function(translateModule) {

  translateModule.factory('$translatePartialLoader', function() {

    var parts = {};

    return {
      isPartAvailable: function(partId) {
        return parts[partId] === 1;
      },

      addPart: function(partId) {
        parts[partId] = 1;
      }
    };
  });

  return translateModule;
});
