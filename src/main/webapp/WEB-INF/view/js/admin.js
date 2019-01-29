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

adminApp.controller('addProductController', function($scope, $http, exception) {    
    
    $http({method : 'POST', url : '/getAllCategories'})
            .then(function(resp) {
                $scope.categories = angular.fromJson(resp.data);
    }, function(resp) {
        exception.show(resp.data.resonse);
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
        
        $http({method : 'POST', url : '/addProduct', data : addProdMultyForm, headers : {'Content-Type': undefined}})
                .then(function(){}, 
        function(resp) {
            exception.show(resp.data.response);
        }); 
    };
});

adminApp.controller('searchFormleController', function($scope, $http, $timeout, $location, adminData, exception) {
    
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
        
        var searchReqList = [];
        
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

        adminData.setSearchConditions(searchReqList);
                
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
                adminData.setProductTable(angular.fromJson(resp.data.response));
        }, function(resp) {
            exception.show(resp.data.response);
            adminData.setSearchConditions(null);
        });
    };
});

adminApp.controller('productTableController', function($scope, $http, $location, $compile, $rootScope ,adminData, exception) {
    
    var tablePagLimit = 5;
    $scope.productTableRow = adminData.getProductTable();
    
    if (adminData.getProductTable()[0] === undefined || adminData.getProductTable()[0] === null) {
        $('.prodTabMessage').html('<p>Ни один товар не соответствует заданному условию поиска.</p>');
        adminData.setSearchConditions(null);
    } else {
        paginator(adminData.getProductTable()[0].productsCount, $compile, $scope, tablePagLimit);
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
                searchQuery: adminData.getSearchConditions(),
                limit : tablePagLimit,
                offset : ($(event.currentTarget)[0].querySelector('p').innerText - 1) * tablePagLimit
            }
        }).then(function(resp) {
            $location.path('/productTable');
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
                    adminData.setOriginalProduct(resp.data.response);
                    adminData.setBindProduct(resp.data.response);
                }, function(resp) {
                    exception.show(resp.data.response);
                });
    };
});

adminApp.controller('adminProductController', function($scope, $http, $window, $location, adminData, exception) {
    //пишу как массив что-бы использовать ng-repeat что-бы упростить биндинг ng-model
    $scope.prods = [adminData.getBindProduct()];
    
    $scope.jampToProductTable = function() {
        adminData.deleteProductData();
        $location.path('/productTable');
    };

    $scope.getOriginSingleVal = function() {
        var label = $(event.currentTarget).parent().find('label').text();
        
        switch (label.trim()) {
            case 'Название:': $scope.prods[0].product.name = adminData.getOriginalProduct().name; break;
            case 'Описание:': $scope.prods[0].product.description = adminData.getOriginalProduct().description; break;
            case 'Цена:': $scope.prods[0].product.price = adminData.getOriginalProduct().price; break;
            case 'Оценка:': $scope.prods[0].product.mark = adminData.getOriginalProduct().mark; break;
            case 'Кол-во:': $scope.prods[0].product.quantity = adminData.getOriginalProduct().quantity; break;
        }
    };
    
    $scope.getOriginComment = function(event) {
        var commentId = $(event.currentTarget).parent().find('.adminProdCommentId').val();
        var isCycleEnd = false;
        for (var i = 0; i < $scope.prods[0].product.comments.length; i++) {
            for (var j = 0; j < adminData.getOriginalProduct().comments.length; j++) {
                if (Number(adminData.getOriginalProduct().comments[j].id) === Number(commentId)) {
                    $scope.prods[0].product.comments[i] = adminData.getOriginalComment(j);
                    console.log($scope.prods[0].product.comments[i]);
                    isCycleEnd = true;
                    break;
                }         
                if (isCycleEnd) {
                    break;
                }
            }
        }
    };
    
    $scope.getOriginCategories = function() {
        $scope.prods[0].product.category = adminData.getOriginalCategories();
    };
    
    $scope.deleteCategory = function(event) {
        var catId = Number($(event.currentTarget).parent().find('input').val());
        for (var i = 0; i < $scope.prods[0].product.category.length; i++) {
            if (Number($scope.prods[0].product.category[i].id) === catId) {
                $scope.prods[0].product.category.splice(i, 1);
            }
        }
    };
    
    $scope.deleteProduct = function() {
        if ($window.confirm('Подтвердите удаление товара.')) {
            $http({url: '/admin/deleteProduct/' + $('.adminProdId').val(), method: 'POST'})
                    .then(function() {
                    adminData.deleteProductData();    
                    $location.path('/searchProduct');
            }, function(resp) {
                exception.show(resp);
            });
        }
    };
    
    $scope.deleteComment = function(event) {
        var commentId = $(event.currentTarget).parent().find('.adminProdCommentId').val();
        
        if ($window.confirm('Подтвердите удаление комментария.')) {
            $http({url: 'admin/deleteComment/' + commentId , method: 'POST'})
                    .then(function() {
                        for (var i = 0; i < $scope.prods[0].comments.length; i++) {
                            if ($scope.prods[0].product.comments[i].id === commentId) {
                                $scope.prods[0].product.comments.splice(i, 1);
                            }
                        }
            }, function(resp) {
                exception.show(resp);
            });
        }
    };
    
    $scope.getOriginImg = function() {
        $('#adminProdImage').attr('src',  adminData.getOriginalProduct().imgUrl);
    };    
    
    $scope.addCategories = function() {
        var addCatLink = $('.adminProdUpAllCategoriesWrapper');
        if (addCatLink.is(':visible')) {
            addCatLink.hide();
        } else {
            addCatLink.show();
        }
    };
    
    $scope.addCategory = function(event) {
        var newCatId = $(event.currentTarget).parent().find('input').val();
        var isExist = false;
        for (var i = 0; i < $scope.prods[0].product.category.length; i++) {
            if (Number($scope.prods[0].product.category[i].id) === Number(newCatId)) {
                isExist = true;
            }
        }
        if (!isExist) {
            $scope.prods[0].product.category.push(adminData.getCategory(newCatId));
        }
    };
    
});

///FACTORIES
adminApp.factory('exception', function($log) {
    var exceptionModal = $('.adminExWrapper');
    var exName = exceptionModal.find('.exceptionModalName');
    var exMess = exceptionModal.find('.exceptionModalMessage');
    var exCause = exceptionModal.find('.exceptionModalCause');
    var exStrace = exceptionModal.find('.exceptionModalStrace');
    
    function clear() {
        exName.text('');    
        exMess.text('');
        exCause.text('');
        exStrace.html('');
    }
        
    return {
        show: function(dataResponse) {
            try {
                clear();
                exName.text(dataResponse.exceptionName || '');
                exMess.text(dataResponse.message || '');
                exCause.text(dataResponse.cause || '');
                exStrace.html(dataResponse.strace || '');
                exceptionModal.show();
            } catch (ex) {
                $log.warn('Exception: fields are not init.');
            }
        }
    };
});

adminApp.factory('adminData', function($log) {
    //conditions for searche form
    var searchConditions;
    //product searche table
    var productTable;
    //for admin product update model
    var bindProduct;
    //need to hold original response data. without this object angular binding will interfear with bindProduct
    var originalProduct;
    
    function Product() {
        this.name; this.description; this.price; this.quantity; this.mark; this.imgUrl;  this.categories = new Array() ; this.comments = new Array(); this.allCategories = new Array(); 
        this.init = function(data) { this.name = data.product.name; this.description = data.product.description;  this.price = data.product.price; 
            this.quantity = data.product.quantity; this.mark = data.product.mark; this.imgUrl = data.product.imgUrl;
            
            for (var i = 0; i < data.product.comments.length; i++) {
                var comment = new Comment(data.product.comments[i].id, data.product.comments[i].nick, data.product.comments[i].body);
                this.comments.push(comment);
            }
            for (var i = 0; i < data.product.category.length; i++) {
                var category = new Category(data.product.category[i].id, data.product.category[i].name);
                this.categories.push(category);
            }
            for (var i = 0; i < data.allCategories.length; i++) {
                var category = new Category(data.allCategories[i].id, data.allCategories[i].name);
                this.allCategories.push(category);
            }
        };
    }
    
    function Comment(id, nick, body) { this.id = id; this.nick = nick; this.body = body; }
    function Category(id, name) { this.id = id; this.name = name; }
    
    return {
        deleteProductData() {
           bindProduct = null;
           originalProduct = null;
        },
        getCategory: function(catId) {
            for (var i =0; i < bindProduct.allCategories.length; i++) {
                if (Number(bindProduct.allCategories[i].id) === Number(catId)) {
                    return bindProduct.allCategories[i];
                }
            }
        },
        getOriginalCategories: function() {
           var cats = originalProduct.categories;
           var tmpCatList = new Array();
           for (var i = 0; i < cats.length; i++) {
               tmpCatList.push(new Category(cats[i].id, cats[i].name));
           }
            return tmpCatList;
        },
        getOriginalComment: function(index) {
           var com = originalProduct.comments[index];
           return new Comment(com.id, com.nick, com.body);
        },
        setOriginalProduct: function(prod) {
           if (prod === undefined) {
               $log.warn('Illegal argument: undefined.');
           }
           originalProduct = new Product();
           originalProduct.init(prod);
       },
       getOriginalProduct: function() {
           if (originalProduct === null) {
               $log.warn('originalProduct === null');
           }
           return originalProduct;
       },
        setBindProduct: function(prod) {
           if (prod === undefined) {
               $log.warn('Illegal argument: undefined.');
           }
           bindProduct = prod;
       },
       getBindProduct: function() {
           if (bindProduct === null) {
               $log.warn('product === null');
           }
           return bindProduct;
       },
       setSearchConditions: function(conditions) {
           if (conditions === undefined) {
               $log.warn('Illegal argument: undefined.');
           }
           searchConditions = conditions;
       },
       getSearchConditions: function() {
           if (searchConditions === null) {
               $log.warn('searchConditions === null');
           }
           return searchConditions;
       },
       setProductTable: function(prodTab) {
           if (prodTab === undefined) {
               $log.warn('Illegal argument: undefined.');
           }
           productTable = prodTab;
       },
       getProductTable: function() {
           if (productTable === null) {
               $log.warn('productTable === null');
           }
           return productTable;
       }
    };
});

///DIRECTIVES
adminApp.directive('ngImg', function() {
    return {
        restrict : 'A',
        link : function(scope, element, attrs) {
            element.bind('change', function(el) {
                var ImgReader = new FileReader();
                var addElm = $(el.target).prop('files')[0];
                ImgReader.onload = function() {
                        $('#adminProdImage').attr('src', ImgReader.result);
                };
                ImgReader.readAsDataURL(addElm);
            });  
        }
    };
});

adminApp.directive('ngFileUpp', function() {
    return {
        restrict : 'A',
        require : 'ngModel',
        link : function(scope, element, attrs, ngModel) {
            element.bind('change', function(el) {
                var ImgReader = new FileReader();
                var addElm = $(el.target).prop('files')[0];
//                ngModel.$setViewValue(addElm);
                ImgReader.onload = function() {
                        $('#prodBigImage').attr('src', ImgReader.result);
                        $('#prodBigImage').attr('ng-src', ImgReader.result);
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
        adminExWrap.attr('style', 'width: 40%; margin-left: 30%;');
        adminExWrap.hide();
    });
}, 1000);

//plus size exeption window
setTimeout(function () {  
    $('.adminExWrapper').find('.adminExName').dblclick(function () {
        $('.adminExWrapper').attr('style', 'width: 60%; margin-left: 20%;');
    });
}, 1000);

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