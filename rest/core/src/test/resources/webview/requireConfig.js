
require.config({

  paths: {
    "testDataSourceGetIncludeModule": "/src/test/resources/webview/js/dataSourceGetIncludeModule",

    "com.pentaho.profiling.notification.service": "/src/test/resources/webview/js/notificationServiceMock",

    "common-ui/angular-translate": "/src/test/resources/webview/js/angularTranslateMock",
    "common-ui/angular-translate-loader-partial": "/src/test/resources/webview/js/angularTranslatePartialLoaderMock",

    "common-ui/angular":          "/webjars/angular",
    "common-ui/angular-route":    "/webjars/angular-route",
    "common-ui/angular-resource": "/webjars/angular-resource",
    "common-ui/angular-mocks":    "/webjars/angular-mocks"
  },
  shim: {
    "common-ui/angular":          {exports: "angular"},
    "common-ui/angular-route":    {deps: ["common-ui/angular"]},
    "common-ui/angular-resource": {deps: ["common-ui/angular"]},
    "common-ui/angular-mocks":    {deps: ["common-ui/angular"]}
  }
});