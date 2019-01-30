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
                    console.log(resp.data.response.product.category);
                    adminData.setOriginalProduct(resp.data.response);
                    adminData.setBindProduct(resp.data.response);
                }, function(resp) {
                    exception.show(resp.data.response);
                });
    };
});

adminApp.controller('adminProductController', function($scope, $window, adminData, productUpdate, exception) {
    //пишу как массив что-бы использовать ng-repeat что-бы упростить связку ng-model
    $scope.prods = [adminData.getBindProduct()];
    $scope.addNewComment = {};
    
    productUpdate.initControllerScope($scope);
    
    $scope.addComment = function() {
        var prodId = $('.adminProdId').val();
        var addNewComment = $scope.addNewComment;
        var successesPromice = productUpdate.addComment(addNewComment.nick, addNewComment.body, prodId);  
        successesPromice.then(function(result) {
            if (result === 1) {
                productUpdate.updateProductComments(prodId);    
            }
        });
    };
    
    $scope.jampToProductTable = function() {
        productUpdate.jampToProductTable();
    };

    $scope.getOriginSingleVal = function() {
        productUpdate.getOriginSingleVal();
    };
    
    $scope.getOriginComment = function(event) {
        productUpdate.getOriginComment(event);
    };
    
    $scope.getOriginCategories = function() {
        productUpdate.getOriginCategories();
    };
    
    $scope.deleteCategory = function(event) {
        productUpdate.deleteCategory(event);
    };
    
    $scope.deleteProduct = function() {
        if ($window.confirm('Подтвердите удаление товара.')) {
            productUpdate.deleteProduct();
        }
    };
    
    $scope.deleteComment = function(event) {
        productUpdate.deleteComment(event);
    };
    
    $scope.getOriginImg = function() {
        productUpdate.getOriginImg();
    };    
    
    $scope.showCategories = function() {
        var addCatLink = $('.adminProdUpAllCategoriesWrapper');
        if (addCatLink.is(':visible')) {
            addCatLink.hide();
        } else {
            addCatLink.show();
        }
    };
    
    $scope.addCategory = function(event) {
        productUpdate.addCategory(event);
    };
    
    $scope.showComments = function() {
        var hidenCat = $('.adminProdUpCommentBlock');
        if (hidenCat.is(':visible')) {
            hidenCat.hide();
        } else {
            hidenCat.show();
        }
    };
    
    $scope.updateComment = function(event) {
        productUpdate.updateComment(event);
    };
    
    $scope.updateProductTableField = function(event) {
        productUpdate.updateProductTableField(event);
    };
    
    $scope.updateCategories = function() {
       if ($window.confirm("Применить изменения к категориям?")) {
            var newCategories =  $scope.prods[0].product.category;
            var oldCategories =  adminData.getOriginalCategories();
            var newCategoriesIds = [];
            var oldCategoriesIds = [];
            
            newCategories.forEach(function(el) {
                newCategoriesIds.push(el.id);
            });
            
            oldCategories.forEach(function(el) {
                oldCategoriesIds.push(el.id);
            });
            
           productUpdate.updateCategories($('.adminProdId').val(), oldCategoriesIds, newCategoriesIds)
                   .then(
                   function(val) {
                       window.alert(val);
                   }, 
                   function(val) {
                        if (typeof(val) === 'string') {
                            window.alert(val);
                        } else {
                            exception.show(val);
                        }
                    });
       }
    };
});

///SERVICES
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
            } catch (ex) {
                $log.error('Exception: fields are not init.');
                return;
            }
            exceptionModal.show();
        }
    };
});

adminApp.factory('productUpdate', function($window, $http, $location, $timeout, $log, $q, adminData, exception) {
    var controllerScope;
    
    return {
        
        initControllerScope: function(scope) {
           controllerScope = scope; 
        },
        
        updateCategories: function(productId, oldCategoryId, newCategoryId) {
            var defer = $q.defer();
            
            if (oldCategoryId === null || newCategoryId === null) {
                defer.reject("Категории не инициализированны.");
                return defer.promise;
            }
            
            $http({url: '/admin/updateCategories', method: 'POST', data: { 
                    productId: productId,
                    oldCategoriesId: oldCategoryId,
                    newCategoriesId: newCategoryId}})
                    .then(
                    function() {
                        defer.resolve('Категории изменены.');
                    },
                    function(resp) {
                        defer.reject(resp.data.response);
                    });
                    
            return defer.promise;        
        },
        
        updateProductComments: function (prodId) {
            $http({url: '/admin/getAllProductComments/' + prodId, method: 'POST'})
                    .then(function(resp) {
                        controllerScope.prods[0].product.comments = resp.data.response;
                        adminData.setOriginalComments(resp.data.response);
            }, function(resp) {
                exception.show(resp.data.response);
            });
        },
        
        updateProductTableField: function(event) {
            var prodId = $('.adminProdId').val();
            var controll = $(event.currentTarget).parent().find('.updateClickTarget');
            var htmlElName = controll.attr('name');
            var htmlElVal = (htmlElName === 'description') ? controll.text() : controll.val();
            
            if (prodId !== null || htmlElName !== null || htmlElVal !== null) {
                if ($window.confirm("Подтвердите изменение поля " + htmlElName + ".")) {
                    $http({url: '/admin/updateProductField', method: 'POST', 
                        data: { productId: prodId, columnName: htmlElName, value: htmlElVal }
                    }).then(function() {}, 
                    function(resp) {
                        exception.show(resp.data.response);
                    });
                } else {
                    return;
                }
            }
        },
        
        updateComment: function(event) {
            var commentId = $(event.currentTarget).parent().find('.adminProdCommentId').val();
            var comment = null;
            var originComments = null;
            var tmpComs = controllerScope.prods[0].product.comments;
            
            for (var i = 0; i < tmpComs.length; i++) {
                if (Number(tmpComs[i].id) === Number(commentId)) {
                    comment = tmpComs[i];
                    break;
                }
            }
            
            if (comment !== null && comment.body.trim() !== '' && comment.body.trim() !== '') {
                originComments = adminData.getOriginalComments();
                for (var i = 0; i < tmpComs.length; i++) {
                    if (Number(originComments[i].id) === Number(commentId)) {
                        if (originComments[i].nick === comment.nick && originComments[i].body === comment.body) {
                            $window.alert("Данные в коментарии не изменились. Коментарий не будет изменен.");
                            break;
                        } else {
                            $http({url: '/admin/updateComment', method: 'POST', 
                                data: { id: commentId, nick: comment.nick, body: comment.body }
                            }).then(function() {
                                adminData.setOriginalComments(tmpComs);
                            }, function(resp) {
                                exception.show(resp.data.response);
                            });
                            break;            
                        }
                    }
                }
            } else {
                $window.alert('Коментарий пуст и не будет изменен.');
            }
        },
        
        addComment(comNick, comBody, prodId) {
            var defer = $q.defer();
            if (comNick !== null && comBody !== null && comNick.trim() !== '' && comBody.trim() !== '') {
                if ($window.confirm("Добавить комментарий?")) {
                    $http({url: '/admin/addComment', method: 'POST', 
                        data: { productId: prodId, nick: controllerScope.addNewComment.nick, body: controllerScope.addNewComment.body }
                    }).then(function() {
                        defer.resolve(1);
                    }, function(resp) {
                        exception.show(resp.data.response);
                        defer.resolve(0);
                    });
                }
            } else {
                $window.alert('Коментарий пуст и не будет добавлен.');    
            }
            return defer.promise;
        },
        
        addCategory: function(event) {
            var newCatId = $(event.currentTarget).parent().find('input').val();
            var isExist = false;
          
            for (var i = 0; i < controllerScope.prods[0].product.category.length; i++) {
                if (Number(controllerScope.prods[0].product.category[i].id) === Number(newCatId)) {
                    isExist = true;
                }
            }
            
            if (!isExist) {
                controllerScope.prods[0].product.category.push(adminData.getCategory(newCatId));
            }  
        },
        
        getOriginImg: function() {
            $('#adminProdImage').attr('src',  adminData.getOriginalProduct().imgUrl);
        },
        
        deleteComment: function(event) {
            var commentId = $(event.currentTarget).parent().find('.adminProdCommentId').val();

            if ($window.confirm('Подтвердите удаление комментария.')) {
                $http({url: '/admin/deleteComment/' + commentId , method: 'POST'})
                        .then(function() {
                            var comList = controllerScope.prods[0].product.comments;
                    
                            for (var i = 0; i < comList.length; i++) {
                                if (Number(comList[i].id) === Number(commentId)) {
                                    comList.splice(i, 1);
                                }
                            }
                }, function(resp) {
                    exception.show(resp.data.response);
                });
            }
        },
        
        deleteProduct: function() {
                $http({url: '/admin/deleteProduct/' + $('.adminProdId').val(), method: 'POST'})
                        .then(function() {
                        adminData.deleteProductData();
                        $location.path('/searchProduct');
                }, function(resp) {
                    exception.show(resp);
                });
        },
        
        deleteCategory: function(event) {
            var catId = Number($(event.currentTarget).parent().find('input').val());
            var catList = controllerScope.prods[0].product.category;
            
            for (var i = 0; i < catList.length; i++) {
                if (Number(catList[i].id) === catId) {
                    catList.splice(i, 1);
                }
            }
        },
        
        jampToProductTable: function() {
            adminData.deleteProductData();
            $location.path('/productTable');
        },

        getOriginSingleVal: function() {
            var label = $(event.currentTarget).parent().find('label').text();
            var tmpProd= controllerScope.prods[0].product;
            
            switch (label.trim()) {
                case 'Название:': tmpProd.name = adminData.getOriginalProduct().name; break;
                case 'Описание:': tmpProd.description = adminData.getOriginalProduct().description; break;
                case 'Цена:': tmpProd.price = adminData.getOriginalProduct().price; break;
                case 'Оценка:': tmpProd.mark = adminData.getOriginalProduct().mark; break;
                case 'Кол-во:': tmpProd.quantity = adminData.getOriginalProduct().quantity; break;
            }
        },
        
        getOriginComment: function(event) {
            var commentId = $(event.currentTarget).parent().find('.adminProdCommentId').val();
            var comlist = controllerScope.prods[0].product.comments;
            
             for (var i = 0; i < comlist.length; i++) {
                 if (Number(comlist[i].id) === Number(commentId)) {   
                     for (var j = 0; j < adminData.getOriginalProduct().comments.length; j++) {
                         if (Number(adminData.getOriginalProduct().comments[j].id) === Number(commentId)) {
                            comlist[i] = adminData.getOriginalComment(j);
                            return;
                         }
                     }
                 }
             }
        },
    
        getOriginCategories: function() {
            controllerScope.prods[0].product.category = adminData.getOriginalCategories();
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
           var tmpComList = new Array();
           for (var i = 0; i < cats.length; i++) {
               tmpComList.push(new Category(cats[i].id, cats[i].name));
           }
           return tmpComList;    
        },
        
        getOriginalComment: function(index) {
           var com = originalProduct.comments[index];
           return new Comment(com.id, com.nick, com.body);
        },
        
        getOriginalComments: function() {
           var comments = originalProduct.comments;
           var tmpComList = new Array();
           for (var i = 0; i < comments.length; i++) {
               tmpComList.push(new Comment(comments[i].id, comments[i].nick,  comments[i].body));
           }
           return tmpComList;    
        },
        
        setOriginalComments: function(data) {
            originalProduct.comments = new Array();
            
            for (var i = 0; i < data.length; i++) {
                var comment = new Comment(data[i].id, data[i].nick, data[i].body);
                originalProduct.comments.push(comment);
            }
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