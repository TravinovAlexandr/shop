var adminApp = angular.module('adminPage', ['ngRoute']);
var authApp = angular.module('authPage', []);

adminApp.config(function($routeProvider, $locationProvider) {
    $routeProvider.when('/addNewProduct', {
        templateUrl : '/html/add_product.html',
        controller: 'addProductController'
    });
    
    $routeProvider.when('/searchProduct', {
        templateUrl : '/html/search_table.html',
        controller: 'searchTableController'
    });
    
    $routeProvider.otherwise({redirectTo: '/admin'});

    
    $locationProvider.html5Mode({enabled: true, requireBase: false});
});

adminApp.controller('addProductController', function($scope, $http) {
    
    $http({method : 'POST', url : '/getAllCategories'}).then(function(resp) {
        var objResp = angular.fromJson(resp.data);
        $scope.categories = objResp.response;
    }, function(resp) {
        alert('Categories are not found');
    });
    
    $scope.addNewProduct = function() { 
        var addProdMultyForm = new FormData();
        addProdMultyForm.append('name', $scope.addName);
        addProdMultyForm.append('description', $scope.addDesc);
        addProdMultyForm.append('price', $scope.addPrice);
        addProdMultyForm.append('quantity', $scope.addQuant);
        addProdMultyForm.append('image', $scope.addImage);

        var categoryIds = [];
        
        $('input:checkbox:checked').each(function() {
            categoryIds.push($(this).val());
        });
        
        addProdMultyForm.append('categoryIds', categoryIds);
        
        $http({method : 'POST', url : '/addProduct', data : addProdMultyForm, headers : {'Content-Type': undefined}}).then(function(){
        }, 
        function(resp) {
            if (resp.data !== undefined) {
                alert(resp.data.response);
            }
        }); 
    };
});

adminApp.controller('searchTableController', function($scope, $http, $q, $timeout) {       
    $http({method : 'POST', url : '/getSearchTable'}).then(
        function(resp) {
                $scope.searchTab = angular.fromJson(resp.data.response);
                $timeout(function(){
                    $('.searchSelect').on('change', defSearchChangeEvent);        
                }, 2000);
        }, function(resp) {
            alert(resp.data.response);
    });
    
    function defSearchChangeEvent(e)  {
        var searhInputsDiv = $($(e.target).parent().parent());
        searhInputsDiv.find('.searchOptionHidden').val(e.target.value);
        var doubleSearchInput = searhInputsDiv.find('.doubleSearchInput');
        if (e.target.value === 'beetwen' || e.target.value === '>= and <') {
                doubleSearchInput.show();
        } else {
            doubleSearchInput.hide();
        }
    }
   
    $scope.submitSerchForm = function() {
        var searchReqList = [];    
   
        function SearchElement() {
            this.name; this.operator; this.data;
            this.push = function(el) {
                if (!this.data) { this.data = []; }
                this.data.push(el); };
        }
        
        var searchInputs = $('.searchInputWrapper');
        
        for (var i = 0; i < searchInputs.length; i++) {
            var serchElement = new SearchElement();
            var searchInputEl = $(searchInputs[i]);
            serchElement.name = searchInputEl.find('.searchNameHidden').val();
            serchElement.operator = searchInputEl.find('.searchOptionHidden').val();
            serchElement.push(searchInputEl.find('.mainSearchInput').val());
            var doubleInput = searchInputEl.find('.doubleSearchInput');
            if (doubleInput.is(':visible') && doubleInput.val() !== null && doubleInput.val() !== undefined) {
                serchElement.push(doubleInput.val());
            }
            searchReqList.push(serchElement);
        }
    };
});

adminApp.controller('adminNavController', function($scope) {
    var navEls = $('.hidden');
    
    $scope.nickVisible = function() {
        var cadn = $('.changeAdminDataNick');
        if (cadn.is(':visible')) {
            cadn.hide();
        } else {
            cadn.show();
        }
    };
    
    $scope.passwordVisible = function() {
        var cadn = $('.changeAdminDataPassword');
        if (cadn.is(':visible')) {
            cadn.hide();
        } else {
            cadn.show();
        } 
    };
    
    $scope.productUlVisible = function() {
        var navEl0 = $(navEls[0]);
        if (navEl0.is(':visible')) {
            navEl0.hide();
        } else {
            navEl0.show();
        }
    };
    
    $scope.categoryUlVisible = function() {
        var navEl1 = $(navEls[1]);
        if (navEl1.is(':visible')) {
            navEl1.hide();
        } else {
            navEl1.show();
        }
    };
    
});

adminApp.directive('ngFileUp', function() {
    return {
        restrict : 'A',
        require : 'ngModel',
        link : function(scope, element, attrs, ngModel) {
            if (!ngModel) {
                console.log('Dirrective ng-file-up requires ng-model.');
                return;
            }
            element.bind('change', function(el) {
                var addElm = $(el.target).prop('files')[0];
                ngModel.$setViewValue(addElm);
                var addImgReader = new FileReader();
                addImgReader.onload = function(){
                        $('#prodBigImage').attr('src', addImgReader.result);
                };
                addImgReader.readAsDataURL(addElm);
            });  
        }
    };
});



//adminApp.controller('navController', function($scope){
//    $scope.navBar = "html/nav_bar.html";
//});

//////ANGULAR SERVICES $rootScope, $location, $http, $window , $dirty
//
////$q - deffered object  
////$timeout
//
////$routeProvider, $locationProvider
//let authApp = angular.module('authPage', []);
//let adminApp = angular.module('adminPage', ['ngRoute'])
//        .config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
//                $routeProvider.when('/b' ,{
//                    template : '<h1>HELLO ANGUU</h1>',
//                    controller: 'bController'
//                });
////                $routeProvider.when('/admin' , {
////                    templateUrl : 'html/admin.html',
////                    controller: 'defferController',
////                    resolve: {
////                        main: function( $q, $timeout, $log) {
////                            $log.log($q);
////                            let defer = $q.defer();
////                            $timeout(function() {
////                                defer.resolve();
////                            }, 2000);
////                        return defer.promise;
////                        }
////                    }
////                });
////                $routeProvider.otherwise({redirectTo: '/auth'});
//                
//                $locationProvider.html5Mode({enabled: true, requireBase: false});
//}]);
//
//                adminApp.controller('bController', function($window) {
//                    $window.log(1);
//                });
//                
////                adminApp.controller('defferController', function($q) {
////                    let defer = $q.defer();
////                    defer.promise.then(function(data) {
////                        let resp = data;
////                        alert(data);
////                        return resp;
////                    });
////                    defer.resolve("Alex");
////                });
//
//
//
//adminApp.controller('productController', ['$scope', '$http', function($scope, $http){
//        
//}]);
//
//
//adminApp.controller('adminController', ['$scope', '$http', function($scope, $http) {
//        
//        let allAdmins = {};
//        
//        $scope.insertAdmin = function() {           
//            let adminForm = {
//               nick : $scope.nick,
//               password: $scope.password,
//               role: $scope.role
//           };
//           $http({
//                   method: 'POST',
//                   url: '/insertAdmin',
//                   data: adminForm
//            }).then(function(rsp) {
//                alert(angular.toJson(rsp.data));
//            }, function(rsp) {
//                alert(angular.fromJson(rsp.data));
//            });
//        };
//        
//        $scope.deleteAdmin = function() {
//           
//           
//        };
//        
//        $scope.getAllAdmins = function() {
//            
//        };
//}]);
//
//
//
//authApp.controller('authenticationController', ['$scope', '$http', function($scope, $http){
//        
//        $scope.userAuthentication = function() {
//            let nick = $scope.nick;
//            let password = $scope.password;
//
//            $http({
//                method: 'POST',
//                url: '/auth',
//                data: 'nick=' + nick + '&password=' + password,
//                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
//            }, function(rsp) {
//                alert(angular.toJson(rsp.data));
//            }, function(rsp) {
//                alert(angular.toJson(rsp.data));
//            });
//        };
//}]);

