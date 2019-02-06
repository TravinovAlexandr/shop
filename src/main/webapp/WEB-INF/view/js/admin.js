var adminApp = angular.module('adminPage', ['ngRoute']);
var authApp = angular.module('authPage', []);

adminApp.config(function($routeProvider, $locationProvider) {    
    $routeProvider.when('/addNewProduct', {
        templateUrl : '/html/add_product.html',
        controller: 'addProductController'
    }).when('/searchProduct', {
        templateUrl : '/html/search_form.html',
        controller: 'searchFormController'
    }).when('/productTable', {
        templateUrl : '/html/product_table.html',
        controller: 'productTableController'
    }).when('/product/:prId', {
        templateUrl : '/html/admin_product_update.html',
        controller: 'updateProductController'
    }).when('/categories', {
        templateUrl : '/html/admin_categories.html',
        controller: 'categoryController'
    });
    
    $routeProvider.otherwise({redirectTo: '/admin'});
//    $locationProvider.html5Mode({enabled: true, requireBase: false});
//    $locationProvider.html5Mode({enabled: true});
});

//INIT CONTROLLER
adminApp.controller('initController', function (initService) {
    initService.initSharedEvents();
});

//INIT SERVICE
adminApp.factory('initService', function ($timeout) {
    return {
       initSharedEvents : function () {
           //close exception window
           $timeout(function () {
               var adminExWrap = $('.adminExWrapper');
               adminExWrap.find('.quitWrapper').click(function () {
                   adminExWrap.attr('style', 'width: 40%; margin-left: 30%;');
                   adminExWrap.hide();
               });
           }, 1000);
           //close notification
           $timeout(function () {
               $('.notificationMessage').click(function () {
                   $('.notificationWindow').hide(300);
               });
           }, 1000);
           //up size exeption window
           $timeout(function () {
               $('.adminExWrapper').find('.adminExName').dblclick(function () {
                   $('.adminExWrapper').attr('style', 'width: 60%; margin-left: 20%;');
               });
           }, 1000);
       }
    };
});

//CATEGORY CONTROLLER
adminApp.controller('categoryController', function ($scope, categoryService, exception, notification) {
    
    categoryService.getAllCategories().then(function (res) {
        categoryService.setCacheCategories(res);
        $scope.categoryTable = categoryService.getCacheCategories();
    }, function (ex) {
        exception.show(ex);
    });
    
    $scope.addNewCategory = function () {
            var newCategory = $scope.newCategory;
            categoryService.addNewCategory(newCategory.name, newCategory.description).then(function () {
            categoryService.setCacheCategory(newCategory);
            $scope.categoryTable = categoryService.getCacheCategories();    
            notification.showNotification('Категория добавлена.');
        }, function (ex) {
            exception.show(ex);
        });  
    };
    
    $scope.deleteCategory = function () {
        categoryService.deleteCategory($('.updateCategoryId').val())
                .then(function () {
                    categoryService.hideUpdateCategoryModal();
                    $scope.categoryTable = categoryService.getCacheCategories();
                    notification.showNotification('Категория удалена.');
                }, function (ex) {
                    if (typeof (ex) === 'string') {
                      categoryService.hideUpdateCategoryModal();
                      notification.showNotification(ex);
                      return;
                    }
                    exception.show(ex);
                });
    };
    
    $scope.updateCategory = function () {
        var catId = $('.updateCategoryId').val();
        var catName = $('.updateCategoryName').val();
        var catDesc = $('.updateCategoryDescription').val();
        
        categoryService.updateCategory(catId, catName, catDesc)
                .then(function () {
                    categoryService.updateElement(catId, catName, catDesc);
                    $scope.categoryTable = categoryService.getCacheCategories();
                    categoryService.hideUpdateCategoryModal();
                    notification.showNotification('Категория изменена.');
                }, function (ex) {
                    if (typeof (ex) === 'string') {
                        notification.showNotification(ex);
                        return;
                    }
                    exception.show(ex);
                });
    };

    $scope.showUpdateCategoryModal = function (event) {
        categoryService.showUpdateCategoryModal(event);
    };
    
    $scope.hideUpdateCategoryModal = function () {
      categoryService.hideUpdateCategoryModal();
    };
    
    $scope.filterCategories = function (column) {
        $scope.orderedCategories = column;
    };
});

//CATEGORY SERVICE
adminApp.factory('categoryService', function($http, $q, $log) {
    
    var URL_GET_ALL_CATEGORIES = '/getAllCategories';
    var URL_ADD_CATEGORY = '/admin/addCategory';
    var URL_UPDATE_CATEGORY = '/admin/updateCategory/';
    var URL_DELETE_CATEGORY = '/admin/deleteCategory/';
    var categories = [];
    
    return {
        getAllCategories: function () {
            return $http({url: URL_GET_ALL_CATEGORIES, method: 'POST'})
                    .then(function (res) {
                        return res.data.response;
                    }, function (res) {
                        return $q.reject(res.data.response);
                    });
        },
        addNewCategory: function (name, description) {
            if (name && description) {
                return $http({url: URL_ADD_CATEGORY, method: 'POST', data: {name: name, description: description}})
                        .then(null, function (ex) {
                            return $q.reject(ex.data.response);
                        });
            } else {
                return $q.reject('Arguments mast not be null. ' + !name ? 'name ===' + name : '' + !description ? 'description ===' + description : '');
            }
        },
        updateCategory : function(id, name, description) {
            if (id && name && description) {
                return $http({url: URL_UPDATE_CATEGORY, method: 'POST', data: {id : id, name: name, description: description}})
                        .then(null, function (ex) {
                            return $q.reject(ex.data.response);
                        });
            } else {
                return $q.reject('Arguments mast not be null.'.concat((typeof(id) !== 'number') ? ' id === ' + id : '').concat((!name) ? ' name === ' + name : '' ).concat((!description) ? ' description === ' + description : ''));
            }
        }, 
        deleteCategory : function (id) {
            if (id) {
                return $http({url: URL_DELETE_CATEGORY + id, method : 'POST'})
                        .then(function () {
                            for (var i = 0; i < categories.length; i ++) {
                                if (categories[i].id == id) {
                                    categories.splice(i, 1);
                                }
                            }
                        }, function (ex) {
                            return $q.reject(ex.data.response);
                        });
            } else {
                return $q.reject('Attribute id === undefined');
            }
        },
        setCacheCategories : function (cat) {
            if (!cat) {
                $log.warn('setCacheCategory arg categories === ' + cat);
                return;
            }
            categories = cat;
        }, 
        getCacheCategories() {
            return categories;
        },
        setCacheCategory : function (cat) {
            if (!cat) {
                $log.warn('setCacheCategory arg category == ' + cat);
                return;
            }
            categories.push(cat);
        },
        showUpdateCategoryModal : function (event) {
            if (!event) {
                $log.warn('showUpdateCategoryModal api chenged');
                return;
            }
            var modalWindow = $('.updateCategoryModal');
            var categoryRow = $(event.currentTarget);
            modalWindow.find('.updateCategoryId').val(categoryRow.find('.categoryRowId').val());
            modalWindow.find('.updateCategoryName').val(categoryRow.find('.categoryRowName').text());
            modalWindow.find('.updateCategoryDescription').val(categoryRow.find('.categoryRowDescription').text());
            modalWindow.find('.updateCategoryDescription').text(categoryRow.find('.categoryRowDescription').text());
            modalWindow.show(100);
        },
        hideUpdateCategoryModal : function () {
            var modalWindow = $('.updateCategoryModal');
            modalWindow.find('.updateCategoryId').val('');
            modalWindow.find('.updateCategoryName').val('');
            modalWindow.find('.updateCategoryDescription').text('');
            modalWindow.hide(100);
        },
        updateElement : function (id, name, description) {
            if (id && name && description) {
                categories.forEach(function (el) {
                    if (el.id == id) {
                        el.name = name;
                        el.description = description;
                    }
                });
            } else {
                $log.error('Arguments must not be null.'.concat((!id) ? ' id === ' + id : '')
                        .concat((!name) ? ' name === ' + name : '' )
                        .concat((!description) ? ' description === ' + description : ''));
            }
        }
    };
});

//ADD PRODUCT CONTROLLER
adminApp.controller('addProductController', function($scope, addProductService, exception, notification) {    
    
    addProductService.getAllCategories()
            .then(function (res) {
                $scope.categories = res;
            }, function (ex) {
                exception.show(ex);
            });
    
    $scope.addNewProduct = function() { 
        //продумать необходимые данные для товара, написать валидацию
        var form = new FormData();
        form.append('name', $scope.addName);
        form.append('description', $scope.addDesc);
        form.append('price', $scope.addPrice);
        form.append('quantity', $scope.addQuant);
        form.append('image', $scope.addImage);
        
        var categoryIds = [];
        
        $('input:checkbox:checked').each(function() {
            categoryIds.push($(this).val());
        });
        
        form.append('categoryIds', categoryIds);
        
        addProductService.addNewProduct(form)
                .then(function () {
                    notification.showNotification('Товар добавлен.');
                }, function (ex) {
                    exception.show(ex);
                });
    };
});

//ADD PRODUCT SERVICE
adminApp.factory('addProductService', function ($q, $http) {
    
    var URL_GET_ALL_CATEGORIES = '/getAllCategories';
    var URL_ADD_PRODUCT = '/admin/addProduct';
    
    return {
       getAllCategories : function () {
           return $http({method : 'POST', url : URL_GET_ALL_CATEGORIES })
                .then(function(resp) {
                    return resp.data.response;
                }, function(resp) {
                    return $q.reject(resp.data.resonse);
                });
       }, 
       addNewProduct : function (form) {
           if (form) {
            return $http({method : 'POST', url : URL_ADD_PRODUCT, data : form, headers : {'Content-Type': undefined}})
                .then(null, function(ex) {
                    return $q.reject(ex.data.response);
                });   
           } else {
               return $q.regect('addProductService: argument form === undefined');
           }     
       }
    };
});

//SEARCH FORM CONTROLLER
adminApp.controller('searchFormController', function($scope, $timeout, $location, adminData, searchFormService, exception, notification) {
    
    function searchOptionsEvent (event)  {
        var searhInputsDiv = $($(event.target).parent().parent().parent());
        var doubleSearchInput = searhInputsDiv.find('.doubleSearchInput');
        
        searhInputsDiv.find('.searchOptionHidden').val(event.target.value);

        if (event.target.value === 'Between' || event.target.value === '>=<') {
            doubleSearchInput.show();
        } else {
            doubleSearchInput.hide();
        }
    };
    
    searchFormService.getSearchForm()
            .then(function (res) {
                $scope.searchForm = res;
                $timeout(function () {
                    $('.searchSelect').on('change', searchOptionsEvent);        
                }, 1500);
            }, function (ex) {
                exception.show(ex);
            });
    
    $scope.submitSerchForm = function() {
        var searchInputs = $('.searchInputWrapper');
        
        var searchConditionsList = [];
        
        for (var i = 0; i < searchInputs.length; i++) {
            var serchElement = searchFormService.getSearchElement();
            var searchInputEl = $(searchInputs[i]);
            var sInputType = searchInputEl.find('.mainSearchInput').attr('type');
            var sOperator = searchInputEl.find('.searchOptionHidden').val();
            var sMainValue = searchInputEl.find('.mainSearchInput').val();
            serchElement.columnName = searchInputEl.find('.searchNameHidden').val();
            serchElement.type = (sInputType) ? sInputType : 'boolean';
            serchElement.operator = (sInputType) ? sOperator : null;
            serchElement.push((sInputType) ? sMainValue : sOperator);
            var doubleInput = searchInputEl.find('.doubleSearchInput');
            if (doubleInput.is(':visible') && doubleInput.val()) {
                serchElement.push(doubleInput.val());
            }
            searchConditionsList.push(serchElement);
        }

        adminData.setSearchConditions(searchConditionsList);
        
        if (searchConditionsList !== undefined && searchConditionsList !== null) {      
            searchFormService.sendSearchQuery(searchConditionsList)
                    .then(function (res) {
                        $location.path('/productTable');
                        adminData.setProductTable(res);
                    }, function (ex) {
                        if (typeof (ex) === 'string') {
                            notification.showNotification(ex);
                        } else {
                            exception.show(ex);  
                        }
                        adminData.setSearchConditions(null);
                    });
        }   
    };
});

//SEARCH FORM SERVICE
adminApp.factory('searchFormService', function ($log, $http, $q, paginator) {
    
    var URL_SEARCH_FORM = '/admin/searchForm';
    var URL_SEARCH_QUERY = '/admin/searchQuery';
    
    function SearchElement() {
            this.columnName; this.operator; this.type; this.data = [];
            this.push = function(elem) {
                if (elem === undefined || elem === null) {
                    $log.error('SearchElement: push elem === ' + elem);
                    return;  
                }
                this.data.push(elem); 
            }.bind (this);
        };
        
        return {
            getSearchForm : function () {
                return $http({ method: 'POST', url: URL_SEARCH_FORM })
                    .then(function (res) {
                        return res.data.response;
                    }, function (ex) {
                        return $q.reject(ex.data.response);
                    });
            },
            sendSearchQuery : function (conditions) {
                if (conditions) {
                    return $http({ method : 'POST', url : URL_SEARCH_QUERY, 
                        data: {
                            searchQuery : conditions,
                            limit : paginator.getLimit(),
                            offset : 0
                        }
                        }).then(function(res) {
                            return res.data.response;
                        }, function(ex) {
                            return $q.reject(ex.data.response);
                        });  
                } else {
                    return $q.reject(conditions);
                }
            }, 
            getSearchElement : function() {
                return new SearchElement();
            }
    };
});

//PRODUCT TABLE CONTROLLER
adminApp.controller('productTableController', function($scope, $location, $compile, productTableService, adminData, exception, paginator, notification) {
    
    $scope.productTableRow = adminData.getProductTable();
    
    if (!adminData.getProductTable()[0]) {
        $('.prodTabMessage').html('<p>Ни один товар не соответствует заданному условию поиска.</p>');
        adminData.setSearchConditions(null);
    } else {
        var paginationRow = $compile(paginator.getPaginRow(adminData.getProductTable()[0].productsCount))($scope);
        $('.paginationBar').html(paginationRow);
    }
    
    
    $scope.pagClickedStyle = function(num) {
        if (paginator.getClickedElem() === num){
            return paginator.getStyle();
        }
    };
    
    $scope.getAnotherPage = function(num) {
        
        paginator.setClickedElem(num);
        
        productTableService.getAnotherPage(adminData.getSearchConditions(), paginator.getLimit(),
                ($(event.currentTarget)[0].querySelector('p').innerText - 1) * paginator.getLimit())
                        .then(function(res) {
                            $location.path('/productTable');
                            $scope.productTableRow = res;
                        }, function(res) {
                            if (typeof(res) === 'string') {
                                notification.showNotification(res);
                            } else {
                                exception.show(res);
                            }
        });
    };
   
    $scope.selectProduct = function() {
        var pathVariable = this.element.id;
        $location.path('/product/' + pathVariable);
    };
});

//PRODUCT TABLE SERVICE
adminApp.factory('productTableService', function ($http, $q) {
    
    var URL_SEARCH_QUERY = '/admin/searchQuery';
    var URL_SEARCH_PRODUCT = '/admin/product/';

    return {
        getAnotherPage: function (searchQuery, limit, offset) {
            
            if (searchQuery && typeof (limit) === 'number' && typeof (offset) === 'number') {
                return $http({
                    method: 'POST',
                    url: URL_SEARCH_QUERY,
                    data: { searchQuery: searchQuery, limit: limit, offset: offset }
                }).then(function (res) {
                    return res.data.response;
                }, function (ex) {
                    return $q.reject(ex.data.response);
                });
            } else {
                return $q.reject('productTableService.getAnotherPage args: '.concat((searchQuery) ? '' : 'searchQuery == ' + searchQuery)
                        .concat((limit) ? ' ' : ' limit != number').concat((offset) ? ' ' : ' offset != number'));
            }
        },
        selectProduct: function (pathVariable) {
            if (pathVariable) {
                return $http({method: 'POST', url: URL_SEARCH_PRODUCT + pathVariable})
                        .then(function (res) {
                            return res.data.response;
                        }, function (ex) {
                            return $q.reject(ex.data.response);
                        });
           } else {
               return $q.reject('productTableService.selectProduct args: pathVariable === '.concat(pathVariable));
           }
        }
    };
});

//UPDATE PRODUCT CONTROLLER
adminApp.controller('updateProductController', function($scope, $window, $location, $routeParams ,adminData, updateProductService, exception, notification, productTableService) {    
            
    productTableService.selectProduct($routeParams.prId)
            .then(function (res) {
                if (res.product.id === null) {
                    notification.showNotification('Товар не найден');  
                    $location.path('/searchProduct');
                    return;
                }
        
                adminData.setOriginalProduct(res);
                adminData.setBindProduct(res);
                //пишу как массив что-бы использовать ng-repeat что-бы упростить связку ng-model
                $scope.prods = [res];
            }, function (res) {
                if (typeof (res) === 'string') {
                    notification.showNotification(res);
                } else {
                    exception.show(res);
                }
            });
    
    $scope.addNewComment = {};
    
    $scope.addComment = function () {
        var prodId = $('.adminProdId').val();
        var addNewComment = $scope.addNewComment;
        
        if ($window.confirm("Добавить комментарий?")) {
            updateProductService.addComment(addNewComment.nick, addNewComment.body, prodId)
                    .then(function () {
                        updateProductService.updateProductComments(prodId)
                        .then(function (res) {
                            $scope.prods[0].product.comments = res;
                            adminData.setOriginalComments(res);
                            notification.showNotification('Коментарий добавлен.');
                        }, function (ex) {
                            if (typeof (ex) === 'string') {
                                notification.showNotification(ex);
                            } else {
                                exception.show(ex);
                            }
                        });
                    }, function (ex) {
                        if (typeof (ex) === 'string') {
                            notification.showNotification(ex);
                            return;
                        }
                        exception.show(ex);
                    });
        }
    };
    
    $scope.jampToProductTable = function () {
        adminData.deleteProductData();
        $location.path('/productTable');
    };
    
    $scope.getOriginSingleVal = function () {
        var label = $(event.currentTarget).parent().find('label').text();
        var tmpProd= controllerScope.prods[0].product;
            
        switch (label.trim()) {
            case 'Название:': tmpProd.name = adminData.getOriginalProduct().name; break;
            case 'Описание:': tmpProd.description = adminData.getOriginalProduct().description; break;
            case 'Цена:': tmpProd.price = adminData.getOriginalProduct().price; break;
            case 'Оценка:': tmpProd.mark = adminData.getOriginalProduct().mark; break;
            case 'Кол-во:': tmpProd.quantity = adminData.getOriginalProduct().quantity; break;
        }
    };
    
    $scope.getOriginComment = function (event) {
        var commentId = $(event.currentTarget).parent().find('.adminProdCommentId').val();
        var comlist = $scope.prods[0].product.comments;
            
        for (var i = 0; i < comlist.length; i++) {
            if (comlist[i].id == commentId) {   
                for (var j = 0; j < adminData.getOriginalProduct().comments.length; j++) {
                    if (adminData.getOriginalProduct().comments[j].id == commentId) {
                        comlist[i] = adminData.getOriginalComment(j);
                        return;
                    }
                }
            }
        }
    };
    
    $scope.getOriginCategories = function () {
        $scope.prods[0].product.category = adminData.getOriginalCategories();
    };
    
    $scope.deleteCategory = function(event) {
        var catId = $(event.currentTarget).parent().find('input').val();
        var catList = $scope.prods[0].product.category;

        for (var i = 0; i < catList.length; i++) {
            if (catList[i].id == catId) {
                catList.splice(i, 1);
            }
        }
    };
    
    $scope.deleteProduct = function() {
        if ($window.confirm('Подтвердите удаление товара.')) {
            updateProductService.deleteProduct($('.adminProdId').val())
                    .then(function () {
                        adminData.deleteProductData();
                        $location.path('/admin/searchProduct');
                        notification.showNotification('Товар удален.');
                    }, function (ex) {
                        if (typeof (ex) === 'string') {
                            notification.showNotification(ex);
                            return;
                        }
                        exception.show(ex);
                    });
        }
    };
    
    $scope.deleteComment = function(event) {
        if ($window.confirm('Подтвердите удаление комментария.')) {
            var commentId = $(event.currentTarget).parent().find('.adminProdCommentId').val();
            
            updateProductService.deleteComment(commentId)
                    .then(function () {
                        var comList = $scope.prods[0].product.comments;

                        for (var i = 0; i < comList.length; i++) {
                            if (commentId != undefined && comList[i].id == commentId) {
                                comList.splice(i, 1);
                            }
                        }
                        notification.showNotification('Комментарий удален.');
                    }, function (ex) {
                        if (typeof (ex) === 'string') {
                            notification.showNotification(ex);
                            return;
                        }
                        exception.show(ex);
                    });
        }
    };
    
    $scope.getOriginImg = function() {
        $('#adminProdImage').attr('src',  adminData.getOriginalProduct().imgUrl);
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
        var newCatId = $(event.currentTarget).parent().find('input').val();
        var isExist = false;

        for (var i = 0; i < $scope.prods[0].product.category.length; i++) {
            if (newCatId != undefined && $scope.prods[0].product.category[i].id == newCatId) {
                isExist = true;
            }
        }

        if (!isExist) {
            $scope.prods[0].product.category.push(adminData.getCategory(newCatId));
        }
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
        var commentId = $(event.currentTarget).parent().find('.adminProdCommentId').val();
        var comments = $scope.prods[0].product.comments;
        
        updateProductService.updateComment(commentId, comments)
                .then(function (res) {
                    adminData.setOriginalComments(res);
                    notification.showNotification('Комментарий изменен.');
                }, function (ex) {
                    if (typeof (ex) === 'string') {
                        notification.showNotification(ex);
                        return;
                    }
                    exception.show(ex);
                });
    };
    
    $scope.updateProductTableField = function(event) {
        var controll = $(event.currentTarget).parent().find('.updateClickTarget');
        var htmlElName = controll.attr('name');

        if ($window.confirm('Подтвердите изменение поля ' + htmlElName + ".")) {
            var prodId = $('.adminProdId').val();
            var htmlElVal = (htmlElName === 'description') ? controll.text() : controll.val();
            
            updateProductService.updateProductTableField(prodId, htmlElName, htmlElVal)
                    .then(function () {
                        notification.showNotification('Поле ' + htmlElName + ' изменено.');
                    }, function (ex) {
                        if (typeof (ex) === 'string') {
                            notification.showNotification(ex);
                        } else {
                            exception.show(ex);
                        }
                    });
        }
    };
    
    $scope.updateCategories = function () {
        if ($window.confirm("Применить изменения к категориям?")) {
            var newCategories = $scope.prods[0].product.category;
            var oldCategories = adminData.getOriginalCategories();
            var newCategoriesIds = [];
            var oldCategoriesIds = [];

            newCategories.forEach(function (el) {
                newCategoriesIds.push(el.id);
            });

            oldCategories.forEach(function (el) {
                oldCategoriesIds.push(el.id);
            });

            updateProductService.updateCategories($('.adminProdId').val(), oldCategoriesIds, newCategoriesIds)
                    .then(function () {
                        notification.showNotification('Категории изменены');
                    }, function (ex) {
                        if (typeof (ex) === 'string') {
                            notification.showNotification(ex);
                        } else {
                            exception.show(ex);
                        }
                    });
        }
    };
    
    $scope.updateImg = function () {
        var productId = $('.adminProdId').val();
        var imgId = $('.adminProdImageId').val();
        var updateImgfile = $('#adminImgMultipart').prop('files')[0];
        
        updateProductService.updateImage(productId, imgId, updateImgfile)
                .then(function () {
                    notification.showNotification('Изображение изменено.');
                }, function (ex) {
                    if (typeof (ex) === 'string') {
                        notification.showNotification(ex);
                    } else {
                        exception.show(ex);
                    }
                });
    };
});

//UPDATE PRODUCT SERVICE
adminApp.factory('updateProductService', function($http, $q, adminData) {
//    var controllerScope;
    var URL_ADD_COMMENT = '/admin/addComment';
    var URL_DELETE_PRODUCT = '/admin/deleteProduct/';
    var URL_DELETE_COMMENT = '/admin/deleteComment/';
    var URL_UPDATE_COMMENT = '/admin/updateComment';
    var URL_UPDATE_CATEGORIES = '/admin/updateCategories';
    var URL_UPDATE_IMG = '/admin/updateImg';
    var URL_GET_ALL_COMMENTS = '/admin/getAllProductComments/';
    
    return {
        updateImage: function(productId, imageId, updateImgfile) {
            if (productId && imageId && updateImgfile) {
                var form = new FormData();
                form.append('productId', productId);
                form.append('imageId', imageId);
                form.append('image', updateImgfile);
                
                return $http({url: URL_UPDATE_IMG, method: 'POST', data: form, headers: {'Content-Type': undefined}})
                        .then(null, function (ex) {
                            return $q.reject(ex.data.response);
                        });
            } else {
                $q.reject('updateProductService updateImage: некорректные аргументы'
                            .concat((!productId) ? ' productId === ' + productId : '')
                            .concat((!imageId) ? ' imageId === ' + imageId : '')
                            .concat((!updateImgfile) ? ' updateImgfile === ' + updateImgfile : ''));
            }
        },
        updateCategories: function(productId, oldCategoiesId, newCategoriesId) {
            if (productId && oldCategoiesId && newCategoriesId) {
            return $http({url: URL_UPDATE_CATEGORIES, method: 'POST', data: { 
                    productId: productId,
                    oldCategoriesId: oldCategoiesId,
                    newCategoriesId: newCategoriesId }})
                    .then(null, function(ex) {
                        return $q.reject(ex.data.response);
                    });
            } else {
                $q.reject('updateProductService updateComment: некорректные аргументы'
                            .concat((!productId) ? ' productId === ' + productId : '')
                            .concat((!oldCategoiesId) ? ' oldCategoiesId === ' + oldCategoiesId : '')
                            .concat((!newCategoriesId) ? ' newCategoriesId === ' + newCategoriesId : ''));
            }
        },
        updateProductComments: function (prodId) {
            if (prodId) {
                return $http({url: URL_GET_ALL_COMMENTS + prodId, method: 'POST'})
                        .then(function (res) {
                            return res.data.response;
                        }, function(ex) {
                            return $q.reject(ex.data.response);
                        });
            } else {
                return $q.reject('updateProductService updateProductComments: Неккоректный аргумент prodId === '.concat(prodId));
            }
        },
        updateProductTableField: function(prodId, htmlElName, htmlElVal) {
            if (prodId != undefined && htmlElName != undefined && htmlElVal != undefined) {
                    return $http({url: '/admin/updateProductField', method: 'POST', 
                        data: { productId: prodId, columnName: htmlElName, value: htmlElVal }
                    }).then(null, function (res) {
                        return $q.reject(res.data.response);
                    });
            }
        },
        updateComment: function(commentId, comments) {
            var comment = null;
            var originComments = null;
            
            if (commentId && comments) {
                for (var i = 0; i < comments.length; i++) {
                    if (comments[i].id == commentId) {
                        comment = comments[i];
                        break;
                    }
                }

                if (comment != undefined && comment.nick.trim !== '' && comment.body.trim() !== '') {
                    originComments = adminData.getOriginalComments();
                    for (var i = 0; i < comments.length; i++) {
                        if (originComments[i].id == commentId) {
                            if (originComments[i].nick === comment.nick && originComments[i].body === comment.body) {
                                return $q.reject('Данные в коментарии не изменились. Коментарий не будет изменен.');
                                break;
                            } else {
                                return $http({url: URL_UPDATE_COMMENT, method: 'POST', 
                                    data: { id: commentId, nick: comment.nick, body: comment.body }
                                }).then(function () {
                                    return originComments;
                                }, function(res) {
                                    return $q.reject(res.data.response);
                                });
                                break;            
                            }
                        }
                    }
                } else {
                    return $q.reject('updateProductService updateComment: некорректные аргументы'
                            .concat((!commentId) ? ' comentId === ' + commentId : '')
                            .concat((!comments) ? 'comments ===' + comments : ''));
                }
            }
        },
        addComment(comNick, comBody, prodId) {
            if (prodId && comNick && comBody && comNick.trim() !== '' && comBody.trim() !== '') {
                    return $http({url: URL_ADD_COMMENT, method: 'POST', data: { productId: prodId, nick: comNick, body: comBody }
                    }).then(null, function(ex) {
                        return $q.reject(ex.data.response);
                    });
            } else {
                return $q.reject('Коментарий пуст и не будет добавлен.');
            }
        },
        deleteComment: function(commentId) {
            if (commentId) {
                return $http({url: URL_DELETE_COMMENT + commentId , method: 'POST'})
                        .then(null, function(ex) {
                            return $q.defer(ex.data.response);
                        });
            } else {
                return $q.defer('deleteComment args: commentId ==='.concat(commentId));
            }
        },
        deleteProduct: function(prodId) {
            if (prodId) {
                return $http({url: URL_DELETE_PRODUCT + prodId, method: 'POST'})
                        .then(null, function (ex) {
                            return $q.reject(ex.data.response);
                        });
            } else {
                return $q.reject('updateProductService param prodId === '.concat(prodId));
            }
        }
    };
});

//ADMIN NAV CONTROLLER
adminApp.controller('adminNavController', function($scope, adminNavService) {
    $scope.nickVisible = function() {
        adminNavService.setNicVisibility();
    };
    $scope.passwordVisible = function() {
        adminNavService.setPasswordVisibility();
    };
    $scope.productUlVisible = function() {
        adminNavService.setProductUlVisibility();
    };
    $scope.categoryUlVisible = function() {
        adminNavService.setCategoryUlVisibility();
    };
});

//ADMIN NAV SERVICE
adminApp.factory('adminNavService', function () {
    var navEls = $('.hidden');
    
    return {
        setNicVisibility : function() {
            var cadn = $('.changeAdminDataNick');
            if (cadn.is(':visible')) {
                cadn.hide();
            } else {
                cadn.show();
            }
        },
        setPasswordVisibility : function() {
            var cadn = $('.changeAdminDataPassword');
            if (cadn.is(':visible')) {
                cadn.hide();
            } else {
                cadn.show();
            } 
        },
        setProductUlVisibility : function() {
            var navEl0 = $(navEls[0]);
            if (navEl0.is(':visible')) {
                navEl0.hide();
            } else {
                navEl0.show();
            }
        },
        setCategoryUlVisibility : function() {
            var navEl1 = $(navEls[1]);
            if (navEl1.is(':visible')) {
                navEl1.hide();
            } else {
                navEl1.show();
            }  
        }
    };    
});


//PAGINATOR
adminApp.factory('paginator', function() {
    
    var clickedStyle = { 'background-color' : '#CEFF81' };
    var limit = 5;
    var offset = 0;
    var clickedElemtIndex = -1;
    
    return {
        getPaginRow : function(elementNum) {
            var pagElement = '';
            var mod = elementNum % limit;
            for (var i = 1; i <= elementNum % limit + ((mod === 0) ? 0 : 1); i++) {
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

//NOTIFICATION
adminApp.factory('notification', function ($timeout, $log) {
    return {
        showNotification : function (message) {
            if (!message) {
                $log.warn('NotificationService : '.concat('message = ') + message);
                return;
            }
            
            $('.notificationMessage p').text(message);
            $('.notificationWindow').show(300);
            
            $timeout(function () {
                $('.notificationWindow').hide(300);
            }, 10000);
        }
    };
});

//EXCEPTION
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

//ADMIN DATA
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