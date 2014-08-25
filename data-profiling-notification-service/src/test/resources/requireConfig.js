require.config({
 "paths": {
   "angular": "/webjars/angular",
   "angular-route": "/webjars/angular-route",
   "angular-resource": "/webjars/angular-resource",
   "angular-mocks": "/webjars/angular-mocks"
 },
 "shim" : {
   "angular" : { "exports": "angular" },
   "angular-route" : { "deps": ["angular"] },
   "angular-resource" : { "deps": ["angular"] },
   "angular-mocks" : { "deps": ["angular"] }
 }
});