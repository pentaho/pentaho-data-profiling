'use-strict';
// UTILITIES
var Pentaho = Pentaho || {};

Pentaho.utilities = {
  toArray: function(t) {
    return (t == null || Array.isArray(t)) ? t : [t];
  },
  append: function(a1, a2) {
    for(var i = 0, L = a2.length; i < L; i++) a1.push(a2[i]);
    return a1;
  },
  compare: function(a, b) {
    return a > b ? 1 : a < b ? -1 : 0;
  }
};

define([
  'common-ui/angular',
  'common-ui/angular-route',
  'common-ui/angular-mocks',
  'controllers/controllers',
  'controllers/profileAppController',
  'services/services',
  'services/profileService',
  'services/dataSourceService',
  'services/profileAppService',
  'services/tabularService'
], function(angular) {

  var PROFILE_STATUS_NOTIF_STYPE = "com.pentaho.profiling.model.ProfilingServiceImpl";
  var PROFILE_GETOPERS_URL = "/cxf/profile/operations/";
  var DATASOURCE_GETINCLUDE_URL = "/cxf/data-profiling-service/dataSource/include/";

  describe("Profiling Service profileAppController -", function() {

    angular.module('profileApp', [
      'ngRoute',
      'appServices',
      'appControllers',
      'pascalprecht.translate'
    ]);

    var $scope, $rootScope, $httpBackend, $timeout, $routeParams, notificationService;

    beforeEach(module("profileApp"));

    beforeEach(inject(function(_$rootScope_, $controller, _$httpBackend_, _$timeout_, _$routeParams_, NotificationService) {
      $rootScope = _$rootScope_;
      $scope = _$rootScope_.$new();
      $httpBackend = _$httpBackend_;
      $timeout = _$timeout_;
      $routeParams = _$routeParams_;
      notificationService = NotificationService;

      $routeParams.profileId = 'ABCD';

      $controller("profileAppController", {$scope: $scope, $routeParams: $routeParams});
    }));

    describe("scope object initialization -", function() {

      it("should have a property 'isOrderReversed' with value false", function() {
        expect($scope.profileAppService.tabularService.isOrderReversed).toBe(false);
      });

      it("should have a property 'orderByCol' with value [\"aFieldName\"]", function() {
        expect(/^\["[^"]+"\]$/.test($scope.profileAppService.tabularService.orderByCol)).toBe(true);
      });

      it("should not have a property 'profileId'", function() {
        expect($scope.profileAppService.profileId).toBeUndefined();
      });

      it("should register for a ProfileStatus notification for id 'ABCD'", function() {
        notificationService.verifyRegistrationExists(PROFILE_STATUS_NOTIF_STYPE, "ABCD");
      });
    });

    describe("when a ProfileStatus notification arrives -", function() {

      it("should set the scope property 'profileId' to 'ABCD'", function() {
        notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
          id: "ABCD",
          dataSourceReference: {
            id: 'abc',
            dataSourceProvider: 'cde'
          }
        });

        notificationService.flush();

        expect($scope.profileAppService.profileId).toBe('ABCD');

        notificationService.verifyNoUnfulfilledRegistrations();
      });

      it("should set the scope property 'currentOperation'", function() {
        var currentOper = {
          messageKey: 'A',
          messagePath: '/A',
          messageVariables: []
        };

        notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
          id: "ABCD",
          dataSourceReference: {
            id: 'abc',
            dataSourceProvider: 'cde'
          },
          currentOperationMessage: currentOper
        });

        notificationService.flush();

        expect($scope.profileAppService.currentOperationMessage).toBe(currentOper);
      });

      it("should set the scope property 'operationError'", function() {
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
          operationError: operationError
        });

        notificationService.flush();

        expect($scope.profileAppService.operationError).toBe(operationError);
      });

      it("should issue GET on the profile getOperations service", function() {
        notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
          id: "ABCD",
          dataSourceReference: {
            id: 'abc',
            dataSourceProvider: 'cde'
          }
        });

        $httpBackend.expectGET(PROFILE_GETOPERS_URL + 'ABCD').respond("");
        $httpBackend.whenGET(DATASOURCE_GETINCLUDE_URL + 'abc/cde').respond("");

        notificationService.flush();
        $httpBackend.verifyNoOutstandingExpectation();
      });

      it("should issue GET on the data source getInclude service", function() {
        notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
          id: "ABCD",
          dataSourceReference: {
            id: 'abc',
            dataSourceProvider: 'cde'
          }
        });

        $httpBackend.whenGET(PROFILE_GETOPERS_URL + 'ABCD').respond("");
        $httpBackend.expectGET(DATASOURCE_GETINCLUDE_URL + 'abc/cde').respond("");

        notificationService.flush();
        $httpBackend.verifyNoOutstandingExpectation();
      });

      describe("the profile field columns, determined based on the notification's 'profileFieldProperties' and 'fields' - ", function() {

        it("should be an array", function() {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
            id: "ABCD",
            dataSourceReference: {id: 'abc', dataSourceProvider: 'cde'},
            profileFieldProperties: [],
            fields: []
          });

          notificationService.flush();

          expect($scope.profileAppService.tabularService.fieldCols instanceof Array).toBe(true);
        });

        it("should only contain columns for which at least on field has a value for", function() {
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

          expect($scope.profileAppService.tabularService.fieldCols.length).toBe(1);
          var col = $scope.profileAppService.tabularService.fieldCols[0];
          expect(col.stringifiedPath).toBe(JSON.stringify(['name']));
        });

        it("should contain columns with all expected attributes", function() {
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

          var col = $scope.profileAppService.tabularService.fieldCols[0];
          expect(col.index).toBe(0);
          expect(col.isColumn).toBe(true);
          expect(col.nameKey).toBe('NAME');
          expect(col.pathToProperty).toEqual(['name']);
          expect(col.stringifiedPath).toBe(JSON.stringify(['name']));
        });

        it("should contain columns for used multi-level properties", function() {
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

          var col = $scope.profileAppService.tabularService.fieldCols[0];
          expect(col.pathToProperty).toEqual(['bag', 'needle']);
          expect(col.stringifiedPath).toBe(JSON.stringify(['bag', 'needle']));
        });

        it("should not contain columns for properties in 'fields' but not in 'profileFieldProperties'", function() {
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
          expect($scope.profileAppService.tabularService.fieldCols.length).toBe(1);
          var col = $scope.profileAppService.tabularService.fieldCols[0];
          expect(col.pathToProperty).toEqual(['name']);
        });

        it("should contain columns in the order of 'profileFieldProperties'", function() {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, 'ABCD', {
            id: "ABCD",
            dataSourceReference: {id: 'abc', dataSourceProvider: 'cde'},
            profileFieldProperties: [
              // Age is before Name, on purpose.
              {pathToProperty: ['age' ]},
              {pathToProperty: ['name']}
            ],
            fields: [
              // Name is before Age, on purpose.
              {values: {name: 'A'}},
              {values: {age:  12 }}
            ]
          });

          notificationService.flush();
          var col0 = $scope.profileAppService.tabularService.fieldCols[0];
          var col1 = $scope.profileAppService.tabularService.fieldCols[1];
          expect(col0.index).toBe(0);
          expect(col0.pathToProperty).toEqual(['age']);

          expect(col1.index).toBe(1);
          expect(col1.pathToProperty).toEqual(['name']);
        });
      });

      describe("the profile field rows, determined based on the notification's 'profileFieldProperties' and 'fields' - ", function() {

        it("should be an array", function() {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
            id: "ABCD",
            dataSourceReference: {id: 'abc', dataSourceProvider: 'cde'},
            profileFieldProperties: [],
            fields: []
          });

          notificationService.flush();

          expect($scope.profileAppService.tabularService.fieldRows instanceof Array).toBe(true);
        });

        it("should be in the number of one row per field, when all fields' properties have a single value", function() {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
            id: "ABCD",
            dataSourceReference: {id: 'abc', dataSourceProvider: 'cde'},
            profileFieldProperties: [
              {pathToProperty: ['name']},
              {pathToProperty: ['age' ]},
              {pathToProperty: ['type', 'code']},
            ],
            fields: [
              {values: {name: 'A', age: 12, type: {code: '1'}}},
              {values: {name: 'B', age: 11, type: {code: '2'}}}
            ]
          });

          notificationService.flush();

          expect($scope.profileAppService.tabularService.fieldRows.length).toBe(2);
        });

        it("should have properties for every column, when the corresponding field has the property", function() {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
            id: "ABCD",
            dataSourceReference: {id: 'abc', dataSourceProvider: 'cde'},
            profileFieldProperties: [
              {pathToProperty: ['name']},
              {pathToProperty: ['age' ]},
              {pathToProperty: ['type', 'code']},
            ],
            fields: [
              {values: {name: 'A', age: 12, type: {code: '1'}}},
              {values: {name: 'B', age: 11, type: {code: '2'}}}
            ]
          });

          notificationService.flush();

          var propNameId = JSON.stringify(['name']);
          var propAgeId  = JSON.stringify(['age']);
          var propTypeCodeId = JSON.stringify(['type', 'code']);

          var row0 = $scope.profileAppService.tabularService.fieldRows[0];
          var row1 = $scope.profileAppService.tabularService.fieldRows[1];

          expect(row0[propNameId]).toBe('A');
          expect(row0[propAgeId]).toBe(12);
          expect(row0[propTypeCodeId]).toBe('1');

          expect(row1[propNameId]).toBe('B');
          expect(row1[propAgeId]).toBe(11);
          expect(row1[propTypeCodeId]).toBe('2');
        });

        it("should not have a property for a columns, when the corresponding field does not have it", function() {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
            id: "ABCD",
            dataSourceReference: {id: 'abc', dataSourceProvider: 'cde'},
            profileFieldProperties: [
              {pathToProperty: ['name']},
              {pathToProperty: ['age' ]},
              {pathToProperty: ['type', 'code']},
            ],
            fields: [
              {values: {name: 'A', age: 12}},
              {values: {name: 'B', type: {code: '2'}}}
            ]
          });

          notificationService.flush();

          var propAgeId  = JSON.stringify(['age']);
          var propTypeCodeId = JSON.stringify(['type', 'code']);

          var row0 = $scope.profileAppService.tabularService.fieldRows[0];
          var row1 = $scope.profileAppService.tabularService.fieldRows[1];

          expect(row0.hasOwnProperty(propTypeCodeId)).toBe(false);
          expect(row1.hasOwnProperty(propAgeId)).toBe(false);
        });

        it("should be in the number of two rows for a field having two values in just one of the columns", function() {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
            id: "ABCD",
            dataSourceReference: {id: 'abc', dataSourceProvider: 'cde'},
            profileFieldProperties: [
              {pathToProperty: ['name']},
              {pathToProperty: ['age' ]}
            ],
            fields: [
              {values: {name: 'A', age: 12}},
              {values: {name: 'B', age: [11]}},
              {values: {name: 'C', age: [13, 15]}}
            ]
          });

          notificationService.flush();

          var propAgeId  = JSON.stringify(['age']);
          var propNameId = JSON.stringify(['name']);

          var rows = $scope.profileAppService.tabularService.fieldRows;
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

        it("should be in the number of six rows for a field having two values in a column and three values in another column", function() {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
            id: "ABCD",
            dataSourceReference: {id: 'abc', dataSourceProvider: 'cde'},
            profileFieldProperties: [
              {pathToProperty: ['name']},
              {pathToProperty: ['age' ]},
              {pathToProperty: ['experience' ]}
            ],
            fields: [
              {values: {name: 'A', age: 12, experience: 5}},
              {values: {name: 'B', age: [11], experience: 2}},
              {values: {name: 'C', age: [13, 15], experience: [3, 6, 10]}}
            ]
          });

          notificationService.flush();

          var propAgeId  = JSON.stringify(['age']);
          var propNameId = JSON.stringify(['name']);
          var propExpId  = JSON.stringify(['experience']);

          var rows = $scope.profileAppService.tabularService.fieldRows;
          expect(rows.length).toBe(1 + 1 + 2*3);

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

    describe("when the response of the profile getOperations service arrives", function() {
      it("should set the scope property 'operations'", function() {
        notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
          id: "ABCD",
          dataSourceReference: {
            id: 'abc',
            dataSourceProvider: 'cde'
          }
        });

        var operations = [{id: 'a', nameKey: 'b'}];
        $httpBackend.expectGET(PROFILE_GETOPERS_URL + 'ABCD').respond(operations);
        $httpBackend.whenGET(DATASOURCE_GETINCLUDE_URL + 'abc/cde').respond({
          profileDataSourceInclude: {url: 'foo'}
        });

        notificationService.flush();
        $httpBackend.flush();

        // angular.equals takes care of ignoring special angular $properties.
        expect(angular.equals($scope.profileAppService.operations, operations)).toBe(true);
      });
    });

    describe("when the response of the data source getInclude service arrives", function() {

      describe("and the data source does not have an associated AMD module", function() {

        it("should set the scope property 'dataSourceUrl'", function() {
          notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
            id: "ABCD",
            dataSourceReference: {
              id: 'abc',
              dataSourceProvider: 'cde'
            }
          });

          var operations = [{id: 'a', nameKey: 'b'}];
          $httpBackend.whenGET(PROFILE_GETOPERS_URL + 'ABCD').respond(operations);
          $httpBackend.whenGET(DATASOURCE_GETINCLUDE_URL + 'abc/cde').respond({
            profileDataSourceInclude: {
              url: 'foo'
            }
          });

          notificationService.flush();
          $httpBackend.flush();

          expect($scope.profileAppService.dataSourceUrl).toBe("foo");
        });

      });

      describe("and the data source has an associated AMD module", function() {

        it("should set the scope property 'dataSourceUrl'", function() {
          runs(function() {
            notificationService.notify(PROFILE_STATUS_NOTIF_STYPE, "ABCD", {
              id: "ABCD",
              dataSourceReference: {
                id: 'abc',
                dataSourceProvider: 'cde'
              }
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

            notificationService.flush();
            $httpBackend.flush();
          });

          waitsFor(function() {
            return !!$scope.profileAppService.dataSourceUrl;
          }, "The data source url should have changed", 5000);

          runs(function() {
            expect($scope.profileAppService.dataSourceUrl).toBe("foo");
          });
        });

      });
    });

  });
});