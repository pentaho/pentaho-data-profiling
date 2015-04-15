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
        this.mongoHostViewService;
        this.hdfsTextHostViewService;
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
                        aProfileService, aDataSourceService, aNotificationService, aMongoHostViewService,
                        aHdfsTextHostViewService, aCreateProfilerViewService, scope) {
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
          profileAppService.mongoHostViewService = aMongoHostViewService;
          profileAppService.hdfsTextHostViewService = aHdfsTextHostViewService;
          profileAppService.createProfilerViewService = aCreateProfilerViewService;
          profileAppService.scope = scope;
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
        buildAvailableProfiles: function () {
          //Get all active Profiles
          profileAppService.profileService.profileResource.getActiveProfiles({}, function (activeProfiles) {
            profileAppService.profileManagementViewService.activeProfiles = [];
            angular.forEach(activeProfiles[1], function (value, key) {
              profileAppService.profileManagementViewService.activeProfiles.push(value);
            });
            //Get all aggregate Profiles
            profileAppService.profileService.aggregateProfileResource.getAggregates({}, function (aggregateProfiles) {
              profileAppService.profileManagementViewService.aggregateProfiles = [];
              angular.forEach(aggregateProfiles[1], function (value, key) {
                profileAppService.profileManagementViewService.aggregateProfiles.push(value);
              });
              profileAppService.profileManagementViewService.buildProfileManagementViewServiceTreeViewSchemas();
              profileAppService.profileManagementViewService.setCurrentProfileTreeViewSchema(profileAppService.profileId);
            });
          });
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
            case "hdfsText":
              if (profileAppService.hdfsTextHostViewService.pentahoHdfsTextProfilingHost !== "" &&
                  profileAppService.hdfsTextHostViewService.pentahoHdfsTextProfilingPort !== "") {
                profileAppService.redirectRoute("http://" +
                profileAppService.hdfsTextHostViewService.pentahoHdfsTextProfilingHost + ":" +
                profileAppService.hdfsTextHostViewService.pentahoHdfsTextProfilingPort + "/hdfsText/create.html");
              } else {
                alert('Please enter Host and Port Information.');
              }
              break;
            case "mongo":
              if (profileAppService.mongoHostViewService.pentahoMongoProfilingHost !== "" &&
                  profileAppService.mongoHostViewService.pentahoMongoProfilingPort !== "") {
                profileAppService.redirectRoute("http://" +
                profileAppService.mongoHostViewService.pentahoMongoProfilingHost + ":" +
                profileAppService.mongoHostViewService.pentahoMongoProfilingPort + "/mongoProfileWebView/create.html");
              } else {
                alert('Please enter Host and Port Information.');
              }
              break;
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