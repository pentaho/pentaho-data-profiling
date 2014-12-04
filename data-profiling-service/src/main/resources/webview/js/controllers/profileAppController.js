define(['./controllers'], function (appControllers) {
  appControllers.controller('profileAppController', [
    '$scope',
    'ProfileService',
    'DataSourceService',
    'NotificationService',
    'ProfileAppService',
    'TabularService',
    function ($scope, profileService, dataSourceService, notificationService, profileAppService, tabularService) {

      profileAppService.init(tabularService, profileService, dataSourceService, notificationService);
      $scope.profileAppService = profileAppService;
    }
  ])
});