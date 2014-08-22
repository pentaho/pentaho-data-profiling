'use-strict';

define(["angular", "angular-resource"], function(angular, angularResource){
  var pollerModule = angular.module('NotificationServiceModule', ['ngResource']);
  return pollerModule.factory('NotificationService', ['$http',
    function($http) {
      var registrations = {};
      var registrationNumber = 0;
      var shouldStop = false;
      var running = false;
      var responseMap = {};
      var singlePoll = function() {
        running = true;
        var data = {};
        for (key in registrations) {
          var registration = registrations[key];
          var interestedIds;
          if (registration.notificationType in data) {
            interestedIds = data[registration.notificationType];
          } else {
            interestedIds = {};
            data[registration.notificationType] = interestedIds;
          }
          for (id in registration.interestedIds) {
            if (id in interestedIds) {
              var oldTimestamp = interestedIds[id];
              interestedIds[id] = Math.min(oldTimestamp, registration.interestedIds[id]);
            } else {
              interestedIds[id] = registration.interestedIds[id];
            }
          };
        }
        var postData = [];
        for (notificationType in data) {
          var notificationTypeObject = {'notificationType': notificationType};
          var notificationTypeEntries = [];
          for (id in data[notificationType]) {
            notificationTypeEntries.push({'key': id, 'value': data[notificationType][id]});
          }
          postData.push({'notificationType': notificationType, 'entries': notificationTypeEntries});
        }
        $http({method: 'POST', url: '/cxf/notificationService', data: { 'notificationRequestWrapper' : { 'requests' : postData }},
                                        headers: {'Content-Type': 'application/json'}, timeout: 60 * 1000 }).success(
          function(data){
            data.notificationResponse.forEach(function(response){
              var notificationType = response.notificationType;
              var notificationTypeMap = responseMap[notificationType];
              var changedFunction = function(changedItem) {
                var id = changedItem.id;
                var timestamp = changedItem.timestamp;

                for (key in notificationTypeMap[id]) {
                  var registration = registrations[key];
                  if (registration.interestedIds[id] < timestamp) {
                    registration.interestedIds[id] = timestamp;
                    registration.callback(id);
                  };
                };
              };

            if (response.changedItems instanceof Array) {
              response.changedItems.forEach(changedFunction);
            } else {
              changedFunction(response.changedItems);
            }
          });
          if (!shouldStop) {
            singlePoll();
          } else {
            running = false;
          }
        });
      };

      var buildResponseMap = function() {
        var newResponseMap = {};
        for (key in registrations) {
          var registration = registrations[key];
          var notificationTypeMap = null;
          if (registration.notificationType in newResponseMap) {
            notificationTypeMap = newResponseMap[registration.notificationType];
          } else {
            notificationTypeMap = {};
            newResponseMap[registration.notificationType] = notificationTypeMap;
          }
          for (id in registration.interestedIds) {
            if (!(id in notificationTypeMap)) {
              notificationTypeMap[id] = {};
            }
            notificationTypeMap[id][key] = registration.interestedIds[id];
          }
        }
        responseMap = newResponseMap;
      };

      var register = function(notificationType, interestedIds, callback) {
        var interestedMap = {};
        interestedIds.forEach(function(id){
          interestedMap[id] = 0;
        });
        registrations[registrationNumber] = { 'notificationType': notificationType, 'interestedIds': interestedMap, 'callback': callback};
        buildResponseMap();
        if (Object.keys(registrations).length == 1) {
          shouldStop = false;
          if (!running) {
            singlePoll();
          }
        }
        return registrationNumber++;
      };

      var unregister = function(registrationId) {
        delete registrations[registrationId];
        buildResponseMap();
        if (Object.keys(registrations).length == 1) {
          shouldStop = true;
        }
      }
      return {
        'register': register,
        'unregister': unregister
      };
    }
  ]);
});