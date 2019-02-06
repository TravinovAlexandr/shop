//Привязка данных
//ng-bind: осуществляет привязку к свойству innerText html-элемента
//ng-bind-html: осуществляет привязку к свойству innerHTML html-элемента // REQUIRE SANITAZER
//ng-bind-template: аналогична ng-bind за тем исключением, что позволяет установить привязку сразу к нескольким выражениям
//ng-model: создает двустороннюю привязку
//ng-non-bindable: определяет участок html-кода, в котором привязка не будет использоваться

var indexApp = angular.module('indexPage', ['ngRoute', 'ngSanitize', 'ngCookies']);

indexApp.config(function ($routeProvider, $locationProvider) {
    $routeProvider.when('/', {
        controller: 'IndexInitController'
    }).when('/products/:catId/:pageId', {
        templateUrl: '/html/index_products.html',
        controller: 'ProductController'
    }).when('/cart', {
        templateUrl: '/html/cart.html',
        controller: 'CartController'
    });
    $routeProvider.otherwise({redirectTo: '/'});
});

//INDEX INIT CONTROLLER
indexApp.controller('IndexInitController', function ($scope, $log, $location, CategoryService, CartService) {
    
    if (CartService.checkCookies()) {
        _refreshView();
    }
    
    $scope.$on('changeCartData', function () {
        _refreshView();
        $scope.cartProds = CartService.getCart().products;
    });

    CategoryService.getAllCategories()
            .then(function (res) {
                $scope.categories = res;
            }, function (ex) {
                $log.error(ex);
            });

    $scope.getProductsByCategory = function (catId) {
        if (!catId) {
            $log.warn('indexInitController getProductsByCategory: argument catId === '.concat(catId));
            return;
       }
       $location.path('/products/' + catId + '/' + 1);        
    };
    
    $scope.cartProdUp = function () {
        //декремент  установлен правильно
        CartService.decCartPageIndex();
    };
    
    $scope.cartProdDown = function () {
        //инкремент установлен правильно
        CartService.incCartPageIndex();
    };
    
    $scope.deleteProductMinCart = function (num) {
        CartService.deleteProduct(num);
        CartService.setCookies();
        _refreshView();
    };
    
    $scope.cartProds = CartService.getCart().products;
    CartService.computeCartSizePrice();
    
    function _refreshView () {
        CartService.computeCartSizePrice();
        $scope.cartQuant = CartService.getCartSize();
        $scope.cartPrice = CartService.getCartPrice();
    };
    
});

//CART CONTROLLER
indexApp.controller('CartController', function ($scope, CartService) {
    
    $scope.incProductQuant = function (prodId) {
        CartService.getCart().products.forEach(function (el) {
            if (el.id === prodId) {
                el.quantInCart++;
                CartService.setCookies();
                $scope.$emit('changeCartData');
                return;
            }
        });
    };
    
    $scope.decProductQuant = function (prodId) {
        var products = CartService.getCart().products;
        for (var i = 0; i < products.length; i++) {
            if (products[i].id === prodId) {
                if (products[i].quantInCart === 1) {
                    products.splice(i, 1);
                    CartService.setCookies();
                } else {
                    products[i].quantInCart--;
                    CartService.setCookies();
                }
                
                $scope.$emit('changeCartData');
                break;
            }
        }
    };
    
    $scope.deleteProductBigCart = function (prodId) {
        console.log(CartService.getCart().products);
        var products = CartService.getCart().products;
        for (var i = 0; i < products.length; i++) {
            if (products[i].id === prodId) {
                products.splice(i, 1);
                CartService.setCookies();
                $scope.$emit('changeCartData');
                break;
            }
        }
    };
    
});

//PRODUCT CONTROLLER
indexApp.controller('ProductController', function ($log, $scope, $timeout, $location, $compile, $routeParams, ProductService, Paginator, CartService) {
    
    ProductService.getProductsByCategory($routeParams.catId, Paginator.getLimit(), Paginator.getOffset())
            .then(function (res) {
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
        $location.path('/products/' + $routeParams.catId + '/' + num);
    };

    $scope.minProductLike = function (prodid) {
        ProductService.like(prodid)
                .then(function () {
                    CartService.getCart().products.forEach(function (el) {
                        if (el.id === prodid) {
                            el.mark++;
                            CartService.setCookies();
                            $scope.products.forEach(function (el) {
                                if (el.id === prodid) {
                                    el.mark++;
                                }
                            });
                            return;
                        }
                        
                    });
                }, function (ex) {
                    $log.error(ex);
                });
    };
    
    
    $scope.addMinProdToCart = function(prodId, prodName, prodPrice, productUrl, productExist) {
        if (!CartService.checkCookies()) {
            CartService.initCart();
        }
        
        $timeout(function () {
            CartService.setProduct(prodId, prodName, prodPrice, productUrl, productExist);
            CartService.setCookies();
            
            if (CartService.getCart().uuid === undefined) {
                CartService.deleteCart();
                $log.error('TimeException: 500 млс не достаточно для полной инициализации UUID');
                return;
            }
            
            CartService.computeCartSizePrice();
            $scope.$emit('changeCartData');
            
        }, 500);
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
               return $http({url : '/incrementMark/' + prodId, method : 'POST'})
                       .then(null, function (ex) {
                           return $q.reject(ex.data.response);
                        });
           } else {
             return $q.reject('ProductService.like: arg === '.concat(prodId));  
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

//CART SERVICE
indexApp.factory('CartService', function ($cookieStore, $http) {
    
    var COOKIES_KEY = 'AL_SH_KEY_2019';
    var now = new Date();
    var CART = new Cart();
    var cartPagIndex = 0;
    var cartSize = 0;
    var cartPrice = 0;

    function _Product(id, name, price, url, exist) { this.id = id; this.name = name; this.price = price; this.url = url; this.exist = exist; this.quantInCart = 1; };

    function Cart() { this.uuid; this.products = []; };
    
    return {
        getCartSize : function () {
            return cartSize;
        },
        computeCartSizePrice : function () {
            cartSize = cartPrice = 0;
            CART.products.forEach(function (el) {
                cartSize += el.quantInCart;
                cartPrice += el.quantInCart * el.price;
            });
        },
        getCartPrice : function () {
            return cartPrice;
        },
        incCartPageIndex : function () {
            if (cartPagIndex >= CART.products.length - 3) {
                return;
            }
            cartPagIndex += 3;
        },
        decCartPageIndex : function () {
            if (cartPagIndex === 0) {
                return;
            } 
            cartPagIndex -= 3;
        },
        getMinCartPagIndex() {
            return cartPagIndex;
        },
        deleteCookie() {
            $cookieStore.remove(COOKIES_KEY);
        },
        setCookies : function() {
            $cookieStore.put(COOKIES_KEY, CART, new Date(now.getFullYear(), now.getMonth() + 6, now.getDate()));
        },
        checkCookies() {
            var tmp = $cookieStore.get(COOKIES_KEY);

            if (tmp) {
                CART = tmp;
                return true;
            }
            return false;
        },
        initCart: function () {
            return $http({url: '/createCart', method: 'POST'})
                    .then(function (res) {
                        CART.uuid = res.data.response;
                    }, function (ex) {
                        return ex.data.response;
                    }); 
        },
        getCart : function (){
           return CART; 
        },
        setProduct: function (id, name, price, url, exist) {
            if (id && name && price !== undefined && url && exist !== undefined) {
            for (var i = 0; i < CART.products.length; i++) {
                if (CART.products[i].id === id) {
                    CART.products[i].quantInCart++;
                    return;
                }
            }
            
            CART.products.push(new _Product(id, name, price, url, exist));
            }
        },
        deleteProduct: function (id) {
            if (id) {
                for (var i = 0; i < CART.products.length; i++) {
                    if (CART.products[i].id === id) {
                        if (CART.products[i].quantInCart === 1) {
                            CART.products.splice(i, 1);
                            return;
                        }
                        
                        CART.products[i].quantInCart--;
                    }
                }
            }
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

//CART SLICE FILTER
indexApp.filter('cartProdFilter', function (CartService) {
    return function (val) {
        if (!val) {
            return val;
        }
        
        return val.slice(CartService.getMinCartPagIndex(), CartService.getMinCartPagIndex() +3);
    };
});