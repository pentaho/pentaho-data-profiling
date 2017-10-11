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

define(['./controllers'], function (appControllers) {
  appControllers.controller('FieldOverviewViewController', [
    '$scope',
    'ProfileAppService',
    '$routeParams',
    function ($scope, profileAppService, $routeParams) {
      $scope.profileAppService = profileAppService;
      profileAppService.fieldOverviewViewService.physicalName = $routeParams.physicalName;
      //I don't know if this is what we want...but probably the alternative would be profileAppService.fieldOverviewViewService.profileId = $routeParams.profileId;
      profileAppService.profileId = $routeParams.profileId;

      profileAppService.fieldOverviewViewService.setCurrentFieldRow(profileAppService.tabularViewService.fieldRows, profileAppService.fieldOverviewViewService.physicalName);

      profileAppService.leftNavSelection = "overview";

      profileAppService.leftNavDisplay = true;
    }
  ])
});