//ANGULAR SERVICES $rootScope, $location, $http, $window $routeProvider, $locationProvider, $dirty

let authApp = angular.module('authPage', []);
let adminApp = angular.module('adminPage', []);

adminApp.controller('insertProductController', ['$scope', '$http', function($scope, $http){
        
}]);

adminApp.controller('companyController', ['$scope', '$http', function($scope, $http) {
    
        $scope.insertCompany = function() {
            
            let form = $scope.companyForm;
            
            alert(angular.toJson(form));
            $http({
                method: 'POST',
                url: '/insertCompany',
                data: form
            }).then(function(rsp) {
                alert(angular.toJson(rsp.data));
            }, function(rsp) {
                alert(angular.toJson(rsp.data));
            });
        };
}]);

adminApp.controller('insertAdminController', ['$scope', '$http', function($scope, $http) {
        
        $scope.insertAdmin = function() {
           
            let adminForm = {
               nick : $scope.nick,
               password: $scope.password,
               role: $scope.role
           };
           
           $http({
                   method: 'POST',
                   url: '/insertAdmin',
                   data: adminForm
            }).then(function(rsp) {
                alert(angular.toJson(rsp.data));
            }, function(rsp) {
                alert(angular.fromJson(rsp.data));
            });
        };
}]);

authApp.controller('authenticationController', ['$scope', '$http', function($scope, $http){
        
        $scope.userAuthentication = function() {
            let nick = $scope.nick;
            let password = $scope.password;

            $http({
                method: 'POST',
                url: '/auth',
                data: 'nick=' + nick + '&password=' + password,
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }, function(rsp) {
                alert(angular.toJson(rsp.data));
            }, function(rsp) {
                alert(angular.toJson(rsp.data));
            });
        };
}]);