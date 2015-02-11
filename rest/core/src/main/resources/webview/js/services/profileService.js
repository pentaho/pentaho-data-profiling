define(['./services'], function (appServices) {
  appServices.factory('ProfileService', ['$resource',
    function ($resource) {
      return $resource('../cxf/profile/:profileId', {}, {
        query: {method: 'GET', params: {profileId: 'profileId'}},
        stop: {method: 'PUT', url: '../cxf/profile/stop' },
        start: {method: 'PUT', url: '../cxf/profile/start' }
      });
    }])
});