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
    function () {
      function ProfileAppService() {
        this.profileId;
        this.dataSourceUrl;
        this.dataSourceReference;
        this.statusMessages;
        this.operationError;
        this.tabularService;
        this.profileService;
        this.dataSourceService;
        this.notificationService;
        this.leftNavSelection;
      }

      ProfileAppService.prototype = {
        constructor: ProfileAppService,
        init: function (aTabularService, aProfileService, aDataSourceService, aNotificationService, scope) {
          profileAppService.leftNavSelection = "stats";
          //Because of the way we are using services as singleton instances of objects that are injectable, yet leverage the
          //dual binding that angular provides, we need to initialize the TabularService and set it on the ProfileAppService
          aTabularService.init(JSON.stringify(["name"]), false);
          profileAppService.tabularService = aTabularService;
          profileAppService.profileService = aProfileService;
          profileAppService.dataSourceService = aDataSourceService;
          profileAppService.notificationService = aNotificationService;
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
            profileAppService.updateDataSource(profileStatus.dataSourceReference);

            var cols = Pentaho.utilities.toArray(profileStatus.profileFieldProperties),
                colCount = cols && cols.length,
                itemSchema = profileAppService.tabularService.buildItemSchema(cols),
                items = Pentaho.utilities.toArray(profileStatus.fields);

            profileAppService.tabularService.fieldCols = profileAppService.tabularService.getCols(itemSchema, items, colCount);
            profileAppService.tabularService.fieldRows = profileAppService.tabularService.getRows(itemSchema, items);

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
        /**
         * Shows the "info" view of the given data source reference.
         *
         * When the given data source reference is different from the current data source reference,
         * the data source <i>getInclude</i> service is called to obtain
         * the new data source's requireJS module and view url.
         *
         * @param {DataSourceReference} dataSourceReference The data source reference.
         */
        updateDataSource: function (dataSourceReference) {
          var oldDsr = profileAppService.dataSourceReference,
              newDsId = dataSourceReference.id,
              newDsProv = dataSourceReference.dataSourceProvider;

          if (!oldDsr || oldDsr.id != newDsId) {
            profileAppService.dataSourceReference = dataSourceReference;

            if (!oldDsr || oldDsr.dataSourceProvider != newDsProv) {
              profileAppService.dataSourceService.getInclude({id: newDsId, dataSourceProvider: newDsProv}, function (dsIncludeWrapper) {

                var dsInclude = dsIncludeWrapper.profileDataSourceInclude;
                if (dsInclude.require) {
                  require([dsInclude.require], function () {
                    profileAppService.dataSourceUrl = dsInclude.url;
                    profileAppService.scope.$apply();
                  });
                } else {
                  profileAppService.dataSourceUrl = dsInclude.url;
                }
              });
            }
          }
        },
        /**
         * Orders to stop the current operation on the scope object's profile.
         *
         * This method is published in the scope object and can thus be called by the view.
         */
        stopCurrentOperation: function () {
          profileAppService.profileService.stop({profileId: profileAppService.profileId});
        }
      };
      var profileAppService = new ProfileAppService();
      return profileAppService;
    }])
});