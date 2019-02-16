//Привязка данных
//ng-bind: осуществляет привязку к свойству innerText html-элемента
//ng-bind-html: осуществляет привязку к свойству innerHTML html-элемента // REQUIRE SANITAZER
//ng-bind-template: аналогична ng-bind за тем исключением, что позволяет установить привязку сразу к нескольким выражениям
//ng-model: создает двустороннюю привязку
//ng-non-bindable: определяет участок html-кода, в котором привязка не будет использоваться

var indexApp = angular.module('indexPage', ['ngRoute', 'ngSanitize', 'ngCookies']);

indexApp.config(function ($routeProvider) {
    $routeProvider.when('/', {
        controller: 'IndexInitController'
    }).when('/products/:catId/:pageId', {
        templateUrl: '/html/index_products.html',
        controller: 'ProductController'
    }).when('/cart', {
        templateUrl: '/html/cart.html',
        controller: 'CartController'
    }).when('/product/:mainProdId', {
        templateUrl: '/html/product.html',
        controller: 'MainProdController'
    }).when('/contract', {
        templateUrl: '/html/contract.html',
        controller: 'ContractController'
    });
    $routeProvider.otherwise({redirectTo: '/'});
});

//CONTRACT CONTROLLER
indexApp.controller('ContractController', function ($scope, $log, SharedData, CartService) {
    
});

//CONTRACT SERVICE
indexApp.factory('ContractService', function ($http) {
    
});

//INDEX INIT CONTROLLER
indexApp.controller('IndexInitController', function ($scope, $timeout, $log, $location, CategoryService, CartService) {
    if (CartService.checkCookies()) {
        _refreshView();
    }
    
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
        CartService.decCartPageIndex();
    };
    
    $scope.cartProdDown = function () {
        CartService.incCartPageIndex();
    };
    
    $scope.deleteProductMinCart = function (num) {
        CartService.deleteProduct(num);
        CartService.setCookies();
        _refreshView();
    };
    
        
    $scope.showMainProduct = function (prodId) {
        if (prodId) {
            var prevLocation = $location.path();
            $location.path('/cart');
            $timeout(function () {
                var top = $('.anchor' + prodId).offset().top;
                $('body').animate({scrollTop: top}, 300);
            });
            $scope.prevLocation = prevLocation;
        }
    };
    
    _refreshView();
    
    function _refreshView () {
        CartService.computeCartSizePrice();
        $scope.cartQuant = CartService.getCartSize();
        $scope.cartPrice = CartService.getCartPrice();
        $scope.cartProdLength = CartService.getCart().products.length;
        $scope.cartProds = CartService.getCart().products;
    };
    
    function _addProdToCart (prodId, prodName, prodPrice, productUrl, productExist) {
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
            _refreshView();
            
        }, 500);
    };
    
    $scope.$on('changeCartData', function () {
        _refreshView();
    });
    
    $scope.$on('addProdToCart', function (e, data) {
        _addProdToCart(data.prodId, data.name, data.price, data.imgUrl, data.isExist);
    });
    
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
indexApp.controller('ProductController', function ($log, $scope, $location, $compile, $routeParams, ProductService, Paginator, CartService, SharedData) {
    
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

    $scope.minProductLike = function (prodId) {
        if (CartService.isLikePosible(prodId)) {
            ProductService.like(prodId)
                    .then(function () {
                        $scope.products.forEach(function (el) {
                            if (el.id === prodId) {
                                el.mark++;
                                CartService.updateLikeCookies(prodId);
                                return;
                            }
                        });
                    }, function (ex) {
                        $log.error(ex);
                    });
        }
    };
    
    $scope.addMinProdToCart = function (prodId, name, price, imgUrl, isExist) {
        if (prodId && name && price && imgUrl && typeof(isExist) === 'boolean') {
            $scope.$emit('addProdToCart', {
                prodId: prodId, name: name, price: price, imgUrl: imgUrl, isExist: isExist
            });
        }
    };
    
    $scope.showBigProduct = function (prodId) {
        CartService.setRedirectInfo($routeParams.catId, $routeParams.pageId);
        SharedData.setCurrentCategory($routeParams.catId);
        $location.path('/product/' + prodId);
    };
});

/*SHARED DATA*/
indexApp.factory('SharedData', function ($log) {
    
    var products = [];
    var recommended = [];
    var lastAdded = [];
    var lastAllAdded = [];
    var lastAddedIndex = 0;
    var lastAddedOffset = 4;
    var lastAllAddedIndex = 0;
    var lastAllAddedOffset = 6;
    var currentLocation;
    var currentCategory;

    return {
        setCurrentCategory(catId) {
            if (catId) {
                currentCategory = catId;
            }
        },
        getCurrentCategory() {
            return currentCategory;
        },
        getLastAddedOffset() {
           return lastAddedOffset; 
        },
        getLastAddedIndex() {
           return lastAddedIndex; 
        },
        incLastAddedIndex() {
            if (lastAddedIndex < lastAdded.length - lastAddedOffset) {
                lastAddedIndex += lastAddedOffset;
            }
        },
        decLastAddedIndex() {
           if (lastAddedIndex !== 0) {
               lastAddedIndex -= lastAddedOffset;
           }
        },
        setLastAllAdded(prods) {
            if (prods) {
                lastAllAdded = prods;
            }
        },
        getLastAllAdded() {
            return lastAllAdded;
        },
        getLastAllAddedOffset() {
           return lastAllAddedOffset; 
        },
        getLastAllAddedIndex() {
           return lastAllAddedIndex; 
        },
        incLastAllAddedIndex() {
            if (lastAllAddedIndex < lastAllAdded.length - lastAllAddedOffset) {
                lastAllAddedIndex += lastAllAddedOffset;
            }
        },
        decLastAllAddedIndex() {
           if (lastAllAddedIndex !== 0) {
               lastAllAddedIndex -= lastAllAddedOffset;
           }
        },
        setRecommended(recomm) {
            if (!recomm) {
                $log.warn('SharedData: setRecommended arg == undefined');
                return;
            }
            recommended = recomm; 
        },
        getRecommended() {
            return recommended;
        },
        setLastAdded(ladded) {
            if (!ladded) {
                $log.warn('SharedData: setLastAdded arg == undefined');
                return;
            }
            lastAdded = ladded; 
        },
        getLastAdded() {
            return lastAdded;
        },
        setLocation(location) {
            if (!location) {
                $log.warn('SharedData: Location arg == undefined');
                return;
            }
            currentLocation = location;
        },
        getLocation() {
            return currentLocation;
        },
        setProducts(prods) {
            if (!prods) {
                $log.warn('SharedData: setProducts arg == undefined');
                return;
            }
            products = prods;
        },
        getProducts() {
            return products;
        }
    };
});

indexApp.controller('MainProdController', function ($scope, $log, $routeParams, $location, CartService, ProductService, SharedData, Paginator) {
    
    $scope.submitNewComment = function (prodId) {
        if (prodId) {
            ProductService.addNewComment(prodId, $scope.addCommentName, $scope.addCommentBody)
                    .then(function () {
                        ProductService.getComments(prodId)
                        .then(function (res) {
                            $scope.mainProd.comments = res;
                            $scope.addCommentName = '';
                            $scope.addCommentBody = '';
                        }, null);
                    }, function (ex) {
                        $log.error(ex);
                    });
        }
    };
     
    ProductService.selectMainPageProduct($routeParams.mainProdId)
                .then(function (res) {
                    var redirectInfo = CartService.getRedirectInfo();
                    $scope.mainProdRet = 'products/' + redirectInfo.category + '/' + redirectInfo.page;
                    $scope.mainProd = res;
                }, function (ex) {
                    $log.error(ex);
                });
                
    if (SharedData.getCurrentCategory()) {
        ProductService.selectLastAddedInCategory(SharedData.getCurrentCategory())
                .then(function (res) {
                    $scope.sliderElements = res;
                    SharedData.setLastAdded(res);
                }, function (ex) {
                    $log.error(ex);
                });
    }
    
    ProductService.selectLastAllAdded()
                .then(function (res) {
                    $scope.sliderAllElements = res;
                    SharedData.setLastAllAdded(res);
                }, function (ex) {
                    $log.error(ex);
                });
    
    $scope.addToCart = function (prodId, name, price, imgUrl, isExist) {
        if (prodId && name && price && imgUrl && isExist) {
            $scope.$emit('addProdToCart', {
                prodId:prodId, name:name, price:price, imgUrl:imgUrl, isExist:isExist
            });
        }
    };
                
    $scope.likeBigProd = function (prodId) {
        if (CartService.isLikePosible(prodId)) {
            ProductService.like(prodId)
                    .then(function () {
                        var products = SharedData.getProducts();
                        if (!products || products.length === 0) {
                            var info = CartService.getRedirectInfo();
                            ProductService.getProductsByCategory(info.category, Paginator.getLimit(), (info.page - 1) * Paginator.getLimit())
                                    .then(function (res) {
                                        SharedData.setProducts(res.products);
                                        _updateLike(prodId);
                                    }, function (ex) {
                                        $log.warn(ex);
                                    });
                        } else {
                            _updateLike(prodId);
                        }
                    }, function (ex) {
                        $log.error(ex);
                    });
        }
    };
    
    $scope.showLeftSlider = function() {
        if (SharedData.getLastAddedIndex() !== 0) {
            return true;
        }
        return false;
    };
    
    $scope.showRightSlider = function() {
        if (SharedData.getLastAddedIndex() + SharedData.getLastAddedOffset() < SharedData.getLastAdded().length) {
            return true;
        } 
        return false;
    };
    
    $scope.sliderLeftClick = function () {
        SharedData.decLastAddedIndex();
    };
    
    $scope.sliderRightClick = function () {
        SharedData.incLastAddedIndex();
    };
    
    $scope.showLeftAllSlider = function() {
        if (SharedData.getLastAllAddedIndex() !== 0) {
            return true;
        }
        return false;
    };
    
    $scope.showRightAllSlider = function() {
        if (SharedData.getLastAllAddedIndex() + SharedData.getLastAllAddedOffset() < SharedData.getLastAllAdded().length) {
            return true;
        } 
        return false;
    };
    
    $scope.sliderLeftAllClick = function () {
        SharedData.decLastAllAddedIndex();
    };
    
    $scope.sliderRightAllClick = function () {
        SharedData.incLastAllAddedIndex();
    };
    
    $scope.sliderSelectProd = function (prodId) {
        if (prodId) { 
            $location.path('/product/' + prodId);
        }
    };
    
    function _updateLike(prodId) {
        SharedData.getProducts().forEach(function (el) {
            if (el.id === prodId) {
                el.mark++;
                CartService.setCookies();
                $scope.mainProd.mark++;
                CartService.updateLikeCookies(prodId);
                return;
            }
        });
    };
});

//PRODUCT SERVICE
indexApp.factory('ProductService', function ($q, $http) {
    
    var URL_GET_PRODUCTS = '/getProductsPage/';
    var URL_LAST_ADDED_PROD_CATEGORY = '/selectLastAddedInCategory/';
    var URL_LAST_ALL_ADDED = '/selectLastAddedInAllCategories';
    var URL_ADD_NEW_COMMENT = '/addComment/';
    var URL_GET_COMMENTS = '/getAllComments/';
    var LIMIT = 20;
    
    return {
        getComments(prodId) {
            if (prodId) {
                return $http({url: URL_GET_COMMENTS + prodId, method : 'POST'})
                        .then(function (res) {
                            return res.data.response;
                        }, function (ex) {
                            return $q.reject(ex.data.response);
                        });
            } else {
                return $q.reject('ProductService getAllComments args === undefined');
            }
        },
        addNewComment(prodId, nick, body) {
            if (prodId && nick.trim() && body.trim()) {
                return $http({url : URL_ADD_NEW_COMMENT, method : 'POST', data : {productId : prodId, nick : nick, body : body}})
                        .then(null, function (ex) {
                            return $q.reject(ex.data.response);
                        });
            } else {
                return $q.reject('ProductService addNewComment args === undefined');
            }
        },
        selectLastAddedInCategory(curCatId) {
            if (curCatId) {
                return $http({
                    url : URL_LAST_ADDED_PROD_CATEGORY + curCatId + '/' + LIMIT,
                    method : 'POST'
                }). then(function (res) {
                    return res.data.response;
                }, function (ex) {
                    return $q.reject(ex.data.response);
                });
            } else {
                return $q.reject('SharedData currentCategory === undefined');
            }
        },
        selectLastAllAdded() {
                return $http({
                    url : URL_LAST_ALL_ADDED  + '/' + LIMIT,
                    method : 'POST'
                }). then(function (res) {
                    return res.data.response;
                }, function (ex) {
                    return $q.reject(ex.data.response);
                });
            
        },
        selectMainPageProduct : function (prodId) {
            if (prodId) {
                return $http({url : '/getMainPageProduct/' + prodId, method : 'POST'})
                        .then(function (res) {
                            return res.data.response;
                        }, function (ex) {
                            return $q.reject(ex.data.response);
                        });
            } else {
                return $q.reject('ProductService: selectMainPageProduct arg ==='.concat(prodId));
            }
        },
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
    
    var COOKIES_CART_KEY = 'AL_SH_KEY_2019';
    var COOKIES_INFO_KEY = 'REDIRECT_INFO_AL_SH_KEY_2019';
    var COOKIES_LIKE_CHECK = 'LIKE_CHECK_AL_SH_KEY_2019';
    var now = new Date();
    var CART = new CartCookies();
    var cartPagIndex = 0;
    var cartSize = 0;
    var cartPrice = 0;

    function Product(id, name, price, url, exist) { this.id = id; this.name = name; this.price = price; this.url = url; this.exist = exist; this.quantInCart = 1; };

    function CartCookies() { this.uuid; this.products = []; };
    
    function RedirectInfoCookies(cat, pag) { this.category = cat; this.page = pag; };
    
    return {
        updateLikeCookies(prodId) {
            $cookieStore.remove(COOKIES_LIKE_CHECK);
            
            if (prodId) {
                var likeCookies = $cookieStore.get(COOKIES_LIKE_CHECK);
                if (likeCookies) {
                    likeCookies.forEach(function (el) {
                        if (el !== prodId) {
                            likeCookies.push(prodId);  
                        }
                    });
                } else {
                    likeCookies = [];
                    likeCookies[0] = prodId;
                }
                $cookieStore.put(COOKIES_LIKE_CHECK, likeCookies, new Date(now.getFullYear() + 10, now.getMonth(), now.getDate()));
            } else {
                $log.error('updateLikeCookies arg === undefined');
            }
        },
        isLikePosible(prodId) {
//            if (prodId) {
//                var likeCookies = $cookieStore.get(COOKIES_LIKE_CHECK);
//                if (!likeCookies) {
//                    return true;
//                }
//                for (var i =0; i < likeCookies.length; i++) {
//                    if (likeCookies[i] === prodId) {
//                        return false;
//                    }
//                }
//                return true;
//            } else {
//                $log.error('isLikePosible arg === undefined');
//                return false;
//            }
            return true;
        },
        setRedirectInfo(cat, pag) {
            $cookieStore.put(COOKIES_INFO_KEY, new RedirectInfoCookies(cat, pag));
        },
        getRedirectInfo() {
            return $cookieStore.get(COOKIES_INFO_KEY);
        },
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
            $cookieStore.remove(COOKIES_CART_KEY);
        },
        setCookies : function() {
            $cookieStore.put(COOKIES_CART_KEY, CART, new Date(now.getFullYear(), now.getMonth() + 6, now.getDate()));
        },
        checkCookies() {
            var tmp = $cookieStore.get(COOKIES_CART_KEY);

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
            
            CART.products.push(new Product(id, name, price, url, exist));
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

//SLIDER FILTER
indexApp.filter('sliderFilter', function (SharedData, CartService) {
    return function (val, data) {
        if (!val) {
            return val;
        }
        
        if (data === 'all') {
            return val.slice(SharedData.getLastAllAddedIndex(), SharedData.getLastAllAddedIndex() + SharedData.getLastAllAddedOffset());
        } else if (data === 'cart') {
            return val.slice(CartService.getMinCartPagIndex(), CartService.getMinCartPagIndex() +3);
        } else {
            return val.slice(SharedData.getLastAddedIndex(), SharedData.getLastAddedIndex() + SharedData.getLastAddedOffset());
        }
    };
});
