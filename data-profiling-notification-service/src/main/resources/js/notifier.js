'use-strict';

define(["common-ui/angular", "common-ui/angular-resource"], function(angular) {

  var pollerModule = angular.module('NotificationServiceModule', ['ngResource']);

  pollerModule.factory('NotificationService', [
    '$http',
    function($http) {
      // {<regNumber>: {
      //    notificationType: <notifType>,
      //    callback:         <callback>,
      //    interestedIds: {<interestedId>: <latestTimestamp>}
      //  }
      // }
      var registrations = {};
      var registrationCount = 0;
      var nextRegNumber = 1;

      // Registrations indexed in a way that it
      // makes it direct to process poll responses.
      // Invalidated in `register` and in `unregister`.
      // Lazily created by getResponseMap.
      //
      // {<notifType>: {<interestedId>: [<registration>]}}
      var responseMap = null;

      // If we're currently polling.
      var running = false;

      function singlePoll() {
        var requestData = buildRequestData();

        running = true;

        $http({
          method: 'POST',
          url:    '/cxf/notificationService',
          data: {
            notificationRequestWrapper: {requests: requestData}
          },
          headers: {'Content-Type': 'application/json'},
          timeout: 60 * 1000
        })
        .success(function(respData) {
          processResponseData(respData);

          // Whenever there's a registration, there's need to keep polling.
          if(registrationCount) {
            singlePoll();
          } else {
            running = false;
          }
        });
      }

      function mergeRegistrations() {
        // {<notifType>: {<interestedId>: minTimestamp}}
        var mergedRegistrations = {};

        for(var regNumber in registrations) {
          var registration = registrations[regNumber],
              interestedIdsMap = registration.interestedIds,
              mergedInterestedIdsMap = getLazyMap(mergedRegistrations, registration.notificationType);

          for(var interestedId in interestedIdsMap) {
            var timestamp = interestedIdsMap[interestedId],
                minTimestamp = mergedInterestedIdsMap[interestedId];

            // For each interestedId, take the minimum timestamp, overall registrations.
            mergedInterestedIdsMap[interestedId] = minTimestamp == null
              ? timestamp
              : Math.min(minTimestamp, timestamp);
          }
        }

        return mergedRegistrations;
      }

      function buildRequestData() {
        var mergedRegistrations = mergeRegistrations();

        return Object.keys(mergedRegistrations).map(function(notifType) {

          var mergedInterestedIdsMap = mergedRegistrations[notifType],
              notifTypeEntries = Object.keys(mergedInterestedIdsMap).map(function(interestedId) {
                return {
                  key:   interestedId,
                  value: mergedInterestedIdsMap[interestedId] // minTimestamp
                };
              });

          return {notificationType: notifType, entries: notifTypeEntries};
        });
      }

      function processResponseData(respData) {
        var resps = respData && respData.notificationResponse;

        if(!resps || !resps.length) return;

        // Defensive copy.
        var respMap = getResponseMap();

        // May have unregistered while waiting.
        if(!respMap) return;

        resps.forEach(function(resp) {
          var respNotifTypeMap = respMap[resp.notificationType];

          var processChangedItem = function(changedItem) {
            var interestedId   = changedItem.id,
                timestamp      = changedItem.timestamp,
                interestedRegs = respNotifTypeMap[interestedId];

            // May have unregistered while waiting.
            if(interestedRegs) {
              interestedRegs.forEach(function(registration) {
                if(registration.interestedIds[interestedId] < timestamp) {
                  registration.interestedIds[interestedId] = timestamp;

                  registration.callback(interestedId);
                }
              });
            }
          };

          if(resp.changedItems instanceof Array) {
            resp.changedItems.forEach(processChangedItem);
          } else {
            processChangedItem(resp.changedItems);
          }
        });
      }

      function getResponseMap() {
        return responseMap || (responseMap = buildResponseMap());
      }

      function buildResponseMap() {
        // {<notifType>: {<interestedId>: [<registration>]}}
        var newResponseMap = null;

        if(registrationCount) {
          newResponseMap = {};

          for(var regNumber in registrations) {
            var registration = registrations[regNumber],
                newInterestedIdsMap = getLazyMap(newResponseMap, registration.notificationType);

            for(var interestedId in registration.interestedIds) {
              getLazyArray(newInterestedIdsMap, interestedId).push(registration);
            }
          }
        }

        return newResponseMap;
      }

      function invalidateResponseMap() {
          responseMap = null;
      }

      function register(notifType, interestedIds, callback) {
        var interestedIdsMap = {};
        interestedIds.forEach(function(id) {
          interestedIdsMap[id] = 0; // the oldest possible timestamp.
        });

        var regNumber = nextRegNumber++;
        // assert !registrations[regNumber];

        registrations[regNumber] = {
          notificationType: notifType,
          interestedIds: interestedIdsMap,
          callback: callback
        };
        registrationCount++;

        invalidateResponseMap();

        if(!running && registrationCount === 1) {
          singlePoll();
        }

        return regNumber;
      }

      function unregister(regNumber) {
        if(registrations[regNumber]) {
          delete registrations[regNumber];
          registrationCount--;

          invalidateResponseMap();
        }
      }

      return {
        register:   register,
        unregister: unregister
      };
    }
  ]);

  function getLazyMap(o, p) {
    return o[p] || (o[p] = {});
  }

  function getLazyArray(o, p) {
    return o[p] || (o[p] = []);
  }

  return pollerModule;
});