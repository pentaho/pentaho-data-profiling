define(['notifier', 'angular-mocks'], function(notifier) {
  var httpBackend;
  beforeEach(module('NotificationServiceModule'));
  beforeEach(inject(function($httpBackend){
    httpBackend = $httpBackend;
  }));
  //var injector = new Squire();
  //var builder = injector.mock('angular', {}).mock('angular-resource', {});

  describe("Notifier ", function() {
    var notificationService;
    beforeEach(inject(function(NotificationService, $httpBackend){
      notificationService = NotificationService;
      httpBackend = $httpBackend;
    }));

    afterEach(function() {
      httpBackend.verifyNoOutstandingExpectation();
      httpBackend.verifyNoOutstandingRequest();
    });

    it('should call callback for one registered listener', function() {
      var type = 'TEST_TYPE';
      var id = 'TEST_ID';
      httpBackend.expectPOST('/cxf/notificationService', {
        'notificationRequestWrapper' : {
          'requests' : [{'notificationType': type, 'entries': [{'key': id, 'value': 0}]}]
        }
      }).respond({"notificationResponse":[{"changedItems":{"id":id,"timestamp":1408992291626},"notificationType":type}]});
      var callbackCalled =false;
      var registerId = notificationService.register(type, [id], function(id) {
        callbackCalled = true;
        notificationService.unregister(registerId);
      });
      httpBackend.flush();
      expect(callbackCalled).toBe(true);
    });

    it('should call callback for two registered listener', function() {
      var type = 'TEST_TYPE';
      var id = 'TEST_ID';
      var id2 = 'TEST_ID_2';
      httpBackend.expectPOST('/cxf/notificationService', {
        'notificationRequestWrapper' : {
          'requests' : [{'notificationType': type, 'entries': [{'key': id, 'value': 0}]}]
        }
      }).respond({"notificationResponse":[{"changedItems":[],"notificationType":type}]});
      httpBackend.expectPOST('/cxf/notificationService', {
        'notificationRequestWrapper' : {
          'requests' : [{'notificationType': type, 'entries': [{'key': id, 'value': 0}, {'key': id2, 'value': 0}]}]
        }
      }).respond({"notificationResponse":[{"changedItems":[{"id":id,"timestamp":1408992291626}, {"id":id2,"timestamp":1408992291626}],"notificationType":type}]});
      var callbackCalled =false;
      var registerId = notificationService.register(type, [id], function(id) {
        callbackCalled = true;
        notificationService.unregister(registerId);
      });
      var callbackCalled2 =false;
      var registerId2 = notificationService.register(type, [id2], function(id) {
        callbackCalled2 = true;
        notificationService.unregister(registerId2);
      });
      httpBackend.flush();
      expect(callbackCalled).toBe(true);
      expect(callbackCalled2).toBe(true);
    });

    it("shouldn't call callback with unrelated change", function() {
      var type = 'TEST_TYPE';
      var id = 'TEST_ID';
      var registerId;
      httpBackend.expectPOST('/cxf/notificationService', {
        'notificationRequestWrapper' : {
          'requests' : [{'notificationType': type, 'entries': [{'key': id, 'value': 0}]}]
        }
      }).respond(function() {
        notificationService.unregister(registerId);
        return {"notificationResponse":[{"changedItems":{"id":"DIFFERENT_ID","timestamp":1408992291626},"notificationType":type}]};
      });
      registerId = notificationService.register(type, [id], function(id) {
        // Fail as we shouldn't get this callback
        expect(false).toBe(true);
      });
      httpBackend.flush();
    });

    it("should only call callback with lower timestamp than response", function() {
      var type = 'TEST_TYPE';
      var id = 'TEST_ID';
      httpBackend.expectPOST('/cxf/notificationService', {
        'notificationRequestWrapper' : {
          'requests' : [{'notificationType': type, 'entries': [{'key': id, 'value': 0}]}]
        }
      }).respond({"notificationResponse":[{"changedItems":{"id":id,"timestamp":5},"notificationType":type}]});
      httpBackend.expectPOST('/cxf/notificationService', {
        'notificationRequestWrapper' : {
          'requests' : [{'notificationType': type, 'entries': [{'key': id, 'value': 0}]}]
        }
      }).respond({"notificationResponse":[{"changedItems":{"id":id,"timestamp":5},"notificationType":type}]});
      var callbackCalled = 0;
      var callbackCalled2 = 0;
      var registerId = notificationService.register(type, [id], function(id) {
        callbackCalled++;
        var registerId2 = notificationService.register(type, [id], function(id) {
          callbackCalled2++;
          notificationService.unregister(registerId);
          notificationService.unregister(registerId2);
        });
      });
      httpBackend.flush();
      expect(callbackCalled).toBe(1);
      expect(callbackCalled2).toBe(1);
    });
  });
});