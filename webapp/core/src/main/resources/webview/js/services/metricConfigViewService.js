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
          "javaClass": "org.pentaho.model.metrics.contributor.metricManager.impl.RegexAddressMetricContributor",
          "name": "RegexAddressMetricContributor",
          "regex": "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",
          "profileFieldProperty": {
            "javaClass": "org.pentaho.profiling.api.ProfileFieldProperty",
            "namePath": "profiling-metrics/org.pentaho.model.metrics.contributor.metricManager.impl.messages",
            "nameKey": "EmailAddressMetricContributor",
            "pathToProperty": [
              "java.util.ArrayList",
              [
                "type",
                "org.pentaho.str.email_address"
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
          metricConfigViewService.metricContributorConfig.metricManagerContributors = metricConfigViewService.tmpMetricManagerContributors;
          metricConfigViewService.metricContributorConfig.metricContributors = metricConfigViewService.tmpMetricContributors;
        },
        resetSampleRegexMetricContributor: function() {
          metricConfigViewService.sampleRegexMetricContributor = {
            "javaClass": "org.pentaho.model.metrics.contributor.metricManager.impl.RegexAddressMetricContributor",
            "name": "RegexAddressMetricContributor",
            "regex": "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",
            "profileFieldProperty": {
              "javaClass": "org.pentaho.profiling.api.ProfileFieldProperty",
              "namePath": "profiling-metrics/org.pentaho.model.metrics.contributor.metricManager.impl.messages",
              "nameKey": "EmailAddressMetricContributor",
              "pathToProperty": [
                "java.util.ArrayList",
                [
                  "type",
                  "org.pentaho.str.email_address"
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
              metricConfigViewService.sampleRegexMetricContributor.profileFieldProperty.pathToProperty.length > 0 &&
              metricConfigViewService.sampleRegexMetricContributor.supportedTypes.length > 0) {
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