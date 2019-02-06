//Привязка данных
//ng-bind: осуществляет привязку к свойству innerText html-элемента
//ng-bind-html: осуществляет привязку к свойству innerHTML html-элемента // REQUIRE SANITAZER
//ng-bind-template: аналогична ng-bind за тем исключением, что позволяет установить привязку сразу к нескольким выражениям
//ng-model: создает двустороннюю привязку
//ng-non-bindable: определяет участок html-кода, в котором привязка не будет использоваться

let indexApp = angular.module('indexPage', ['ngRoute', 'ngSanitize', 'ngCookies']);

indexApp.config(function ($routeProvider, $locationProvider) {
    $routeProvider.when('/', {
        controller: 'IndexInitController'
    }).when('/products/:catId/:pageId', {
        templateUrl: '/html/index_products.html',
        controller: 'ProductController'
    });
    
    $routeProvider.otherwise({redirectTo: '/'});
});

//INDEX INIT CONTROLLER
indexApp.controller('IndexInitController', function ($scope, $log ,$location, CategoryService, SharedDataService, CartCookiesService) {
    
    CartCookiesService.initCookiesKey()
            .then(null, function (ex) {
                $log.warn(ex);
            });

    CategoryService.getAllCategories()
            .then(function (res) {
                $scope.categories = res;
            }, function (ex) {
                $log.error(ex);
            });


    $scope.getProductsByCategory = function (catName, catId) {
        if (!catId && !catName) {
            $log.warn('indexInitController getProductsByCategory: argument catId === '.concat(catId));
            return;
       }
       
       SharedDataService.setCurrentCtegoryId(catId);
       SharedDataService.setCurrentCtegoryName(catName);
       $location.path('/products/' + catName + '/' + 1);        
    };
});

//CART COOKIES SERVICE
indexApp.factory('CartCookiesService', function ($log, $cookieStore, $http, $q) {
    
    var CART = new Cart();
    var COOKIES_KEY;
    
    function _Product(id, name, price, url, exist) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.url = url;
        this.exist = exist;
        this.quantInCart = 1;
    };

    function Cart() {
        this.uuid;
        this.products = [];
        return {
            products: this.products,
            uuid: this.uuid
        };
    };
    
    return {
        initCookiesKey() {
            return $http({url : '/getCookiesKey', method : 'POST'})
                    .then(function(res) {
                        COOKIES_KEY = res.data.response;
                    }, function (ex) {
                        return $q.reject(ex.data.exception);
                    });
        },
        deleteCart() {
            $cookieStore.remove(COOKIES_KEY);
        },
        initCart : function () {
            $http({url: '/createCart', method: 'POST'})
                    .then(function (res) {
                        CART.uuid = res.data.response;
                    }, function (ex) {
                        $log.warn(ex.data.exception);
                    }); 
            },
        setProduct: function (id, name, price, url, exist) {
            $log.info('Проверка аргументов');
            $log.info(id && name && price !== undefined && url && exist !== undefined);
            if (id && name && price !== undefined && url && exist !== undefined) {
            for (var i = 0; i < CART.products.length; i++) {
                if (CART.products[i].id == id) {
                    CART.products[i].quantInCart++;
                    return;
                }
            }
            
            CART.products.push(new _Product(id, name, price, url, exist));
            }
        },
        getCart : function (){
           return CART; 
        },
        setCookies : function() {
            $cookieStore.put(COOKIES_KEY, CART);
        },
        checkCookies() {
            var tmp = $cookieStore.get(COOKIES_KEY);
            if (tmp) {
                CART = tmp;
                return true;
            }
            return false;
        },
    };
});

//PRODUCT CONTROLLER
indexApp.controller('ProductController', function ($log, $scope, $timeout, $location, $compile, $routeParams, ProductService, Paginator, SharedDataService, CartCookiesService) {
    
    ProductService.getProductsByCategory($routeParams.catId, Paginator.getLimit(), Paginator.getOffset())
            .then(function (res) {
                SharedDataService.setProducts(res.products);
                SharedDataService.setProductsCount(res.count);
                $scope.products = res.products;
                $('.paginationBar').html($compile(Paginator.getPaginRow(res.count))($scope));
                Paginator.setOffset(0);
            }, function (ex) {
                $log.warn(ex);
            });

    $scope.pagClickedStyle = function (num) {
        if (!num) {
            $log.warn('ProductController: pagClickedStyle arg === undefined');
            return;
        }
        if ($routeParams.pageId == num) {
            return Paginator.getStyle();
        }

    };
    
    $scope.getAnotherPage = function(num) {
        if (!num) {
            $log.warn('ProductController: getAnotherPage arg === undefined');
            return;
        }
        Paginator.setOffset( (num  - 1) * Paginator.getLimit());
        $location.path('/products/' + SharedDataService.getCurrentCtegoryName() + '/' + num);
    };
    
    $scope.like = function (prodId) {
        SharedDataService.like(prodId)
                .then(function () {
                    SharedDataService.incrementLike(prodId);
                }, function (ex) {
                    $log.warn(ex);
                });
    };
    
    $scope.addMinProdToCart = function(prodId, prodName, prodPrice, productUrl, productExist) {
        if (!CartCookiesService.checkCookies()) {
            CartCookiesService.initCart();
        }
        
        $timeout(function () {
            CartCookiesService.setProduct(prodId, prodName, prodPrice, productUrl, productExist);
            CartCookiesService.setCookies();
            console.log(CartCookiesService.getCart());
            if (CartCookiesService.getCart().uuid == undefined) {
                CartCookiesService.deleteCart();
                $log.error('TimeException: 500 млс не достаточно для полной инициализации UUID');
            }
        }, 500);
    };

});

//CHECK VALUE FILTER
indexApp.filter('defaultVal', function () {
    return function (val, arg) {
        if (val == undefined) {
            if (arg != undefined) {
                return arg;
            }
            return '-';
        }
        return val;
    };
});

//PRODUCT SERVICE
indexApp.factory('ProductService', function ($q, $http) {
    
    var URL_GET_PRODUCTS = '/getProductsPage/';
    
    return {
       getProductsByCategory : function(catId, limit, offset) {
           if (catId && limit && offset || offset === 0) {
               return $http({url : URL_GET_PRODUCTS, method : 'POST', data : { id : catId, limit : limit, offset : offset}})
                       .then(function (res) {
                           return res.data.response;
                        }, function (ex) {
                            return $q.reject(ex.data.response);
                        });
           } else {
               return $q.reject('ProductService.getProductsByCategory args: '.concat((catId) ? '' : ' catId == ' + catId)
                       .concat((limit) ? ' ' : ' limit ===' + limit).concat((offset) ? ' ' : ' offset === ' + offset));
           }
       }, 
       like : function (prodId) {
           if (prodId) {
               return $http({url : '/incrementMark', method : 'POST'})
                       .then(null, function (ex) {
                           return $q.reject(ex.data.response);
                        });
           } else {
             return $q.reject('ProductService.like: arg ==='.concat(prodId));  
           }
       }
    };
});

//CATEGORY SERVICE
indexApp.factory('CategoryService', function ($http, $q) {

    var URL_GET_ALL_CATEGORIES = '/getAllCategories';

    return {
        getAllCategories: function () {
            return $http({url: URL_GET_ALL_CATEGORIES, method: 'POST'})
                    .then(function (res) {
                        return res.data.response;
                    }, function (res) {
                        return $q.reject(res.data.response);
                    });
        }
    };
});

// SHARED DATA SERVICE
indexApp.factory('SharedDataService', function ($log) {
    
    var currentCategoryId;
    var currentCategoryName;
    var products;
    var productsCount;
    
    return {
        incrementLike : function (prodId) {
            products.forEach(function (el) {
                if (el.id == prodId) {
                    el.mark =  Number(el.mark) + 1;
                }
            });
        },
        setCurrentCtegoryId: function (curCatId) {
            if (curCatId === undefined || Number(curCatId) < 0) {
                $log.warn('SharedDataService: argument curCat === undefined ||  curCatId < 0');
            }
            currentCategoryId = curCatId;
        },
        getCurrentCtegoryId: function () {
            return currentCategoryId;
        },
        setCurrentCtegoryName: function (curCatName) {
            if (curCatName === undefined) {
                $log.warn('SharedDataService: argument curCatName === undefined');
            }
            currentCategoryName = curCatName;
        },
        getCurrentCtegoryName: function () {
            return currentCategoryName;
        },
        setProducts: function (prods) {
            if (prods === undefined) {
                $log.warn('SharedDataService: argument prods === undefined');
            }
            products = prods;
        },
        getProducts: function () {
            return products;
        },
        setProductsCount: function (count) {
            if (count === undefined || Number(count) < 0) {
                $log.warn('SharedDataService: argument num === undefined || < 0');
            }
            productsCount = count;
        },
        getProductsCount: function () {
            return productsCount;
        }
    };
});




























//PAGINATOR
indexApp.factory('Paginator', function() {
    
    var clickedStyle = { 'background-color' : '#CEFF81' };
    var limit = 5;
    var offset = 0;
    var clickedElemtIndex = -1;

    return {
        getPaginArray : function(elementNum) {
            var pagElement = [];
            var mod = elementNum % limit;
            for (var i = 1; i <= elementNum % limit + ((mod === 0) ? 0 : 1); i++) {
                pagElement.push('<div class="pagElement"  ng-style="pagClickedStyle(' + i + ')"  ng-click="getAnotherPage('+ i +')"> <p style="width:20px;" class="pagP">' + i + '</p> </div>');
            }   
            return pagElement;
        },
        getPaginRow : function(elementNum) {
            var pagElement = '';
            var mod = elementNum % limit;
            for (var i = 1; i <= elementNum / limit + ((mod === 0) ? 0 : 1); i++) {
                pagElement += '<div class="pagElement"  ng-style="pagClickedStyle(' + i + ')"  ng-click="getAnotherPage('+ i +')"> <p style="width:20px;" class="pagP">' + i + '</p> </div>';
            }   
            return pagElement;
        },
        setLimit : function (num) {
            limit = num;
        },
        getLimit () {
            return limit;
        },
        setOffset : function (num) {
            offset = num;
        },
        getOffset () {
            return offset;
        },
        setStyle : function(style) {
            clickedStyle = style;
        },
        getStyle : function() {
            return clickedStyle;
        },
        setClickedElem : function(num) {
            clickedElemtIndex = num;
        },
        getClickedElem : function() {
            return clickedElemtIndex;
        }
    };
});

