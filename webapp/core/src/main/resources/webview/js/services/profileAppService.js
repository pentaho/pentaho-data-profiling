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

define(["require", './services'], function (require, appServices) {
  appServices.factory('ProfileAppService', [
    '$q',
    '$window',
    '$http',
    function ($q, $window, $http) {
      function ProfileAppService() {
        this.profileId;
        this.lastViewedField;
        this.dataSourceUrl;
        this.dataSourceReference;
        this.statusMessages;
        this.operationError;
        this.treeViewService;
        this.profileManagementViewService;
        this.fieldOverviewViewService;
        this.metricConfigViewService;
        this.defaultMetricConfigViewService;
        this.createProfilerViewService;
        this.tabularViewService;
        this.profileService;
        this.dataSourceService;
        this.notificationService;
        this.notificationServiceRegNumber;
        this.leftNavSelection;
        this.leftNavDisplay;
      }

      ProfileAppService.prototype = {
        constructor: ProfileAppService,
        init: function (aTabularViewService, aTreeViewService, aProfileManagementViewService,
                        aFieldOverviewViewService, aMetricConfigViewService, aDefaultMetricConfigViewService,
                        aProfileService, aDataSourceService, aNotificationService, aCreateProfilerViewService, scope) {
          //Because of the way we are using services as singleton instances of objects (to share a single truth
          //throughout the app) that are injectable, yet leverage the dual binding that angular provides, we need
          //to initialize the TabularViewService and set it on the ProfileAppService
          aTabularViewService.init(JSON.stringify(["name"]), false);

          profileAppService.tabularViewService = aTabularViewService;
          profileAppService.treeViewService = aTreeViewService;
          profileAppService.profileManagementViewService = aProfileManagementViewService;
          profileAppService.fieldOverviewViewService = aFieldOverviewViewService;
          profileAppService.metricConfigViewService = aMetricConfigViewService;
          profileAppService.defaultMetricConfigViewService = aDefaultMetricConfigViewService;
          profileAppService.profileService = aProfileService;
          profileAppService.dataSourceService = aDataSourceService;
          profileAppService.notificationService = aNotificationService;
          profileAppService.lastViewedField = "";
          profileAppService.createProfilerViewService = aCreateProfilerViewService;
          profileAppService.scope = scope;

          // Register to receive profile tree updates.
          profileAppService.notificationService.register("org.pentaho.profiling.services.ProfileTreeNotifier", ['profileTree'], function (profileTree) {
            profileAppService.updateAvailableProfiles(profileTree);
          });
        },
        /**
         * Updates the profile information with the given profile status object.
         *
         * This function is called each time a profile status update
         * is received by the profile notification service.
         *
         * It handles:
         * <ul>
         *   <li>obtaining available operations,</li>
         *   <li>determining the rows and columns structures corresponding to the fields in the profile,</li>
         *   <li>any errors reported by the profiling service.</li>
         * </ul>
         *
         * @param {ProfileStatus} profileStatus The profile status.
         */
        updateProfile: function (profileStatus) {
          if (profileStatus && profileStatus.profileState != 'DISCARDED') {
            profileAppService.profileId = profileStatus.id;
            profileAppService.profileManagementViewService.setCurrentProfileTreeViewSchema(profileAppService.profileId);

            // Update datasource.
            if (profileStatus.profileConfiguration) {
              if (profileStatus.profileConfiguration.dataSourceMetadata) {
                profileAppService.tabularViewService.dsLabel = profileStatus.profileConfiguration.dataSourceMetadata.label;
              }
            }

            var cols = Pentaho.utilities.toArray(profileStatus.profileFieldProperties),
                colCount = cols && cols.length,
                itemSchema = profileAppService.tabularViewService.buildItemSchema(cols),
                items = Pentaho.utilities.toArray(profileStatus.fields);

            profileAppService.tabularViewService.fieldCols = profileAppService.tabularViewService.getCols(itemSchema, items, colCount);
            profileAppService.tabularViewService.fieldRows = profileAppService.tabularViewService.getRows(itemSchema, items);

            if (profileAppService.lastViewedField === "") {
              //Defualt to the first row/field
              if (profileAppService.tabularViewService.fieldRows.length > 0 && profileAppService.tabularViewService.fieldCols.length > 0) {
                profileAppService.setLastViewedField(profileAppService.tabularViewService.fieldRows[0][profileAppService.tabularViewService.fieldCols[0].stringifiedPath]);
              }
            }

            profileAppService.statusMessages = profileStatus.statusMessages;

            profileAppService.operationError = profileStatus.operationError;
          } else {
            profileAppService.dataSourceService.getCreate({
              id: profileAppService.dataSourceReference.id,
              dataSourceProvider: profileAppService.dataSourceReference.dataSourceProvider
            }, function (createWrapper) {
              window.location.href = createWrapper.profileDataSourceInclude.url;
            });
          }
        },
        setLastViewedField: function (path) {
          profileAppService.lastViewedField = path;
        },
        updateAvailableProfiles: function (profileTree) {
          profileAppService.profileManagementViewService.buildProfileManagementViewServiceTreeViewSchemas(profileTree);
          profileAppService.profileManagementViewService.setCurrentProfileTreeViewSchema(profileAppService.profileId);
        },
        /**
         * Orders to stop the operation on the profile id.
         */
        stopOperation: function (profileId) {
          profileAppService.profileService.profileResource.stopProfile(profileId, {});
        },
        /**
         * Orders to stop the operation for all active profile ids.
         */
        stopAllOperation: function () {
          var availableProfileIdsArray = [];
          availableProfileIdsArray = profileAppService.profileManagementViewService.getAvailableProfileIdsRecursively(availableProfileIdsArray);
          var promises = availableProfileIdsArray.map(function (id) {
            var deferred = $q.defer();

            profileAppService.profileService.profileResource.stopProfile(id, {}).
                success(function (data, status, headers, config) {
                  deferred.resolve(data);
                }).
                error(function (data, status, headers, config) {
                  // called asynchronously if an error occurs
                  deferred.reject();
                });

            return deferred.promise;
          });
          $q.all(promises).error(function (data, status, headers, config) {
            // called asynchronously if an error occurs
          });
        },
        /**
         * Sets the default metric contributor configuration.
         */
        submitDefaultMetricContributorConfig: function () {
          profileAppService.profileService.metricContributorResource.setDefaultMetricContributorConfig(
              profileAppService.metricConfigViewService.metricContributorConfig,
              function () {
                profileAppService.redirectRoute("view.html#/defaultMetricConfig");
              });
        },
        redirectRoute: function (path) {
          $window.location.href = path;
        },
        createProfiler: function (type) {
          switch (type) {
            default:
              alert('Default case');
              break;
          }

        },
        /**
         * Register the profile id with the notification service.
         */
        register: function (notifType, ids, callback) {
          profileAppService.notificationService.unregister(profileAppService.notificationService.notificationServiceRegNumber);
          profileAppService.notificationService.notificationServiceRegNumber = profileAppService.notificationService.register(
              /* notifType */
              notifType,
              /* ids */
              ids,
              /* cb */
              callback);
        }
      };
      var profileAppService = new ProfileAppService();
      return profileAppService;
    }])
});