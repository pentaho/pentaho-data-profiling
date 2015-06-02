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

'use-strict';

define([
  "common-ui/angular",
  "common-ui/angular-route",
  "common-ui/angular-translate",
  "../services/services",
  "com.pentaho.profiling.notification.service",
  "org.pentaho.profiling.services.webview/lib/angular.treeview"
], function(angular) {

  return angular.module('AppControllers', [
    'NotificationServiceModule',
    'ngRoute',
    'pascalprecht.translate',
    'angularTreeview'
  ]);
});