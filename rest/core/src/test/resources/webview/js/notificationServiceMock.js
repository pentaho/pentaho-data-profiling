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
