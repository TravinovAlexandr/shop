var adminApp = angular.module('adminPage', ['ngRoute']);
var authApp = angular.module('authPage', []);

adminApp.config(function($routeProvider, $locationProvider) {
    $routeProvider.when('/addNewProduct', {
        templateUrl : '/html/add_product.html',
        controller: 'addProductController'
        
    }).when('/searchProduct', {
        templateUrl : '/html/search_form.html',
        controller: 'searchFormleController'
        
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

//ИНИЦИАЛИЗАЦИЯ ГЛОБАЛЕЙ
var exception = new Exception();


adminApp.controller('addProductController', function($scope, $http) {    
    $http({method : 'POST', url : '/getAllCategories'})
            .then(function(resp) {
                var objResp = angular.fromJson(resp.data);
                $scope.categories = objResp.response;
    }, function(resp) {
        exception.init(resp.data.resonse);
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

adminApp.controller('searchFormleController', function($scope, $http, $timeout, $location, $rootScope) {
    //list-casche for searchQuery
    var searchReqList = [];
    
    $http({method : 'POST', url : '/searchForm'}).then(
        function(resp) {
            $scope.searchForm = angular.fromJson(resp.data.response);
            $timeout(function() {
                $('.searchSelect').on('change', searchChangeOptionEvent);        
            }, 1500);
        }, function(resp) {
            exception.show(resp.data.response);
    });

    $scope.submitSerchForm = function() {
        var searchInputs = $('.searchInputWrapper');
        
        //ALL push: ƒ, columnName: "quant", operator: ">", data: Array(1), type: "number"}
        //BOOLEAN {columnName: "exist", operator: "False", data: [null], type: "undefined"}
        //Cложность из-за добавленной exist boolean строки
        //инпут данных отсутствует, тип берется с инпута , а оператор на сервер должен быть передан как данные
        //boolean приведен к columnName: "exist", type: "boolean", operator: undefined, data: Array(1) -> False
        
        //переписать
        for (var i = 0; i < searchInputs.length; i++) {
            var serchElement = new SearchElement();
            var searchInputEl = $(searchInputs[i]);
            var sInputType = searchInputEl.find('.mainSearchInput').attr('type');
            var sOperator = searchInputEl.find('.searchOptionHidden').val();
            var sMainValue = (sInputType !== 'textarea') ? searchInputEl.find('.mainSearchInput').val() : searchInputEl.find('.mainSearchInput').text();
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
        ////УБРАТЬ из скопа для передачи между контроллерами
        $rootScope.searchReqList = searchReqList;
        
        console.log(searchReqList);
        $http({
            method : 'POST', 
            url : '/searchQuery', 
            data: {
                searchQuery: searchReqList,
                limit : 5,
                offset : 0
            }
        }).then(function(resp) {
            $location.path('/productTable');
                //избавиться от шаред
                $rootScope.shared =  angular.fromJson(resp.data.response);
        }, function(resp) {
            exception.show(resp.data.response);
            searchReqList = null;
        });
    };
});

adminApp.controller('productTableController', function($scope, $http, $rootScope, $location, $compile) {
    var tablePagLimit = 5;
    //избавиться от $rootScope.shared через шаред класс или нг фактори
    $scope.productTableRow = $rootScope.shared;
    
    if ($rootScope.shared[0] === undefined || $rootScope.shared[0] === null) {
        $('.prodTabMessage').html('<p>Ни один товар не соответствует заданному условию поиска.</p>');
        $rootScope.searchReqList = null;
        
    } else {
        paginator($rootScope.shared[0].productsCount, $compile, $scope, tablePagLimit);
    }
    
    var prevPagEl = null;
    
    $scope.getAnotherPage = function() {
        if (prevPagEl !== null) {
            $(prevPagEl).removeClass('curPagEl');
        }
        
        var curPagEl = $($(event.currentTarget)[0]);
        prevPagEl = curPagEl;
        curPagEl.addClass('curPagEl');
        
        $http({
            method : 'POST', 
            url : '/searchQuery', 
            data: {
                searchQuery: $rootScope.searchReqList,
                limit : tablePagLimit,
                offset : ($(event.currentTarget)[0].querySelector('p').innerText - 1) * tablePagLimit
            }
        }).then(function(resp) {
            $location.path('/productTable');
//            $rootScope.shared =  angular.fromJson(resp.data.response);
            $scope.productTableRow = angular.fromJson(resp.data.response);
        }, function(resp) {
            exception.show(resp.data.response);
        });
    };
   
    $scope.selectProduct = function() {
        pathPTVariable = '/admin/product/' + this.element.id;
        $http({method: 'GET', url: pathPTVariable})
                .then(function(resp) {
                    $location.path(pathPTVariable);
                    $rootScope.shared = angular.fromJson(resp.data.response);
                }, function(resp) {
                    exception.show(resp.data.response);
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
                console.warn('Dirrective ng-file-up requires ng-model.');
                return;
            }
            
            element.bind('change', function(el) {
                var ImgReader = new FileReader();
                var addElm = $(el.target).prop('files')[0];
                
                ngModel.$setViewValue(addElm);
                
                ImgReader.onload = function() {
                        $('#prodBigImage').attr('src', ImgReader.result);
                };
                
                ImgReader.readAsDataURL(addElm);
            });  
        }
    };
});

///SUBROUTINES
function searchChangeOptionEvent(e)  {
    var searhInputsDiv = $($(e.target).parent().parent().parent());
    var doubleSearchInput = searhInputsDiv.find('.doubleSearchInput');
        
    searhInputsDiv.find('.searchOptionHidden').val(e.target.value);
    
    if (e.target.value === 'Between' || e.target.value === '>=<') {
        doubleSearchInput.show();
    } else {
        doubleSearchInput.hide();
    }
}

function paginator(rowCount, compile, scope, limitPag) {
    if (!rowCount || !compile || !scope || !limitPag) {
        console.error("function paginator(rowCount, compile, scope, limitPag): any arguments === null or undefined.");
    }
    
    var pagElement = '';
    var mod = rowCount % limitPag;
    
    for (var i = 1; i <= rowCount % limitPag + ((mod === 0) ? 0 : 1); i++) {
        pagElement += '<div class="pagElement" ng-click="getAnotherPage($event)"><p style="width:20px;" class="pagP">' + i + '</p></div>';
    }
    
    var compiledScopedElem = compile(angular.element(pagElement))(scope);
    
    $('.paginationBar').html(compiledScopedElem);
}
    
//JQUERY EVENTS INIT
//close exception window
setTimeout(function () {
    var adminExWrap = $('.adminExWrapper');
    
    adminExWrap.find('.quitWrapper').click(function () {
        adminExWrap.hide();
        adminExWrap.attr('style', 'width: 40%; margin-left: 30%;');
        exception.invalidate();
    });
}, 1000);

//plus size exeption window
setTimeout(function () {  
    $('.adminExWrapper').find('.adminExName').dblclick(function () {
        console.log(1);
        $('.adminExWrapper').attr('style', 'width: 60%; margin-left: 20%;');
    });
}, 1000);

///OBJECTS
function Exception() {
    var exceptionModal = $('.adminExWrapper');
    var exName = exceptionModal.find('.exceptionModalName');
    var exMess = exceptionModal.find('.exceptionModalMessage');
    var exCause = exceptionModal.find('.exceptionModalCause');
    var exStrace = exceptionModal.find('.exceptionModalStrace');
    this.show = function(dataResponse) {
        try {
        exName.text(dataResponse.exceptionName || '');
        exMess.text(dataResponse.message || '');
        exCause.text(dataResponse.cause || '');
        exStrace.html(dataResponse.strace || '');
        exceptionModal.show();
        } catch (ex) {
            console.warn('Exception: fields are not init.');
        }
    };
    this.invalidate = function() {
        try {
            exName.text('');
            exMess.text('');
            exCause.text('');
            exStrace.html('');
        } catch (ex) {
            console.warn('Exception: fields are not init.');
        }
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