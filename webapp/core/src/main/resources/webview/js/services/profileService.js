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
  appServices.factory('ProfileService', ['$resource', '$http',
    function ($resource, $http) {
      var profileResource = $resource('../cxf/profile/:profileId', {}, {
        getActiveProfiles: {method: 'GET', url: '../cxf/profile', isArray: true},
        getProfile: {method: 'GET'},
        createProfile: {method: 'POST', url: '../cxf/profile'}
      });
      profileResource.stopProfile = function (id, data) {
        $http.put("../cxf/profile/stop/"+id, data)
      }
      var aggregateProfileResource = $resource('../cxf/aggregate/:profileId', {}, {
        getAggregates: {method: 'GET', url: '../cxf/aggregate', isArray: true},
        getAggregate: {method: 'GET', params: {profileId: 'profileId'}, isArray: true}
      });
      var metricContributorResource = $resource('../cxf/metrics', {}, {
        getDefaultMetricContributorConfig: {method: 'GET', url: '../cxf/metrics/default'},
        setDefaultMetricContributorConfig: {method: 'POST', url: '../cxf/metrics/default'},
        getAllAvailableMetricContributorConfig: {method: 'GET', url: '../cxf/metrics/full'}
      });

      function ProfileService() {
      }

      ProfileService.prototype = {
        constructor: ProfileService,
        aggregateProfileResource: aggregateProfileResource,
        profileResource: profileResource,
        metricContributorResource: metricContributorResource
      };
      var profileService = new ProfileService();
      return profileService;
    }])
});