/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
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

define([
  "common-ui/angular",
  "common-ui/angular-resource"
], function(angular) {

  var pollerModule = angular.module('NotificationServiceModule', ['ngResource']);

  pollerModule.factory('NotificationService', function() {
    var nextRegNumber = 1;
    var registrationsByType, notifications;

    reset();

    function reset() {
      registrationsByType = {};
      notifications = [];
    }

    function register(notifType, interestedIds, callback) {
      getLazyArray(registrationsByType, notifType).push({
        type: notifType,
        callback: callback,
        interestedIds: interestedIds,
        callCount: 0
      });

      return nextRegNumber++;
    }

    function notify(type, id, content) {
      notifications.push({type: type, id: id, content: content});
    }

    function flush() {
      notifications.forEach(function (notif) {
        var regs = registrationsByType[notif.type];
        var any = false;
        if (regs) regs.forEach(function (reg) {
          if (reg.interestedIds.indexOf(notif.id) >= 0) {
            any = true;
            reg.callCount++;
            reg.callback.call(null, notif.content);
          }
        });

        if (!any) throw new Error("Unexpected notification of type '" + notif.type + "' and id '" + notif.id + "'.");
      });
    }

    function verifyNoUnfulfilledRegistrations() {
      // Any registration that wasn't called?
      for (var type in registrationsByType) {
        if (Object.prototype.hasOwnProperty.call(registrationsByType, type)) {
          var regs = registrationsByType[type];
          if (regs) regs.forEach(function (reg) {
            if (!reg.callCount) throw new Error("Unfulfilled registration for notification type '" + reg.type + "' and ids: [" + reg.interestedIds + "].");
          });
        }
      }
    }

    function verifyRegistrationExists(type, id) {
      var regs = registrationsByType[type];
      if(!regs || !regs.some(function(reg) { return reg.interestedIds.indexOf(id) >= 0; }))
        throw new Error("Expected a registration for notification type '" + type + "' and id '" + id + "'.");
    }

    return {
      register: register,

      unregister: function () {
      },

      // Test interface
      notify: notify,
      flush: flush,
      reset: reset,
      verifyNoUnfulfilledRegistrations: verifyNoUnfulfilledRegistrations,
      verifyRegistrationExists: verifyRegistrationExists
    };
  });

  return pollerModule;

  function getLazyArray(o, p) {
    return o[p] || (o[p] = []);
  }

});
