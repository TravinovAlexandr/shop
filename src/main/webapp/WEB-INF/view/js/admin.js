var adminApp = angular.module('adminPage', ['ngRoute']);
var authApp = angular.module('authPage', []);

adminApp.config(function($routeProvider, $locationProvider) {
    $routeProvider.when('/addNewProduct', {
        templateUrl : '/html/add_product.html',
        controller: 'addProductController'
        
    }).when('/searchProduct', {
        templateUrl : '/html/search_form.html',
        controller: 'searchTableController'
        
    }).when('/productTable', {
        templateUrl : '/html/product_table.html',
        controller: 'productTableController'
        
    }).when('/admin/product/:prId', {
        templateUrl : '/html/admin_product_update.html',
        controller: 'adminProductController'
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

adminApp.controller('searchTableController', function($scope, $http, $timeout, $location, $rootScope) {
    var exception = new Exception();
    
    $http({method : 'POST', url : '/searchTable'}).then(
        function(resp) {
            $scope.searchTab = angular.fromJson(resp.data.response);
//            $timeout(function(){
                $('.searchSelect').on('change', defSearchChangeEvent);        
//                }, 2000);
        }, function(resp) {
            alert(resp.data.response);
    });

    function defSearchChangeEvent(e)  {
        var searhInputsDiv = $($(e.target).parent().parent());
        searhInputsDiv.find('.searchOptionHidden').val(e.target.value);
        var doubleSearchInput = searhInputsDiv.find('.doubleSearchInput');
        if (e.target.value === 'Between' || e.target.value === '>= And <') {
                doubleSearchInput.show();
        } else {
            doubleSearchInput.hide();
        }
    }
   
    $scope.submitSerchForm = function() {
        var searchReqList = [];    
        var searchInputs = $('.searchInputWrapper');
        
        //ALL push: ƒ, columnName: "quant", operator: ">", data: Array(1), type: "number"}
        //BOOLEAN {columnName: "exist", operator: "False", data: [null], type: "undefined"}
        //Cложность из-за добавленной exist boolean строки
        //инпут данных отсутствует, тип берется с инпута , а оператор на сервер должен быть передан как данные
        //boolean приведен к columnName: "exist", type: "boolean", operator: undefined, data: Array(1) -> False
        for (var i = 0; i < searchInputs.length; i++) {
            var serchElement = new SearchElement();
            var searchInputEl = $(searchInputs[i]);
            var sInputType = searchInputEl.find('.mainSearchInput').attr('type');
            var sOperator = searchInputEl.find('.searchOptionHidden').val();
            var sMainValue = searchInputEl.find('.mainSearchInput').val();
            serchElement.columnName = searchInputEl.find('.searchNameHidden').val();
            serchElement.type = (sInputType !== undefined) ? sInputType : 'boolean';
            serchElement.operator = (sInputType !== undefined) ? sOperator : null;
            serchElement.push((sInputType !== undefined) ? sMainValue : sOperator);
            var doubleInput = searchInputEl.find('.doubleSearchInput');
            if (doubleInput.is(':visible') && doubleInput.val() !== null && doubleInput.val() !== undefined) {
                serchElement.push(doubleInput.val());
            }
            searchReqList.push(serchElement);
        }

        $http({
            method : 'POST', 
            url : '/searchQuery', 
            data: {
                searchQuery: searchReqList,
                limit : 50,
                offset : 0 
            }
        }).then(function(resp) {
            $location.path('/productTable');
                $rootScope.shared =  angular.fromJson(resp.data.response);
        }, function(resp) {
            exception.init(resp.data.response);
            exception.showExMessage();
        });
    };
});

adminApp.controller('productTableController', function($scope, $http, $rootScope, $location) {    
    $scope.productTableRow = $rootScope.shared;
    
    $scope.selectProduct = function() {
        pathPTVariable = '/admin/product/' + this.element.id;
        $http({method: 'GET', url: pathPTVariable})
                .then(function(resp) {
                    $location.path(pathPTVariable);
                    $rootScope.shared = angular.fromJson(resp.data.response);
                    console.log($rootScope.shared);
                }, function(resp) {
                    console.log(resp.data.response);
                });
    };
});

adminApp.controller('adminProductController', function($scope, $rootScope) {
    $scope.prods = [$rootScope.shared];
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

///SERVICES

///DIRECTIVES
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
                addImgReader.onload = function() {
                        $('#prodBigImage').attr('src', addImgReader.result);
                };
                addImgReader.readAsDataURL(addElm);
            });  
        }
    };
});

///SUBROUTINES

///ADMIN_EXCEPTION_OBJECT
function Exception() {
    var exceptionModal = $('.exceptionModalWindow');
    this.exceptionName;
    this.message;
    this.cause;
    this.sTrace;
    this.isInit = 0;
    this.init = function(dataResponse) {
        this.exceptionName = dataResponse.exceptionName || '';
        this.message = dataResponse.message || '';
        this.cause = dataResponse.cause || '';
        this.sTrace = dataResponse.sTrace || '';
        this.isInit = 1;
    };
    this.showExMessage = function() {
        if (isInit === 0) {
            console.log('Exception was not initiolized.');
            return;
        }
        if (exceptionModal.is(':visible')) {
            exceptionModal.hide();
        } else {
            exceptionModal.show();
        }
        isInit = 0;
    };
}

///OBJECT FOR SEARCH TABLE REQUEST
function SearchElement() {
    this.columnName; 
    this.operator; 
    this.type; 
    this.data;
    this.push = function(el) {
        if (!this.data) { 
            this.data = []; 
        }
        this.data.push(el); 
        };
    }