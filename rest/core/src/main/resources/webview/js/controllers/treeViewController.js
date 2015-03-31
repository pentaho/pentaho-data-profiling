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

define(['./controllers'], function (appControllers) {
  appControllers.controller('TreeViewController', [
    '$scope',
    'ProfileAppService',
    function ($scope, profileAppService) {
      $scope.$watch('uniqueTreeId.currentNode', function (newObj, oldObj) {
        var oldNotificationServiceRegNumber = profileAppService.notificationService.notificationServiceRegNumber;

        // Register to receive profile status updates of the new id.
        if (typeof $scope.uniqueTreeId.currentNode.id !== undefined) {
          profileAppService.notificationService.unregister(oldNotificationServiceRegNumber);
          profileAppService.notificationService.notificationServiceRegNumber = profileAppService.notificationService.register(
              /* notifType */
              "com.pentaho.profiling.model.ProfilingServiceImpl",
              /* ids */
              [$scope.uniqueTreeId.currentNode.id],
              /* cb */
              function (profileStatus) {
                profileAppService.updateProfile(profileStatus);
              });
        }
      }, false);
    }
  ])
});