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

'use-strict';
// UTILITIES
var Pentaho = Pentaho || {};

Pentaho.utilities = {
  toArray: function (t) {
    return (t == null || Array.isArray(t)) ? t : [t];
  },
  append: function (a1, a2) {
    for (var i = 0, L = a2.length; i < L; i++) a1.push(a2[i]);
    return a1;
  },
  compare: function (a, b) {
    return a > b ? 1 : a < b ? -1 : 0;
  }
};

define([
  'common-ui/angular',
  'common-ui/angular-route',
  'common-ui/angular-mocks',
  'controllers/controllers',
  'controllers/profileAppController',
  'controllers/tabularViewController',
  'controllers/treeViewController',
  'controllers/profileManagementViewController',
  'controllers/fieldOverviewViewController',
  'controllers/metricConfigViewController',
  'controllers/defaultMetricConfigViewController',
  'controllers/mongoHostViewController',
  'controllers/hdfsTextHostViewController',
  'controllers/streamingProfilerViewController',
  'controllers/createProfilerViewController',
  'services/services',
  'services/profileService',
  'services/dataSourceService',
  'services/profileAppService',
  'services/tabularViewService',
  'services/treeViewService',
  'services/profileManagementViewService',
  'services/fieldOverviewViewService',
  'services/mongoHostViewService',
  'services/hdfsTextHostViewService',
  'services/streamingProfilerViewService',
  'services/createProfilerViewService',
  'services/metricConfigViewService',
  'services/defaultMetricConfigViewService'
], function (angular) {

  var PROFILE_STATUS_NOTIF_STYPE = "com.pentaho.profiling.model.ProfilingServiceImpl";
  var PROFILE_GETAGGPROF_URL = "../cxf/aggregate";
  var PROFILE_GETPROF_URL = "../cxf/profile";
  var PROFILE_GETOPERS_URL = "../cxf/profile/operations/";
  var DATASOURCE_GETINCLUDE_URL = "../cxf/data-profiling-service/dataSource/include/";

  describe("Profiling Service ProfileAppController -", function () {

    angular.module('profileApp', [
      'ngRoute',
      'AppServices',
      'AppControllers',
      'pascalprecht.translate'
    ]);

    var $scope, $rootScope, $httpBackend, $timeout, $routeParams, notificationService;

    beforeEach(module("profileApp"));

    beforeEach(inject(function (_$rootScope_, $controller, _$httpBackend_, _$timeout_, _$routeParams_, NotificationService) {
      $rootScope = _$rootScope_;
      $scope = _$rootScope_.$new();
      $httpBackend = _$httpBackend_;
      $timeout = _$timeout_;
      $routeParams = _$routeParams_;
      notificationService = NotificationService;

      $routeParams.profileId = 'ABCD';

      $controller("ProfileAppController", {$scope: $scope});
      $controller("TabularViewController", {$scope: $scope});

      spyOn($scope.profileAppService.profileService.profileResource, 'getActiveProfiles').andCallThrough();
      spyOn($scope.profileAppService.profileService.aggregateProfileResource, 'getAggregates').andCallThrough();
    }));

    describe("scope object initialization -", function () {

      it("should have a property 'isOrderReversed' with value false", function () {
        expect($scope.profileAppService.tabularViewService.isOrderReversed).toBe(false);
      });

      it("should have a property 'orderByCol' with value [\"aFieldName\"]", function () {
        expect(/^\["[^"]+"\]$/.test($scope.profileAppService.tabularViewService.orderByCol)).toBe(true);
      });

      it("should not have a property 'profileId'", function () {
        expect($scope.profileAppService.profileId).toBeUndefined();
      });

      it("should register for a ProfileStatus notification for id 'ABCD'", function () {
        notificationService.verifyRegistrationExists(PROFILE_STATUS_NOTIF_STYPE, "ABCD");
      });
    });

    describe("when a ProfileStatus notification arrives -", function () {

      it("should set the scope property 'profileId' to 'ABCD'", function () {
        notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
          id: "ABCD",
          dataSourceReference: {
            id: 'abc',
            dataSourceProvider: 'cde'
          },
          profileFieldProperties: [
            {pathToProperty: ['name']},
            {pathToProperty: ['bag', 'needle']}
          ],
          fields: [
            {values: {name: 'A'}},
            {values: {name: 'B'}}
          ]
        });

        notificationService.flush();

        expect($scope.profileAppService.profileId).toBe('ABCD');

        notificationService.verifyNoUnfulfilledRegistrations();
      });

      it("should set the scope property 'statusMessages'", function () {
        var currentOper = {
          messageKey: 'A',
          messagePath: '/A',
          messageVariables: []
        };

        var statusMessages = [currentOper];

        notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
          id: "ABCD",
          dataSourceReference: {
            id: 'abc',
            dataSourceProvider: 'cde'
          },
          profileFieldProperties: [
            {pathToProperty: ['name']},
            {pathToProperty: ['bag', 'needle']}
          ],
          fields: [
            {values: {name: 'A'}},
            {values: {name: 'B'}}
          ],
          statusMessages: statusMessages
        });

        notificationService.flush();

        expect($scope.profileAppService.statusMessages).toBe(statusMessages);
      });

      it("should set the scope property 'operationError'", function () {
        var operationError = {
          message: {
            messageKey: 'A',
            messagePath: '/A',
            messageVariables: []
          },
          recoveryOperations: [
            {id: 'A', nameKey: 'B', namePath: '/B/C'}
          ]
        };

        notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
          id: "ABCD",
          dataSourceReference: {
            id: 'abc',
            dataSourceProvider: 'cde'
          },
          profileFieldProperties: [
            {pathToProperty: ['name']},
            {pathToProperty: ['bag', 'needle']}
          ],
          fields: [
            {values: {name: 'A'}},
            {values: {name: 'B'}}
          ],
          operationError: operationError
        });

        notificationService.flush();

        expect($scope.profileAppService.operationError).toBe(operationError);
      });

      it("should issue GET on the data source getInclude service", function () {
        notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
          id: "ABCD",
          dataSourceReference: {
            id: 'abc',
            dataSourceProvider: 'cde'
          },
          profileFieldProperties: [
            {pathToProperty: ['name']},
            {pathToProperty: ['bag', 'needle']}
          ],
          fields: [
            {values: {name: 'A'}},
            {values: {name: 'B'}}
          ]
        });

        $httpBackend.expectGET(DATASOURCE_GETINCLUDE_URL + 'abc/cde').respond("");
        $httpBackend.expectGET(PROFILE_GETPROF_URL).respond([]);
        //$httpBackend.whenGET(PROFILE_GETPROF_URL).respond([]);
        //$httpBackend.expectGET(PROFILE_GETAGGPROF_URL).respond([]);
        //$httpBackend.whenGET(PROFILE_GETAGGPROF_URL).respond([]);
        $httpBackend.whenGET(PROFILE_GETOPERS_URL + 'ABCD').respond("");

        notificationService.flush();
        //$httpBackend.expectGET(PROFILE_GETAGGPROF_URL).respond([]);
        $httpBackend.verifyNoOutstandingExpectation();
      });

      describe("the profile field columns, determined based on the notification's 'profileFieldProperties' and 'fields' - ", function () {

        it("should be an array", function () {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
            id: "ABCD",
            dataSourceReference: {id: 'abc', dataSourceProvider: 'cde'},
            profileFieldProperties: [
              {pathToProperty: ['name']},
              {pathToProperty: ['bag', 'needle']}
            ],
            fields: [
              {values: {name: 'A'}},
              {values: {name: 'B'}}
            ]
          });

          notificationService.flush();

          expect($scope.profileAppService.tabularViewService.fieldCols instanceof Array).toBe(true);
        });

        it("should only contain columns for which at least on field has a value for", function () {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
            id: "ABCD",
            dataSourceReference: {id: 'abc', dataSourceProvider: 'cde'},
            profileFieldProperties: [
              {pathToProperty: ['name']},
              {pathToProperty: ['bag', 'needle']}
            ],
            fields: [
              {values: {name: 'A'}},
              {values: {name: 'B'}}
            ]
          });

          notificationService.flush();

          expect($scope.profileAppService.tabularViewService.fieldCols.length).toBe(1);
          var col = $scope.profileAppService.tabularViewService.fieldCols[0];
          expect(col.stringifiedPath).toBe(JSON.stringify(['name']));
        });

        it("should contain columns with all expected attributes", function () {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, 'ABCD', {
            id: "ABCD",
            dataSourceReference: {id: 'abc', dataSourceProvider: 'cde'},
            profileFieldProperties: [
              {pathToProperty: ['name'], nameKey: 'NAME'}
            ],
            fields: [
              {values: {name: 'A'}}
            ]
          });

          notificationService.flush();

          var col = $scope.profileAppService.tabularViewService.fieldCols[0];
          expect(col.index).toBe(0);
          expect(col.isColumn).toBe(true);
          expect(col.nameKey).toBe('NAME');
          expect(col.pathToProperty).toEqual(['name']);
          expect(col.stringifiedPath).toBe(JSON.stringify(['name']));
        });

        it("should contain columns for used multi-level properties", function () {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, 'ABCD', {
            id: "ABCD",
            dataSourceReference: {id: 'abc', dataSourceProvider: 'cde'},
            profileFieldProperties: [
              {pathToProperty: ['bag', 'needle']}
            ],
            fields: [
              {values: {bag: {needle: 'sharp'}}}
            ]
          });

          notificationService.flush();

          var col = $scope.profileAppService.tabularViewService.fieldCols[0];
          expect(col.pathToProperty).toEqual(['bag', 'needle']);
          expect(col.stringifiedPath).toBe(JSON.stringify(['bag', 'needle']));
        });

        it("should not contain columns for properties in 'fields' but not in 'profileFieldProperties'", function () {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, 'ABCD', {
            id: "ABCD",
            dataSourceReference: {id: 'abc', dataSourceProvider: 'cde'},
            profileFieldProperties: [
              {pathToProperty: ['name']}
            ],
            fields: [
              {values: {name: 'A', age: 12}}
            ]
          });

          notificationService.flush();
          expect($scope.profileAppService.tabularViewService.fieldCols.length).toBe(1);
          var col = $scope.profileAppService.tabularViewService.fieldCols[0];
          expect(col.pathToProperty).toEqual(['name']);
        });

        it("should contain columns in the order of 'profileFieldProperties'", function () {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, 'ABCD', {
            id: "ABCD",
            dataSourceReference: {id: 'abc', dataSourceProvider: 'cde'},
            profileFieldProperties: [
              // Age is before Name, on purpose.
              {pathToProperty: ['age']},
              {pathToProperty: ['name']}
            ],
            fields: [
              // Name is before Age, on purpose.
              {values: {name: 'A'}},
              {values: {age: 12}}
            ]
          });

          notificationService.flush();
          var col0 = $scope.profileAppService.tabularViewService.fieldCols[0];
          var col1 = $scope.profileAppService.tabularViewService.fieldCols[1];
          expect(col0.index).toBe(0);
          expect(col0.pathToProperty).toEqual(['age']);

          expect(col1.index).toBe(1);
          expect(col1.pathToProperty).toEqual(['name']);
        });
      });

      describe("the profile field rows, determined based on the notification's 'profileFieldProperties' and 'fields' - ", function () {

        it("should be an array", function () {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
            id: "ABCD",
            dataSourceReference: {id: 'abc', dataSourceProvider: 'cde'},
            profileFieldProperties: [
              {pathToProperty: ['name']},
              {pathToProperty: ['bag', 'needle']}
            ],
            fields: [
              {values: {name: 'A'}},
              {values: {name: 'B'}}
            ]
          });

          notificationService.flush();

          expect($scope.profileAppService.tabularViewService.fieldRows instanceof Array).toBe(true);
        });

        it("should be in the number of one row per field, when all fields' properties have a single value", function () {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
            id: "ABCD",
            dataSourceReference: {id: 'abc', dataSourceProvider: 'cde'},
            profileFieldProperties: [
              {pathToProperty: ['name']},
              {pathToProperty: ['age']},
              {pathToProperty: ['type', 'code']},
            ],
            fields: [
              {values: {name: 'A', age: 12, type: {code: '1'}}},
              {values: {name: 'B', age: 11, type: {code: '2'}}}
            ]
          });

          notificationService.flush();

          expect($scope.profileAppService.tabularViewService.fieldRows.length).toBe(2);
        });

        it("should have properties for every column, when the corresponding field has the property", function () {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
            id: "ABCD",
            dataSourceReference: {id: 'abc', dataSourceProvider: 'cde'},
            profileFieldProperties: [
              {pathToProperty: ['name']},
              {pathToProperty: ['age']},
              {pathToProperty: ['type', 'code']},
            ],
            fields: [
              {values: {name: 'A', age: 12, type: {code: '1'}}},
              {values: {name: 'B', age: 11, type: {code: '2'}}}
            ]
          });

          notificationService.flush();

          var propNameId = JSON.stringify(['name']);
          var propAgeId = JSON.stringify(['age']);
          var propTypeCodeId = JSON.stringify(['type', 'code']);

          var row0 = $scope.profileAppService.tabularViewService.fieldRows[0];
          var row1 = $scope.profileAppService.tabularViewService.fieldRows[1];

          expect(row0[propNameId]).toBe('A');
          expect(row0[propAgeId]).toBe(12);
          expect(row0[propTypeCodeId]).toBe('1');

          expect(row1[propNameId]).toBe('B');
          expect(row1[propAgeId]).toBe(11);
          expect(row1[propTypeCodeId]).toBe('2');
        });

        it("should not have a property for a columns, when the corresponding field does not have it", function () {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
            id: "ABCD",
            dataSourceReference: {id: 'abc', dataSourceProvider: 'cde'},
            profileFieldProperties: [
              {pathToProperty: ['name']},
              {pathToProperty: ['age']},
              {pathToProperty: ['type', 'code']},
            ],
            fields: [
              {values: {name: 'A', age: 12}},
              {values: {name: 'B', type: {code: '2'}}}
            ]
          });

          notificationService.flush();

          var propAgeId = JSON.stringify(['age']);
          var propTypeCodeId = JSON.stringify(['type', 'code']);

          var row0 = $scope.profileAppService.tabularViewService.fieldRows[0];
          var row1 = $scope.profileAppService.tabularViewService.fieldRows[1];

          expect(row0.hasOwnProperty(propTypeCodeId)).toBe(false);
          expect(row1.hasOwnProperty(propAgeId)).toBe(false);
        });

        it("should be in the number of two rows for a field having two values in just one of the columns", function () {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
            id: "ABCD",
            dataSourceReference: {id: 'abc', dataSourceProvider: 'cde'},
            profileFieldProperties: [
              {pathToProperty: ['name']},
              {pathToProperty: ['age']}
            ],
            fields: [
              {values: {name: 'A', age: 12}},
              {values: {name: 'B', age: [11]}},
              {values: {name: 'C', age: [13, 15]}}
            ]
          });

          notificationService.flush();

          var propAgeId = JSON.stringify(['age']);
          var propNameId = JSON.stringify(['name']);

          var rows = $scope.profileAppService.tabularViewService.fieldRows;
          expect(rows.length).toBe(1 + 1 + 2);

          expect(rows[0][propNameId]).toBe('A');
          expect(rows[0][propAgeId]).toBe(12);

          expect(rows[1][propNameId]).toBe('B');
          expect(rows[1][propAgeId]).toBe(11);

          expect(rows[2][propNameId]).toBe('C');
          expect(rows[2][propAgeId]).toBe(13);

          expect(rows[3][propNameId]).toBe('C');
          expect(rows[3][propAgeId]).toBe(15);
        });

        it("should be in the number of six rows for a field having two values in a column and three values in another column", function () {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
            id: "ABCD",
            dataSourceReference: {id: 'abc', dataSourceProvider: 'cde'},
            profileFieldProperties: [
              {pathToProperty: ['name']},
              {pathToProperty: ['age']},
              {pathToProperty: ['experience']}
            ],
            fields: [
              {values: {name: 'A', age: 12, experience: 5}},
              {values: {name: 'B', age: [11], experience: 2}},
              {values: {name: 'C', age: [13, 15], experience: [3, 6, 10]}}
            ]
          });

          notificationService.flush();

          var propAgeId = JSON.stringify(['age']);
          var propNameId = JSON.stringify(['name']);
          var propExpId = JSON.stringify(['experience']);

          var rows = $scope.profileAppService.tabularViewService.fieldRows;
          expect(rows.length).toBe(1 + 1 + 2 * 3);

          expect(rows[0][propNameId]).toBe('A');
          expect(rows[0][propAgeId]).toBe(12);
          expect(rows[0][propExpId]).toBe(5);

          expect(rows[1][propNameId]).toBe('B');
          expect(rows[1][propAgeId]).toBe(11);
          expect(rows[1][propExpId]).toBe(2);

          // Breadth first order of props/values.

          // name: C, exp: 3
          expect(rows[2][propNameId]).toBe('C');
          expect(rows[2][propAgeId]).toBe(13);
          expect(rows[2][propExpId]).toBe(3);

          expect(rows[3][propNameId]).toBe('C');
          expect(rows[3][propAgeId]).toBe(15);
          expect(rows[3][propExpId]).toBe(3);

          // name: C, exp: 6
          expect(rows[4][propNameId]).toBe('C');
          expect(rows[4][propAgeId]).toBe(13);
          expect(rows[4][propExpId]).toBe(6);

          expect(rows[5][propNameId]).toBe('C');
          expect(rows[5][propAgeId]).toBe(15);
          expect(rows[5][propExpId]).toBe(6);

          // name: C, exp: 10
          expect(rows[6][propNameId]).toBe('C');
          expect(rows[6][propAgeId]).toBe(13);
          expect(rows[6][propExpId]).toBe(10);

          expect(rows[7][propNameId]).toBe('C');
          expect(rows[7][propAgeId]).toBe(15);
          expect(rows[7][propExpId]).toBe(10);

        });
      });
    });

    describe("when the response of the data source getInclude service arrives", function () {

      describe("and the data source does not have an associated AMD module", function () {

        it("should set the scope property 'dataSourceUrl'", function () {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
            id: "ABCD",
            dataSourceReference: {
              id: 'abc',
              dataSourceProvider: 'cde'
            },
            profileFieldProperties: [
              {pathToProperty: ['name']},
              {pathToProperty: ['bag', 'needle']}
            ],
            fields: [
              {values: {name: 'A'}},
              {values: {name: 'B'}}
            ]
          });

          var operations = [{id: 'a', nameKey: 'b'}];
          $httpBackend.whenGET(PROFILE_GETOPERS_URL + 'ABCD').respond(operations);
          $httpBackend.whenGET(DATASOURCE_GETINCLUDE_URL + 'abc/cde').respond({
            profileDataSourceInclude: {
              url: 'foo'
            }
          });

          //spyOn($scope.profileAppService.profileManagementViewService, 'buildProfileManagementViewServiceTreeViewSchemas').andCallFake($scope.profileAppService.profileManagementViewService.buildProfileManagementViewServiceTreeViewSchemas([], []));

          $httpBackend.expectGET(PROFILE_GETPROF_URL).respond([]);
          $httpBackend.expectGET(PROFILE_GETAGGPROF_URL).respond([]);

          notificationService.flush();
          $httpBackend.flush();

          expect($scope.profileAppService.dataSourceUrl).toBe("foo");
        });

      });

      describe("and the data source has an associated AMD module", function () {

        it("should set the scope property 'dataSourceUrl'", function () {
          runs(function () {
            notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
              id: "ABCD",
              dataSourceReference: {
                id: 'abc',
                dataSourceProvider: 'cde'
              },
              profileFieldProperties: [
                {pathToProperty: ['name']},
                {pathToProperty: ['bag', 'needle']}
              ],
              fields: [
                {values: {name: 'A'}},
                {values: {name: 'B'}}
              ]
            });

            // Make sure to load a fresh module.
            require.undef('testDataSourceGetIncludeModule');

            var operations = [
              {id: 'a', nameKey: 'b'}
            ];
            $httpBackend.whenGET(PROFILE_GETOPERS_URL + 'ABCD').respond(operations);
            $httpBackend.whenGET(DATASOURCE_GETINCLUDE_URL + 'abc/cde').respond({
              profileDataSourceInclude: {
                require: 'testDataSourceGetIncludeModule',
                url: 'foo'
              }
            });
            $httpBackend.expectGET(PROFILE_GETPROF_URL).respond([]);
            $httpBackend.expectGET(PROFILE_GETAGGPROF_URL).respond([]);

            notificationService.flush();
            $httpBackend.flush();
          });

          waitsFor(function () {
            return !!$scope.profileAppService.dataSourceUrl;
          }, "The data source url should have changed", 5000);

          runs(function () {
            expect($scope.profileAppService.dataSourceUrl).toBe("foo");
          });
        });

      });
    });

  });
});