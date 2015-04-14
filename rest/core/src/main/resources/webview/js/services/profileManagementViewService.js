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

define(['./services'], function (appServices) {
  appServices.factory('ProfileManagementViewService', [
    function () {
      function ProfileManagementViewService() {
        this.activeProfiles = [];
        this.aggregateProfiles = [];
        this.availableProfiles = [];
        // treedata format
        // [
        //  { "name" : "User", "id" : "role1", "childProfiles" : [
        //    { "name" : "subUser1", "id" : "role11", "childProfiles" : [] },
        //    { "name" : "subUser2", "id" : "role12", "childProfiles" : [
        //      { "name" : "subUser2-1", "id" : "role121", "childProfiles" : [
        //        { "name" : "subUser2-1-1", "id" : "role1211", "childProfiles" : [] },
        //        { "name" : "subUser2-1-2", "id" : "role1212", "childProfiles" : [] }
        //      ]}
        //    ]}
        //  ]},
        //  { "name" : "Admin", "id" : "role2", "childProfiles" : [] },
        //  { "name" : "Guest", "id" : "role3", "childProfiles" : [] }
        //];
        this.currentProfileTreeViewData = [];
      }

      ProfileManagementViewService.prototype = {
        constructor: ProfileManagementViewService,
        buildProfileManagementViewServiceTreeViewSchemas: function () {
          var inAggregate = {};
          angular.forEach(profileManagementViewService.aggregateProfiles, function(aggregate) {
            function addChildrenRecursive(aggregateChild) {
              if(typeof aggregateChild.childProfiles !== "undefined") {
                angular.forEach(aggregateChild.childProfiles[1], function(childProfile) {
                  addChildrenRecursive(childProfile);
                });
                // ['java.util.ArrayList', [profiles]] -> [profiles]
                aggregateChild.childProfiles = aggregateChild.childProfiles[1];
              }
              inAggregate[aggregateChild.id] = true;
            }
            addChildrenRecursive(aggregate);
          });
          //Set the availableProfiles to the aggregateProfiles
          profileManagementViewService.availableProfiles = angular.copy(profileManagementViewService.aggregateProfiles);
          for (var i = 0, stopProfLoop = profileManagementViewService.activeProfiles.length; i < stopProfLoop; i++) {
            if (!inAggregate[profileManagementViewService.activeProfiles[i].id]) {
              profileManagementViewService.availableProfiles.push({
                "name": profileManagementViewService.activeProfiles[i].name,
                "id": profileManagementViewService.activeProfiles[i].id,
                "childProfiles": []
              });
            }
          }
        },
        searchAggregateProfilesRecursively: function (aggregateProfileArray, id) {
          for (var j = 0, stopAggLoop = aggregateProfileArray.length; j < stopAggLoop; j++) {
            if(typeof aggregateProfileArray[j].childProfiles !== "undefined") {
              if (aggregateProfileArray[j].childProfiles.length > 0) {
                profileManagementViewService.searchAggregateProfilesRecursively(aggregateProfileArray[j].childProfiles[1], id);
              }
              if (aggregateProfileArray[j].id === id) {
                return aggregateProfileArray[j];
              }
            }
          }
          return [];
        },
        getAvailableProfileIdsRecursively: function (availableProfileIdsArray) {
          for (var j = 0, stopAvailLoop = profileManagementViewService.availableProfiles.length; j < stopAvailLoop; j++) {
            if (profileManagementViewService.availableProfiles[j].childProfiles.length > 0) {
              profileManagementViewService.getAvailableProfileIdsRecursively(availableProfileIdsArray);
            }
            availableProfileIdsArray.push(profileManagementViewService.availableProfiles[j].id);
          }
          return availableProfileIdsArray;
        },
        setCurrentProfileTreeViewSchema: function (profileId) {
          profileManagementViewService.currentProfileTreeViewData = [profileManagementViewService.searchAggregateProfilesRecursively(profileManagementViewService.availableProfiles, profileId)];
        }
      };
      var profileManagementViewService = new ProfileManagementViewService();
      return profileManagementViewService;
    }])
});