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

define(['./services'], function (appServices) {
  appServices.factory('StreamingProfilerViewService', [
    function () {
      function StreamingProfilerViewService() {
        this.webServiceUrl = "";
        this.flattenFunction = "'use strict';" +
        "angular.forEach(data, function (value, key) {" +
        "var newPrefix = angular.copy(prefix);" +
        "newPrefix.push(key);" +
        "flatten(newPrefix, data[key], result)" +
        "});" +
        "var name = prefix.join('.');" +
        "result.push({" +
        "'javaClass': 'com.pentaho.profiling.api.metrics.field.DataSourceFieldValue'," +
        "'logicalName': prefix[prefix.length-1]," +
        "'physicalName': name," +
        "'fieldValue': data" +
        "});";
        this.postData = "{}";
      }

      StreamingProfilerViewService.prototype = {
        constructor: StreamingProfilerViewService,
        flatten: function (prefix, data, result) {
          angular.forEach(data, function (value, key) {
            var newPrefix = angular.copy(prefix);
            newPrefix.push(key);
            flatten(newPrefix, data[key], result)
          });
          var name = prefix.join('.');

          result.push({
            'javaClass': 'com.pentaho.profiling.api.metrics.field.DataSourceFieldValue',
            'logicalName': prefix[prefix.length - 1],
            'physicalName': name,
            'fieldValue': data
          });
        }
      }
      var streamingProfilerViewService = new StreamingProfilerViewService();
      return streamingProfilerViewService;
    }])
});