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
  appServices.factory('MetricConfigViewService', [
    function () {
      function MetricConfigViewService() {
        this.metricContributorConfig;
        this.tmpMetricManagerContributors;
        this.tmpMetricContributors;
        this.metricManagerContributors;
        this.metricContributors;
        this.sampleRegexMetricContributor = {
          "javaClass": "com.pentaho.model.metrics.contributor.metricManager.impl.RegexAddressMetricContributor",
          "name": "RegexAddressMetricContributor",
          "regex": "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",
          "profileFieldProperty": {
            "javaClass": "com.pentaho.profiling.api.ProfileFieldProperty",
            "namePath": "profiling-metrics/com.pentaho.model.metrics.contributor.metricManager.impl.messages",
            "nameKey": "EmailAddressMetricContributor",
            "pathToProperty": [
              "java.util.ArrayList",
              [
                "type",
                "com.pentaho.str.email_address"
              ]
            ]
          },
          "supportedTypes": [
            "java.util.HashSet",
            [
              "java.lang.String"
            ]
          ]
        };
      }

      MetricConfigViewService.prototype = {
        constructor: MetricConfigViewService,
        setConfig: function () {
          metricConfigViewService.tmpMetricManagerContributors = [];
          metricConfigViewService.tmpMetricContributors = [];
          angular.forEach(metricConfigViewService.metricManagerContributors, function (metricManagerContributor) {
            if (metricManagerContributor.checked !== 'undefined') {
              if (metricManagerContributor.checked) {
                //The 'checked' property was added so the UI will know what Metric Contributors to POST
                //but because of the way the server uses Jackson to create POJO's we need to remove this property
                delete metricManagerContributor.checked;
                metricConfigViewService.tmpMetricManagerContributors.push(metricManagerContributor);
              }
            }
          });
          angular.forEach(metricConfigViewService.metricContributors, function (metricContributor) {
            if (metricContributor.checked !== 'undefined') {
              if (metricContributor.checked) {
                //The 'checked' property was added so the UI will know what Metric Contributors to POST
                //but because of the way the server uses Jackson to create POJO's we need to remove this property
                delete metricContributor.checked;
                metricConfigViewService.tmpMetricContributors.push(metricContributor);
              }
            }
          });
          metricConfigViewService.metricContributorConfig.metricManagerContributors[1] = metricConfigViewService.tmpMetricManagerContributors;
          metricConfigViewService.metricContributorConfig.metricContributors[1] = metricConfigViewService.tmpMetricContributors;
        },
        resetSampleRegexMetricContributor: function() {
          metricConfigViewService.sampleRegexMetricContributor = {
            "javaClass": "com.pentaho.model.metrics.contributor.metricManager.impl.RegexAddressMetricContributor",
            "name": "RegexAddressMetricContributor",
            "regex": "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",
            "profileFieldProperty": {
              "javaClass": "com.pentaho.profiling.api.ProfileFieldProperty",
              "namePath": "profiling-metrics/com.pentaho.model.metrics.contributor.metricManager.impl.messages",
              "nameKey": "EmailAddressMetricContributor",
              "pathToProperty": [
                "java.util.ArrayList",
                [
                  "type",
                  "com.pentaho.str.email_address"
                ]
              ]
            },
            "supportedTypes": [
              "java.util.HashSet",
              [
                "java.lang.String"
              ]
            ]
          };
        },
        addRegexToConfig: function () {
          if (metricConfigViewService.sampleRegexMetricContributor.regex !== "" &&
              metricConfigViewService.sampleRegexMetricContributor.profileFieldProperty.nameKey !== "" &&
              metricConfigViewService.sampleRegexMetricContributor.profileFieldProperty.pathToProperty[1].length > 0 &&
              metricConfigViewService.sampleRegexMetricContributor.supportedTypes[1].length > 0) {
            metricConfigViewService.metricContributorConfig.metricManagerContributors.push(metricConfigViewService.sampleRegexMetricContributor);
            metricConfigViewService.resetSampleRegexMetricContributor();
            metricConfigViewService.createRegex = false;
          } else {
            alert('Please enter Host and Port Information.');
          }
        }
      };
      var metricConfigViewService = new MetricConfigViewService();
      return metricConfigViewService;
    }])
});