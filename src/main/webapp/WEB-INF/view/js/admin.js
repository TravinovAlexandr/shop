//ANGULAR SERVICES $rootScope, $location, $http, $window , $dirty

//$q - deffered object  
//$timeout

//$routeProvider, $locationProvider
let authApp = angular.module('authPage', []);
let adminApp = angular.module('adminPage', ['ngRoute'])
        .config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
                $routeProvider.when('/b' ,{
                    template : '<h1>HELLO ANGUU</h1>',
                    controller: 'bController'
                });
//                $routeProvider.when('/admin' , {
//                    templateUrl : 'html/admin.html',
//                    controller: 'defferController',
//                    resolve: {
//                        main: function( $q, $timeout, $log) {
//                            $log.log($q);
//                            let defer = $q.defer();
//                            $timeout(function() {
//                                defer.resolve();
//                            }, 2000);
//                        return defer.promise;
//                        }
//                    }
//                });
//                $routeProvider.otherwise({redirectTo: '/auth'});
                
                $locationProvider.html5Mode({enabled: true, requireBase: false});
}]);

                adminApp.controller('bController', function($window) {
                    $window.log(1);
                });
                
//                adminApp.controller('defferController', function($q) {
//                    let defer = $q.defer();
//                    defer.promise.then(function(data) {
//                        let resp = data;
//                        alert(data);
//                        return resp;
//                    });
//                    defer.resolve("Alex");
//                });



adminApp.controller('productController', ['$scope', '$http', function($scope, $http){
        
}]);


adminApp.controller('adminController', ['$scope', '$http', function($scope, $http) {
        
        let allAdmins = {};
        
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
        
        $scope.deleteAdmin = function() {
           
           
        };
        
        $scope.getAllAdmins = function() {
            
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