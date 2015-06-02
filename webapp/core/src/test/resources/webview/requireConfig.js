/*******************************************************************************
 *
 * Pentaho Data Profiling
 *
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
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


require.config({

  paths: {
    "testDataSourceGetIncludeModule": "/src/test/resources/webview/js/dataSourceGetIncludeModule",

    "com.pentaho.profiling.notification.service": "/src/test/resources/webview/js/notificationServiceMock",

    "org.pentaho.profiling.services.webview/lib/angular.treeview": "/src/main/resources/webview/js/lib/angular.treeview",

    "common-ui/angular-translate": "/src/test/resources/webview/js/angularTranslateMock",
    "common-ui/angular-translate-loader-partial": "/src/test/resources/webview/js/angularTranslatePartialLoaderMock",

    "common-ui/angular":          "/webjars/angular",
    "common-ui/angular-route":    "/webjars/angular-route",
    "common-ui/angular-resource": "/webjars/angular-resource",
    "common-ui/angular-mocks":    "/webjars/angular-mocks",
    'common-ui/jquery':           '/webjars/jquery'
  },
  shim: {
    "common-ui/angular":          {exports: "angular"},
    "common-ui/angular-route":    {deps: ["common-ui/angular"]},
    "common-ui/angular-resource": {deps: ["common-ui/angular"]},
    "common-ui/angular-mocks":    {deps: ["common-ui/angular"]},

    "common-ui/angular":          {exports: "angular", deps: ["common-ui/jquery"]}
  }
});