define(['./services'], function (appServices) {
  appServices.factory('DataSourceService', ['$resource',
    function ($resource) {
      return $resource('../cxf/data-profiling-service/dataSource/include/:id/:dataSourceProvider', {}, {
        getInclude: {method: 'GET', params: {id: 'id', dataSourceProvider: 'dataSourceProvider'}},
        getCreate: {method: 'GET', params: {id: 'id', dataSourceProvider: 'dataSourceProvider'}, url: '../cxf/data-profiling-service/dataSource/create/:id/:dataSourceProvider' }
      });
    }])
});