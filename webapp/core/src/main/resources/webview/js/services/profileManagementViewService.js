/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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
        buildProfileManagementViewServiceTreeViewSchemas: function (profileTree) {
          function copyList(nodes) {
            var result = []
            if (nodes) {
              result = nodes.map(function(node) {
                return copyNode(node);
              }).filter(function (node) {
                return node != null;
              })
            }
            return result
          }

          function copyNode(node) {
            if (!node || !node.name) {
              return null;
            }
            return {
              name: node.name,
              id: node.id,
              childProfiles: copyList(node.childProfiles)
            }
          }
          //Set the availableProfiles to the aggregateProfiles
          profileManagementViewService.availableProfiles = copyList(profileTree);
        },
        searchAggregateProfilesRecursively: function (profiles, id) {
          function childrenContainsProfileId(child) {
            var result = false;
            if (child.id == id) {
              child.selected = 'selected';
              result = true;
            } else {
              delete child['selected'];
            }
            if (typeof child.childProfiles !== "undefined") {
              for (var j = 0, stopChildLoop = child.childProfiles.length; j < stopChildLoop; j++) {
                if (childrenContainsProfileId(child.childProfiles[j])) {
                  result = true;
                }
              }
            }
            return result;
          }
          var result = []
          for (var j = 0, stopLoop = profiles.length; j < stopLoop; j++) {
            if (childrenContainsProfileId(profiles[j])) {
              result = profiles[j];
            }
          }
          return result;
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