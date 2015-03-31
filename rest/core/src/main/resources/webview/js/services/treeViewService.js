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
  appServices.factory('TreeViewService', [
    function () {
      function TreeViewService() {
        // treedata format
        // [
        //  { "name" : "User", "id" : "role1", "children" : [
        //    { "name" : "subUser1", "id" : "role11", "children" : [] },
        //    { "name" : "subUser2", "id" : "role12", "children" : [
        //      { "name" : "subUser2-1", "id" : "role121", "children" : [
        //        { "name" : "subUser2-1-1", "id" : "role1211", "children" : [] },
        //        { "name" : "subUser2-1-2", "id" : "role1212", "children" : [] }
        //      ]}
        //    ]}
        //  ]},
        //  { "name" : "Admin", "id" : "role2", "children" : [] },
        //  { "name" : "Guest", "id" : "role3", "children" : [] }
        //];
        this.treedata = [{"name" : "", "id" : "", "childProfile" : []}];
      }

      TreeViewService.prototype = {
        constructor: TreeViewService,
        buildTreeViewSchema: function (aggregateProfiles, profileStatus) {
          treeViewService.treedata = [];
          if (aggregateProfiles === ""){
            treeViewService.treedata.push({"name" : profileStatus.name, "id" : profileStatus.id, "childProfile" : []});
          } else {
            treeViewService.treedata.push(aggregateProfiles);
          }
        }
      };
      var treeViewService = new TreeViewService();
      return treeViewService;
    }])
});