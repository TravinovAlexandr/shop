adminApp = angular.module('adminPage', ['ngRoute', 'ngFileUpload']);
authApp = angular.module('authPage', []);

adminApp.config(function($routeProvider, $locationProvider) {    
    $routeProvider.when('/addNewProduct', {
        templateUrl : '/html/add_product.html',
        controller: 'AddProductController'
    }).when('/searchProduct', {
        templateUrl : '/html/search_form.html',
        controller: 'SearchFormController'
    }).when('/productTable', {
        templateUrl : '/html/product_table.html',
        controller: 'ProductTableController'
    }).when('/product/:prId', {
        templateUrl : '/html/admin_product_update.html',
        controller: 'UpdateProductController'
    }).when('/categories', {
        templateUrl : '/html/admin_categories.html',
        controller: 'CategoryController'
    }).when('/tags', { 
        templateUrl : '/html/admin_tags.html',
        controller : 'TagController'
    });
    
    $routeProvider.otherwise({redirectTo: '/admin'});
//    $locationProvider.html5Mode({enabled: true, requireBase: false});
//    $locationProvider.html5Mode({enabled: true});
});

adminApp.factory('Validator', function () {
    return {
        validateNull(... args) {
            if (!args) {
                return false;
            }
            
            for (var i = 0; i < args.length; i++) {
                if (args[0] === undefined && args[0] === null) {
                    return false;
                }
            }
            return true;
        }
    };
});

//INIT CONTROLLER
adminApp.controller('initController', function (initService) {
    initService.initSharedEvents();
});

adminApp.controller('TagController', function ($scope, $timeout, $compile, TagService, Paginator, Validator, Notification, Exception) {
    
    function clearPaginationBar() {
        var liElems = angular.element('.tagAlphabet ul li');
        for (var i = 0; i < liElems.length; i++) {
            liElems[i].style.backgroundColor = '';
        }
    };
    
    $scope.chooseLetter = function (event) {
        $scope.letter = event.target.innerText;
        clearPaginationBar();
        angular.element(event.target).css('background-color', Paginator.getClickedColor());
    };
    
    $scope.setVisible = function (className) {
//        var elem;
//        if (className === 'russianAlphabet') {
//            elem = angular.element('.russianAlphabet');
//        } else if (className === 'englishAlphabet') {
//            elem = angular.element('.englishAlphabet');
//        } else if (className === 'addTag') {
//            elem = angular.element('.addTag');
//        }
        var elem = angular.element('.' + className);

        if (elem.css('display') === 'block') {
            elem.css('display', 'none');
        } else {
            elem.css('display', 'block');
        }
    };
    
    $scope.orderTagTable = function (col) {
        $scope.tagOrder = col;
    };
    
    $scope.updateTag = function (id, name) {
        if (!id || !name || name.length === 0) {
            return;
        }
        
        $scope.updateTagObj = {};
        $scope.updateTagObj.id = id;
        $scope.updateTagObj.name = name;
        angular.element('.updateTag').css('display', 'block');
    };
    
    $scope.updateTagExite = function () {
        $scope.updateTagObj = null;
         angular.element('.updateTag').css('display', 'none');
    };
    
    $scope.submitUpdateTag = function () {
        if (!Validator.validateNull($scope.updateTagObj, $scope.updateTagObj.id, $scope.updateTagObj.name)) {
            Notification.showNotification('Данные невалидны');
            return;
        }
        
        var tagObj = $scope.updateTagObj;
        
        TagService.updateTag(tagObj.id, tagObj.name)
                .then(function () {
                    Notification.showNotification('Тег изменен');
                    var tags = $scope.tags;
                    for (var i = 0; i < tags.length; i++) {
                        if (tags[i].id === tagObj.id) {
                            tags[i].name = tagObj.name;
                        }
                    }
                }, function (ex) {
                    if (typeof (ex) === 'string') {
                        Notification.showNotification(ex);
                        return;
                    }
                    Exception.show(ex);
                });
    };
    
    $scope.submitAddTag = function () {
        if (!$scope.addTagName || $scope.addTagName.trim() === '') {
            return;
        }
        
       TagService.addTag($scope.addTagName)
               .then(function (res) {
                   $scope.lastAddedTag = { id: res };
                   Notification.showNotification('Тег добавлен');
                }, function (ex) {
                    if (typeof (ex) === 'string') {
                        Notification.showNotification(ex);
                        return;
                    }
                    Exception.show(ex);
                });
                
       $timeout(function () {
           if (!$scope.lastAddedTag) {
               $scope.lastAddedTag = {};
           }
           
           $scope.lastAddedTag.name = new String($scope.addTagName);
           $scope.addTagName = null;
       }, 100);
    };
    
    $scope.deleteAllOrphanedTags = function() {
        if (!$scope.tags || $scope.tags.length === 0) {
            Notification.showNotification('Ни одного тега не добавлено');
            return;
        }
        
        TagService.deleteAllOrphanedTags()
                .then(function () {
                    Notification.showNotification('Все неиспользуемые теги удалены');
                    $scope.tags = null;
                }, function (ex) {
                    if (typeof (ex) === 'string') {
                        Notification.showNotification(ex);
                        return;
                    }
                    Exception.show(ex);
                });
    };
    
    $scope.deleteTag = function (tagId) {
        if (!tagId) {
            return;
        }

        TagService.deleteTag(tagId)
                .then(function () {
                    Notification.showNotification('Тег удален');
                    for (var i = 0; i < $scope.tags.length; i++) {
                        if ($scope.tags[i].id === tagId) {
                            $scope.tags.splice(i, 1);
                            return;
                        }
                    }
                }, function (ex) {
                    if (typeof (ex) === 'string') {
                        Notification.showNotification(ex);
                        return;
                    }
                    Exception.show(ex);
                });
    };

    $scope.getAnotherPage = function(num) {
        if (!num) {
            Notification.showNotification('getAnotherPage args === undefined');
            return;
        }

        if ($scope.letter) {
            $scope.selectByAlphabet(num);  
        } else {
            clearPaginationBar();
            $scope.getOrphanedTags(num);  
        }
    };
    
    $scope.selectByAlphabet = function (offset) {
        if (!$scope.letter) {
            Notification.showNotification('Буква алфавита не определена');
            return;
        }
        
        offset = (offset) ? offset : 1;
        var limit = Paginator.getTagTableLimit();
            
        TagService.selectTags($scope.letter, limit, (offset - 1) * limit)
                .then(function (res) {
                    if (!res || !res.tags || res.tags.length === 0) {
                        $scope.tags = null;
                        Notification.showNotification('Ни одного тега не найдено');
                        return;
                    }

                    $scope.tags = res.tags;
                    angular.element('.paginationBar').html($compile(Paginator.getPaginRow(res.count, limit))($scope));
                }, function (ex) {
                    if (typeof (ex) === 'string') {
                        Notification.showNotification(ex);
                        return;
                    }
                    Exception.show(ex);
                });
    };
    
    $scope.getOrphanedTags = function (offset) {
       offset = (!offset) ? 1 : offset ;
       var limit = Paginator.getTagTableLimit();
       
       TagService.getOrphanedTags(limit, (offset - 1) * limit)
                .then(function (res) {
                    if (!res || !res.tags || res.tags.length === 0) {
                        $scope.tags = null;
                        Notification.showNotification('Некорректный ответ сервера');
                        return;
                    }
                    
                    $scope.letter = null;
                    $scope.tags = res.tags;
                    clearPaginationBar();
                    angular.element('.paginationBar').html($compile(Paginator.getPaginRow(res.count, limit))($scope));
                }, function (ex) {
                    if (typeof (ex) === 'string') {
                        Notification.showNotification(ex);
                        return;
                    }
                    Exception.show(ex); 
                });
    };
});

adminApp.factory('TagService', function ($http, $q, Validator) {
    var URL_ADD_TAG = '/admin/addTagGetId/';
    var URL_DELETE_TAG = '/admin/deleteTag/';
    var URL_GET_ORPHANED_TAGS = '/admin/getOrphanedTags/';
    var URL_SELECT_TAGS = '/admin/getTagsLike/';
    var URL_UPDATE_TAG = '/admin/updateTag/';
    var URL_DELETE_ORPHANED_TAGS = 'admin/removeAllOrphanedTags';
    
    return {
        updateTag(id, name) {
            if (!Validator.validateNull(id, name)) {
                return $q.reject('TagService.updateTag null args.');
            }

            return $http({ url: URL_UPDATE_TAG + id + '/' + name, method: 'POST' })
                    .then(null, function (ex) {
                        return $q.reject(ex.data.response);
                    });
        },
        selectTags(word, limit, offset) {
            if (!Validator.validateNull(limit, offset, word)) {
                return $q.reject('TagService.selectTags null args.');
            }

            return $http({ url : URL_SELECT_TAGS + word + '/' + limit + '/' + offset, method : 'POST'})
                    .then(function (res) {
                        return res.data.response;
                    }, function (ex) {
                        return $q.reject(ex.data.response);
                    });
        },
        addTag(name) {
            if (Validator.validateNull(name)) {
                return $http({url: URL_ADD_TAG + name, method: 'POST'})
                        .then(function (res) {
                            return res.data.response;
                        }, function (ex) {
                            return $q.reject(ex.data.response);
                        });
            } else {
                return $q.reject('TagService.addTags args === undefined');
            }
        },
        deleteTag(tagId) {
            if (Validator.validateNull(tagId)) {
                return $http({url: URL_DELETE_TAG + tagId, method: 'POST'})
                        .then(null, function (ex) {
                            return $q.reject(ex.data.response);
                        });
            } else {
                return $q.reject('TagService.deleteTags args === undefined');
            }
       },
       getOrphanedTags(limit, offset) {
           if (Validator.validateNull(limit, offset)) {
                return $http({ url: URL_GET_ORPHANED_TAGS + limit + '/' + offset, method: 'POST' })
                        .then(function (res) {
                            return res.data.response;
                        }, function (ex) {
                            return $q.reject(ex.data.response);
                        });    
           } else {
               return $q.reject('TagService.getOrphanedTags args === undefined');
           }
       },
        deleteAllOrphanedTags() {
            return $http({url: URL_DELETE_ORPHANED_TAGS, method: 'POST'})
                    .then(null, function (ex) {
                        return $q.reject(ex.data.response);

                    });
        }
    };
});

//INIT SERVICE
adminApp.factory('initService', function ($timeout) {
    return {
       initSharedEvents : function () {
           //close Exception window
           $timeout(function () {
               var adminExWrap = $('.adminExWrapper');
               adminExWrap.find('.quitWrapper').click(function () {
                   adminExWrap.attr('style', 'width: 40%; margin-left: 30%;');
                   adminExWrap.hide();
               });
           }, 1000);
           $timeout(function () {
               $('.NotificationMessage').click(function () {
                   $('.NotificationWindow').hide(300);
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
adminApp.controller('CategoryController', function ($scope, $log, categoryService, Exception, Notification) {
    //nead init here or inputs wont be init without click
    $scope.updateCat = {};
    
    categoryService.getAllCategories().then(function (res) {
        categoryService.setCacheCategories(res);
        $scope.categoryTable = categoryService.getCacheCategories();
    }, function (ex) {
        Exception.show(ex);
    });
    
    $scope.showAddCat = function () {
       var addCatDiv = $('.addCat');
       if (addCatDiv.css('display') === 'block') {
           addCatDiv.css('display', 'none');
       } else {
           addCatDiv.css('display', 'block');
       }
    };
    
    $scope.addCategory = function (addCatForm) {
        if (!addCatForm || !addCatForm.$valid) {
            Notification.showNotification('Форма заполнена некорректно.');
            return;
        }

        var addCat = $scope.addCat;

        if (!addCat || !addCat.name || !addCat.description || addCat.pid === undefined) {
            Notification.showNotification('!addCat || !addCat.name || !addCat.description || !addCat.pid');
            return;
        }

        categoryService.addNewCategory(addCat.name, addCat.description, addCat.pid)
                .then(function (res) {
                    addCat.id =  res;
                    categoryService.pushCacheCategory(addCat);
                    $scope.categoryTable = categoryService.getCacheCategories();
                    Notification.showNotification('Категория добавлена.');
                }, function (ex) {
                    Exception.show(ex);
                });
    };
    
    $scope.deleteCategory = function (id) {
        if (!id) {
            Notification.showNotification('deleteCategory args === undefined');
        }
        
        categoryService.deleteCategory(id)
                .then(function () {
                    $scope.hideUpdateCategoryModal();
                    $scope.categoryTable = categoryService.getCacheCategories();
                    Notification.showNotification('Категория удалена.');
                }, function (ex) {
                    if (typeof (ex) === 'string') {
                      this.hideUpdateCategoryModal();
                      return;
                    }
                    
                    Exception.show(ex);
                });
    };
    
    $scope.updateCategory = function (id, catUpForm) {
        if (!catUpForm || !catUpForm.$valid) {
            Notification.showNotification('Форма заполнена некорректно.');
            return;
        }

        
        if (id) {
            var upCat = $scope.updateCat;
            if (upCat && upCat.pid === undefined || upCat.name === undefined || upCat.description === undefined) {
                Notification.showNotification('Форма заполнена некорректно.');
                return;
            }
            
            if (!upCat || upCat.pid === undefined || !upCat.name || !upCat.description) {
                Notification.showNotification('updateCat === undefined || updateCat.pid === undefined || updateCat.name || updateCat.description');
                return;
            }
            
            categoryService.updateCategory(id, upCat.pid, upCat.name, upCat.description)
                    .then(function () {
                        categoryService.updateElement(id, upCat.pid, upCat.name, upCat.description);
                        $scope.categoryTable = categoryService.getCacheCategories();
                        $scope.hideUpdateCategoryModal();
                        Notification.showNotification('Категория изменена.');
                    }, function (ex) {
                        if (typeof (ex) === 'string') {
                            Notification.showNotification(ex);
                            return;
                        }
                        Exception.show(ex);
                    });
            } else {
                $log.error('updateCategory arg undefined');
            }
    };
    
    $scope.lightOnTree = function (catId, catPid) {
        var rootElem = angular.element('.catTableBody ' + '[name=' + catId + ']');
        if (rootElem[0].cells[3].innerText == 0) {
            rootElem.css('background-color', 'red');
        } else {
            rootElem.css('background-color', 'yellow');
        }
        
        var tableBodyRows = angular.element('.catTableBody tr');
        for (var i = 0; i < tableBodyRows.length; i++) {
            var elem = angular.element(tableBodyRows[i]);
            if (elem[0].cells[3].innerText == catId) {
                elem.css('background-color', 'white');
            }
            
            if (elem[0].cells[2].innerText == catPid) {
                elem.css('background-color', 'red');
            }
        }
    };
    
    $scope.lightOffTree = function () {
        var tableBodyRows = angular.element('.catTableBody tr');
        for (var i = 0; i < tableBodyRows.length; i++) {
            angular.element(tableBodyRows[i]).css('background-color', '');
        }
    };

    $scope.showUpdateCategoryModal = function (id, pid, name, description) {
        if (!id || pid === undefined || !name || !description) {
            $log.error('showUpdateCategoryModal args === undefined');
        }
        var upcategory = [ { id : id, pid : pid, name : name, description : description } ];
        $scope.upcategory = upcategory;
    };
    
    $scope.hideUpdateCategoryModal = function () {
        $scope.upcategory = null;
    };
    
    $scope.filterCategories = function (column) {
        $scope.orderedCategories = column;
    };
});

//CATEGORY SERVICE
adminApp.factory('categoryService', function($http, $q, $log) {
    
    var URL_GET_ALL_CATEGORIES = '/getAllCategoriesList';
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
        addNewCategory: function (name, description, pid) {
            if (name && description && pid !== undefined) {
                return $http({ url: URL_ADD_CATEGORY, method: 'POST', data: { name : name, description : description, pid : pid } })
                        .then(function (res) {
                            if (res.data.response === null) {
                                return $q.reject("Категория не добавлена.");
                            }
                    
                            return res.data.response;
                        }, function (ex) {
                            return $q.reject(ex.data.response);
                        });
            } else {
                return $q.reject('Arguments mast not be null. ' + !name ? 'name ===' + name : '' + !description ? 'description ===' + description : '');
            }
        },
        updateCategory : function(id, pid, name, description) {
            if (id && pid !== undefined && name && description) {
                return $http( { url : URL_UPDATE_CATEGORY, method : 'POST', data: { id : id, pid: pid, name: name, description: description }})
                        .then(null, function (ex) {
                            return $q.reject(ex.data.response);
                        });
            } else {
                return $q.reject('Arguments mast not be null.'.concat((typeof(id) !== 'number') ? ' id === ' + id : '').concat((!name) ? ' name === ' + name : '' ).concat((!description) ? ' description === ' + description : ''));
            }
        }, 
        deleteCategory : function (id) {
            if (id) {
                return $http({ url: URL_DELETE_CATEGORY + id, method : 'POST' })
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
        pushCacheCategory : function (cat) {
            if (!cat) {
                $log.warn('setCacheCategory arg category == ' + cat);
                return;
            }
            categories.push(cat);
        },
        updateElement : function (id, pid, name, description) {
            if (id && pid !== undefined && name && description) {
                categories.forEach(function (el) {
                    if (el.id == id) {
                        el.pid = pid;
                        el.name = name;
                        el.description = description;
                    }
                });
            } else {
                $log.error('Arguments must not be null.'.concat((!id) ? ' id === ' + id : '').concat((!name) ? ' name === ' + name : '' ).concat((!description) ? ' description === ' + description : ''));
            }
        }
    };
});


adminApp.factory('Counter', function () {
    var count = 0;

    return {
        getNext(clear) {
            if (typeof (clear) === 'boolean' && clear) {
                return count = 0;
            }
            return ++count;
        },
        getCurrent() {
            return count;
        }
    };
}); 

//ADD PRODUCT CONTROLLER
adminApp.controller('AddProductController', function($scope, $timeout, $compile, addProductService, TagService, Exception, Notification, Counter, Paginator, Upload) {    
    
    addProductService.getAllCategories()
            .then(function (res) {
                $scope.categories = res;
            }, function (ex) {
                Exception.show(ex);
            });
            
    $scope.validateName = function(addName) {
        if (!addName) {
            $scope.isNameExist = true;
            return;
        }
        
        addProductService.isNameExist(addName) 
                .then(function (res) {
                    if (res === true) {
                        $scope.isNameExist = false;
                        return;
                    }
                    $scope.isNameExist = true;
                }, function (ex) {
                    Exception.show(ex);
                });
    };
            
    $scope.showAddCategories = function () {
        var catsBlock = angular.element('.categoryCheckBoxBlock');
        if (catsBlock.css('display') === 'none') {
            catsBlock.css('display', 'block');
        } else {
            catsBlock.css('display', 'none');
        }
    };
    
    $scope.selectTags = function (word, offset) {
        if (!word || word.trim() === '') {
            $scope.tags = null;    
            return;
        }
        
        offset = (!offset) ? 1 : offset;
        
        TagService.selectTags(word, Paginator.getAddProductTagsLimit(), (offset - 1) * Paginator.getAddProductTagsLimit())
                .then(function (res) {
                    if (!res || !res.tags || res.tags.length === 0) {
                        Notification.showNotification('Теги не найдены');
                        $scope.tags = null;
                        return;
                    }
        
                    $scope.tags = res.tags; 
                    angular.element('.paginationBar').html($compile(Paginator.getPaginRow(res.count, Paginator.getAddProductTagsLimit()))($scope));
                }, function (ex) {
                    if (typeof(ex) === 'string') {
                        Notification.showNotification(ex);
                        return;
                    }
                    Exception.show(ex);
                });
    };
    
    $scope.getAnotherPage = function (num) {
        if (!$scope.selectTagWord || !num) {
            return;
        }
        
        this.selectTags($scope.selectTagWord, num);
    };
    
    $scope.showModalProdTags = function () {
        var modalTags = angular.element('.modalAddProdTags');
        if (modalTags.css('display') === 'none') {
            modalTags.css('display', 'block');
        } else {
            modalTags.css('display', 'none');
        }
    };
    
    $scope.uploadFiles = function (file) {
        if (!$scope.imgFiles) {
            $scope.imgFiles = [];
            $scope.additionImgs = [];
        }

        if (file && file.length) {
            $scope.imgFiles.push({ index: Counter.getNext(), img: file[0] });
            var addImgReader = new FileReader();
            addImgReader.onload = function () {
                if (addImgReader.result) {
                    $scope.additionImgs.push({index: Counter.getCurrent(), blob: addImgReader.result});
                }
            };
            addImgReader.readAsDataURL($scope.imgFiles[$scope.imgFiles.length - 1].img);
        }
            
            $timeout(function () {
                $('.button').trigger('blur');
            }, 200);
    };
    
    $scope.deleteAdditionImgs = function(index) {
        for (var i = 0; i < $scope.imgFiles.length; i++) {
            if (index === $scope.imgFiles[i].index) {
                $scope.imgFiles.splice(i, 1);
            }
            
            if (index === $scope.additionImgs[i].index) {
                $scope.additionImgs.splice(i, 1);
            }
        }
    };
    
    $scope.addNewProduct = function (addProdForm) {
        if (!addProdForm || addProdForm.$invalid) {
            Notification.showNotification('Форма невалидна.');
            return;
        }
        
        if (!$scope.isNameExist) {
            Notification.showNotification('Название товара не уникально.');
            return;
        }
        
        function fillFormData(formData, map) {
            var keys = Object.keys(map);
            for (var i = 0; i < keys.length; i++) {
                if (map[keys[i]] && typeof(map[keys[i]]) === 'number' && map[keys[i]] !== 0) {
                    formData.append(keys[i], map[keys[i]]);
                } else if (map[keys[i]]) {
                    formData.append(keys[i], map[keys[i]]);
                }
            }
        }
        
        var form = new FormData();
        var inputVals = $scope.addProd;
        fillFormData(form, inputVals);
        
        if ($scope.mainImg) {
            form.append('mainImg', $scope.mainImg);    
        }
        
        if ($scope.imgFiles && $scope.imgFiles.length) {
            for (var i = 0; i < $scope.imgFiles.length; i++) {
                form.append('additionImgs', $scope.imgFiles[i].img);
            }
        }

        var categoryIds = [];
        var tagIds = [];
        
        angular.element('.categoryCheckBoxBlock input:checkbox:checked').each(function() {
            categoryIds.push(angular.element(this).val());
        });
        
        angular.element('.prodTags input:checkbox:checked').each(function() {
            tagIds.push(angular.element(this).val());
        });
        
        if (categoryIds.length === 0) {
            Notification.showNotification('Ни одной категории не выбрано.');
            return;
        }
        
        if (!$scope.addProd.tag && (!tagIds || tagIds.length < 1)) {
            Notification.showNotification('Требуется добавить минимум один тег.Создайте новый или выберите из существующих.');
            return;
        }
        
        form.append('categoryIds', categoryIds);
        form.append('tagIds', tagIds);
        
        addProductService.addNewProduct(form)
                .then(function () {
                    Notification.showNotification('Товар добавлен.');
                    $scope.isNameExist = true;
                }, function (ex) {
                    $scope.isNameExist = true;
                    Exception.show(ex);
                });
    };
});

//ADD PRODUCT SERVICE
adminApp.factory('addProductService', function ($q, $http) {
    var URL_GET_ALL_CATEGORIES = '/getAllCategoriesList';
    var URL_ADD_PRODUCT = '/admin/addProduct';
    var URL_IS_PROD_NAME_EXIST = '/admin/isProdNameExist/';
    
    return {
        isNameExist(prodName) {
            if (!prodName) {
                return;
            }        
            
            return $http({url: URL_IS_PROD_NAME_EXIST + prodName, method: 'POST' })
                    .then(function (res) {
                        return $q.resolve(res.data.response);
                    }, function (ex) {
                        return $.reject(ex);
                    });
        },
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
adminApp.controller('SearchFormController', function($scope, $timeout, $location, AdminData, searchFormService, Exception, Notification) {
    
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
                    angular.element('.searchSelect').on('change', searchOptionsEvent);        
                }, 1500);
            }, function (ex) {
                Exception.show(ex);
            });
            
    $scope.selectCategories = function (catId, e) {
        var el = $(e.currentTarget);
        if (el.css('color') !== 'rgb(0, 0, 255)') {
            el.attr('style', 'color:rgb(0, 0, 255);');
        } else {
            el.attr('style', 'color:rgb(0, 0, 0);');    
        }
        
        AdminData.setSearchCondCategories(catId);
    };
    
    $scope.setActive = function (e) {
       var name = $(e.currentTarget);
       var hidden = name.parent().parent().parent().find('.searchInputWrapper').find('.searchIsActive');
       if (hidden.val() === 'false') {
           hidden.val('true');
           name.attr('style','color:blue;');
       } else {
           hidden.val('false');
           name.attr('style','color:black;');
       }
    };
    
    $scope.submitSerchForm = function () {
        var searchInputs = angular.element('.searchInputWrapper');
        var searchConditionsList = [];
        
        for (var i = 0; i < searchInputs.length; i++) {
            var searchInputEl = angular.element(searchInputs[i]);
            if (searchInputEl.find('.searchIsActive').val() === 'false') {
                continue;
            }
            
            var serchElement = searchFormService.getSearchElement();
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
            
//            if ((serchElement.data[0] && serchElement.data[1] && (serchElement.operator === 'Beetwen' || serchElement.operator === '>=<'))
//                    ^ (serchElement.data[0] && !serchElement.data[1] && serchElement.operator !== 'Beetwen' && serchElement.operator !== '>=<')) {
                searchConditionsList.push(serchElement);
//            }
        }
        
        
        if (searchConditionsList.length === 0) {
            Notification.showNotification('Поисковый запрос не корректен');
            return;
        }
        
        var categIds = AdminData.getSearchCondCategories();
        if (categIds) {
            var categs = searchFormService.getSearchElement();
            categs.columnName = 'categories';
            categs.data = categIds;
            searchConditionsList.push(categs);
        }
        
        $timeout(function () {
            AdminData.setSearchConditions(searchConditionsList);
        }, 200);
        
        if (searchConditionsList !== undefined && searchConditionsList !== null) {      
            searchFormService.sendSearchQuery(searchConditionsList)
                    .then(function (res) {
                        $location.path('/productTable');
                        AdminData.setProductTable(res);
                    }, function (ex) {
                        if (typeof (ex) === 'string') {
                            Notification.showNotification(ex);
                        } else {
                            Exception.show(ex);  
                        }
                        AdminData.setSearchConditions(null);
                    });
        }   
    };
});

//SEARCH FORM SERVICE
adminApp.factory('searchFormService', function ($log, $http, $q, Paginator) {
    
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
                    return $http({ url : URL_SEARCH_QUERY, method : 'POST',  data: { searchQuery : conditions, limit : Paginator.getLimit(), offset : 0 }
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

//DATE SERVICE
adminApp.factory('DateService', function () {
    return {
        getYMD(timestamp) {
            var date = new Date(timestamp);
            return date.getFullYear() + '/' + date.getMonth() + '/' + date.getDay();
        }
    };
});

//PRODUCT TABLE CONTROLLER
adminApp.controller('ProductTableController', function($scope, $location, $compile, DateService, ProductTableService, AdminData, Exception, Paginator, Notification) {
    
    if (AdminData.getProductTable() !== undefined) {
        AdminData.getProductTable().products.forEach(function (el) {
            el.startDate = DateService.getYMD(el.startDate);
            if (el.lastBuyDate) {
                el.lastBuyDate = DateService.getYMD(el.lastBuyDate);
            }
        });
        
        $scope.productTableRow = AdminData.getProductTable().products;   
        
        if (!AdminData.getProductTable().products[0]) {
            $scope.prodTabMessage = 'Ни один товар не соответствует заданному условию поиска.';
            AdminData.setSearchConditions(null);
        } else {
            $scope.prodTabMessage = null;
            var paginationRow = $compile(Paginator.getPaginRow(AdminData.getProductTable().count))($scope);
            angular.element('.paginationBar').html(paginationRow);
        }
    }
    
    $scope.orderprodTab = function (prodTabOrder) {
        $scope.prodTabOrder = prodTabOrder;
    };
    
    $scope.pagClickedStyle = function(num) {
        if (Paginator.getClickedElem() === num){
            return Paginator.getStyle();
        }
    };
    
    $scope.getAnotherPage = function(num) {
        if (!num) {
            Notification.showNotification('getAnotherPage args === undefined');
            return;
        }
        
        Paginator.setClickedElem(num);
        
        ProductTableService.getAnotherPage(AdminData.getSearchConditions(), Paginator.getLimit(), (num - 1) * Paginator.getLimit())
                        .then(function(res) {
                            $location.path('/productTable');
                            res.products.forEach(function (el) {
                                el.startDate = DateService.getYMD(el.startDate);
                                if (el.lastBuyDate) {
                                    el.lastBuyDate = DateService.getYMD(el.lastBuyDate);
                                }
                            });
                            
                            $scope.productTableRow = res.products;
                        }, function(res) {
                            if (typeof(res) === 'string') {
                                Notification.showNotification(res);
                            } else {
                                Exception.show(res);
                            }
        });
    };
   
    $scope.selectProduct = function(prodId) {
        $location.path('/product/' + prodId);
    };
});

//PRODUCT TABLE SERVICE
adminApp.factory('ProductTableService', function ($http, $q) {
    
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
                return $q.reject('ProductTableService.getAnotherPage args: '.concat((searchQuery) ? '' : 'searchQuery == ' + searchQuery)
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
               return $q.reject('ProductTableService.selectProduct args: pathVariable === '.concat(pathVariable));
           }
        }
    };
});

//UPDATE PRODUCT CONTROLLER
adminApp.controller('UpdateProductController', function($scope, $window, $location, $routeParams, $log, AdminData, UpdateProductService, ProductTableService, Exception, Notification) {    
    
    if ($routeParams.prId) {
        ProductTableService.selectProduct($routeParams.prId)
                .then(function (res) {
                    if (res.product.id === null) {
                        Notification.showNotification('Товар не найден');
                        $location.path('/searchProduct');
                        return;
                    }

                    AdminData.setOriginalProduct(res);
                    AdminData.setBindProduct(res);
                    //пишу как массив что-бы использовать ng-repeat что-бы упростить связку ng-model
                    $scope.prods = [res];
                }, function (res) {
                    if (typeof (res) === 'string') {
                        Notification.showNotification(res);
                    } else {
                        Exception.show(res);
                    }
                });
    }
    
    $scope.addNewComment = {};
    
    $scope.addComment = function (prodId) {
        var addNewComment = $scope.addNewComment;
        console.log(addNewComment);
        console.log(prodId);
        if ($window.confirm("Добавить комментарий?")) {
            UpdateProductService.addComment(addNewComment.nick, addNewComment.body, prodId)
                    .then(function () {
                        UpdateProductService.updateProductComments(prodId)
                        .then(function (res) {
                            $scope.prods[0].product.comments = res;
                            AdminData.setOriginalComments(res);
                            Notification.showNotification('Коментарий добавлен.');
                        }, function (ex) {
                            if (typeof (ex) === 'string') {
                                Notification.showNotification(ex);
                            } else {
                                Exception.show(ex);
                            }
                        });
                    }, function (ex) {
                        if (typeof (ex) === 'string') {
                            Notification.showNotification(ex);
                            return;
                        }
                        Exception.show(ex);
                    });
        }
    };
    
    $scope.jampToProductTable = function () {
        AdminData.deleteProductData();
        $location.path('/productTable');
    };
    
    $scope.getOriginSingleVal = function (valName) {
        var refProd = $scope.prods[0].product;
        switch (valName.trim()) {
            case 'name': refProd.name = AdminData.getOriginalProduct().name; break;
            case 'description': refProd.description = AdminData.getOriginalProduct().description; break;
            case 'price': refProd.price = AdminData.getOriginalProduct().price; break;
            case 'mark': refProd.mark = AdminData.getOriginalProduct().mark; break;
            case 'quantity': refProd.quantity = AdminData.getOriginalProduct().quantity; break;
        }
    };
    
    $scope.getOriginComment = function (commentId) {
        var comlist = $scope.prods[0].product.comments;
            
        for (var i = 0; i < comlist.length; i++) {
            if (comlist[i].id === commentId) {   
                for (var j = 0; j < AdminData.getOriginalProduct().comments.length; j++) {
                    if (AdminData.getOriginalProduct().comments[j].id === commentId) {
                        comlist[i] = AdminData.getOriginalComment(j);
                        return;
                    }
                }
            }
        }
    };
    
    $scope.getOriginCategories = function () {
        $scope.prods[0].product.category = AdminData.getOriginalCategories();
    };
    
    $scope.deleteCategory = function(catId) {
        var catList = $scope.prods[0].product.category;

        for (var i = 0; i < catList.length; i++) {
            if (catList[i].id === catId) {
                catList.splice(i, 1);
            }
        }
    };
    
    $scope.updateRecommend = function (prodId) {
        if (prodId) {
            UpdateProductService.updateRecommend(prodId)
                    .then(function () {
                        $scope.prods[0].product.isRecommend = ($scope.prods[0].product.isRecommend) ? false : true;
                    }, function (ex) {
                        if (typeof(ex) === 'string') {
                            $log.error(ex);
                            return;
                        }
                        
                        Exception.show(ex);
                    });
        } 
    };
    
    $scope.deleteProduct = function(prodId) {
        if ($window.confirm('Подтвердите удаление товара.')) {
            UpdateProductService.deleteProduct(prodId)
                    .then(function () {
                        AdminData.deleteProductData();
                        $location.path('/admin/searchProduct');
                        Notification.showNotification('Товар удален.');
                    }, function (ex) {
                        if (typeof (ex) === 'string') {
                            Notification.showNotification(ex);
                            return;
                        }
                        
                        Exception.show(ex);
                    });
        }
    };
    
    $scope.deleteComment = function( commentId ) {
        if ($window.confirm('Подтвердите удаление комментария.')) {
            UpdateProductService.deleteComment( commentId )
                    .then(function () {
                        var comList = $scope.prods[0].product.comments;
                        for (var i = 0; i < comList.length; i++) {
                            if (commentId !== undefined && comList[i].id === commentId) {
                                comList.splice(i, 1);
                            }
                        }
                        Notification.showNotification('Комментарий удален.');
                    }, function (ex) {
                        if (typeof (ex) === 'string') {
                            Notification.showNotification(ex);
                            return;
                        }
                        Exception.show(ex);
                    });
        }
    };
    
    $scope.getOriginImg = function() {
        angular.element('#adminProdImage').attr('src',  AdminData.getOriginalProduct().imgUrl);
    };    
    
    $scope.showCategories = function() {
        var addCatLink = angular.element('.upAllCatBlock');
        var title = angular.element('.showCategoryUpdate');
        if (addCatLink.is(':visible')) {
            title[0].innerText = 'Показать все категории';
            addCatLink.hide();
        } else {
            title[0].innerText = 'Скрыть все категории';
            addCatLink.show();
        }
    };
    
    $scope.addCategory = function(catId) {
        var isExist = false;
        if (!$scope.prods[0].product.category) {
            $scope.prods[0].product.category = [];    
        }
        
        for (var i = 0; i < $scope.prods[0].product.category.length; i++) {
            if (catId !== undefined && $scope.prods[0].product.category[i].id === catId) {
                isExist = true;
            }
        }

        if (!isExist) {
            $scope.prods[0].product.category.push(AdminData.getCategory(catId));
        }
    };
    
    $scope.showComments = function() {
        var hidenCat = angular.element('.upComBlock');
        var title = angular.element('.showCommentsUpdate');
        console.log(title);
        if (hidenCat.is(':visible')) {
            title[0].innerText = 'Показать комментарии';
            hidenCat.hide();
        } else {
            title[0].innerText = 'Скрыть комментарии';
            hidenCat.show();
        }
    };
    
    $scope.updateComment = function(commentId) {
        var comments = $scope.prods[0].product.comments;
        
        UpdateProductService.updateComment(commentId, comments)
                .then(function (res) {
                    AdminData.setOriginalComments(res);
                    Notification.showNotification('Комментарий изменен.');
                }, function (ex) {
                    if (typeof (ex) === 'string') {
                        Notification.showNotification(ex);
                        return;
                    }
                    Exception.show(ex);
                });
    };
    
    $scope.updateProductTableField = function(prodId, fieldName, fieldVal) {
        if (!prodId ||  !fieldName || fieldVal === undefined) {
            Notification.showNotification('!prodId ||  !fieldName || fieldVal === undefined');
        }
        
        if ($window.confirm('Подтвердите изменение поля ' + fieldName + ".")) {
            UpdateProductService.updateProductTableField(prodId, fieldName, fieldVal)
                    .then(function () {
                        Notification.showNotification('Поле ' + fieldName + ' изменено.');
                    }, function (ex) {
                        if (typeof (ex) === 'string') {
                            Notification.showNotification(ex);
                        } else {
                            Exception.show(ex);
                        }
                    });
        }
    };
    
    $scope.updateCategories = function (prodId) {
        if ($window.confirm("Применить изменения к категориям?")) {
            var newCategories = $scope.prods[0].product.category;
            var oldCategories = AdminData.getOriginalCategories();
            var newCategoriesIds = [];
            var oldCategoriesIds = [];

            newCategories.forEach(function (el) {
                newCategoriesIds.push(el.id);
            });

            oldCategories.forEach(function (el) {
                oldCategoriesIds.push(el.id);
            });

            UpdateProductService.updateCategories(prodId, oldCategoriesIds, newCategoriesIds)
                    .then(function () {
                        Notification.showNotification('Категории изменены');
                    }, function (ex) {
                        if (typeof (ex) === 'string') {
                            Notification.showNotification(ex);
                        } else {
                            Exception.show(ex);
                        }
                    });
        }
    };
    
    $scope.updateImg = function (productId, imgId) {
        var updateImgfile = angular.element('#adminImgMultipart').prop('files')[0];
        if (updateImgfile) {
            UpdateProductService.updateImage(productId, imgId, updateImgfile)
                    .then(function () {
                        Notification.showNotification('Изображение изменено.');
                    }, function (ex) {
                        if (typeof (ex) === 'string') {
                            Notification.showNotification(ex);
                        } else {
                            Exception.show(ex);
                        }
                    });
        }
    };
});

//UPDATE PRODUCT SERVICE
adminApp.factory('UpdateProductService', function($http, $q, AdminData) {
//    var controllerScope;
    var URL_ADD_COMMENT = '/admin/addComment';
    var URL_DELETE_PRODUCT = '/admin/deleteProduct/';
    var URL_DELETE_COMMENT = '/admin/deleteComment/';
    var URL_UPDATE_COMMENT = '/admin/updateComment';
    var URL_UPDATE_CATEGORIES = '/admin/updateCategories';
    var URL_UPDATE_IMG = '/admin/updateImg';
    var URL_GET_ALL_COMMENTS = '/admin/getAllProductComments/';
    var URL_ADD_TO_RECOMMEND = '/admin/updateRecommend/';
    
    return {
        updateRecommend (prodId) {
            if (prodId) {
                return $http({url : URL_ADD_TO_RECOMMEND + prodId, method : 'POST'})
                        .then(null, function (ex) {
                            return $q.reject(ex.data.response);
                        });
            } else {
                return $q.reject('UpdateProductService updateRecommend args === undefined');
            }
        },
        updateImage (productId, imageId, updateImgfile) {
            if (productId && updateImgfile) {
                var form = new FormData();
                form.append('productId', productId);
                form.append('imageId', imageId);
                form.append('image', updateImgfile);
                
                return $http({ url: URL_UPDATE_IMG, method: 'POST', data: form, headers: { 'Content-Type': undefined } })
                        .then(null, function (ex) {
                            return $q.reject(ex.data.response);
                        });
            } else {
                return $q.reject('UpdateProductService updateImage: некорректные аргументы'.concat((!productId) ? ' productId === ' + productId : '').concat((!imageId) ? ' imageId === ' + imageId : '').concat((!updateImgfile) ? ' updateImgfile === ' + updateImgfile : ''));
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
                $q.reject('UpdateProductService updateCategory: некорректные аргументы'.concat((!productId) ? ' productId === ' + productId : '').concat((!oldCategoiesId) ? ' oldCategoiesId === ' + oldCategoiesId : '').concat((!newCategoriesId) ? ' newCategoriesId === ' + newCategoriesId : ''));
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
                return $q.reject('UpdateProductService updateProductComments: Неккоректный аргумент prodId === '.concat(prodId));
            }
        },
        updateProductTableField: function(prodId, htmlElName, htmlElVal) {
            if (prodId && htmlElName !== undefined && htmlElVal !== undefined) {
                    return $http({ url: '/admin/updateProductField', method: 'POST', data: { productId: prodId, columnName: htmlElName, value: htmlElVal }})
                            .then(null, function (res) {
                                return $q.reject(res.data.response);
                            });
            }
        },
        updateComment: function(commentId, comments) {
            var comment = null;
            var originComments = null;
            
            if (commentId && comments) {
                for (var i = 0; i < comments.length; i++) {
                    if (comments[i].id === commentId) {
                        comment = comments[i];
                        break;
                    }
                }

                if (comment !== undefined && comment.nick.trim !== '' && comment.body.trim() !== '') {
                    originComments = AdminData.getOriginalComments();
                    for (var i = 0; i < comments.length; i++) {
                        if (originComments[i].id === commentId) {
                            if (originComments[i].nick === comment.nick && originComments[i].body === comment.body) {
                                return $q.reject('Данные в коментарии не изменились. Коментарий не будет изменен.');
                                break;
                            } else {
                                return $http({ url: URL_UPDATE_COMMENT, method: 'POST', data: { id: commentId, nick: comment.nick, body: comment.body } })
                                        .then(function () {
                                            return originComments;
                                        }, function(res) {
                                            return $q.reject(res.data.response);
                                        });
                            }
                        }
                    }
                } else {
                    return $q.reject('UpdateProductService updateComment: некорректные аргументы'.concat((!commentId) ? ' comentId === ' + commentId : '').concat((!comments) ? 'comments ===' + comments : ''));
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
            if (typeof (commentId) === 'number') {
                return $http({ url: URL_DELETE_COMMENT + commentId , method: 'POST' })
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
                return $q.reject('UpdateProductService param prodId === '.concat(prodId));
            }
        }
    };
});

//ADMIN NAV CONTROLLER
adminApp.controller('adminNavController', function($scope) {
    
    $scope.setVisible = function (name) {
        var elem;
        if (name === 'changeNick') {
            elem = angular.element('.adminBarBlock .adminNick');
        } else if (name === 'changePassword') {
            elem = angular.element('.adminBarBlock .adminPassword');
        } else if (name === 'product') {
            elem = angular.element('.adminBarBlock .product');
        } else if (name === 'category') {
            elem = angular.element('.adminBarBlock .category');
        } else if (name === 'tag') {
            elem = angular.element('.adminBarBlock .tag');
        } else if (name === 'statistic') {
          elem = angular.element('.adminBarBlock .statistic'); 
        }
        
        if (elem.css('display') === 'none') {
                elem.css('display', 'block');
            } else {
                elem.css('display', 'none');
            }
    };
});

//PAGINATOR
adminApp.factory('Paginator', function() {
    var clickedStyle = { 'background-color' : '#CEFF81' };
    var clickedColor = 'green';
    var limit = 5;
    var clickedElemtIndex = -1;
    var tagTableLimit = 10;
    var addProductTagsLimit = 50;
    
    return {
        getPaginRow(count, lim) {
            lim = (lim) ? lim : limit;
            var pagElement = '';
            var mod = count % lim;
            for (var i = 1; i <= count / lim + ((mod === 0) ? 0 : 1); i++) {
                pagElement += '<div class="pagElement"  ng-style="pagClickedStyle(' + i + ')"  ng-click="getAnotherPage('+ i +')"> <p style="width:20px;" class="pagP">' + i + '</p> </div>';
            }   
            return pagElement;
        },
        getAddProductTagsLimit() {
           return addProductTagsLimit;
        },
        getClickedColor() {
           return clickedColor; 
        },
        getTagTableLimit() {
           return tagTableLimit; 
        },
        setLimit(num) {
            limit = num;
        },
        getLimit () {
            return limit;
        },
//        setOffset(num) {
//            offset = num;
//        },
//        getOffset() {
//            return offset;
//        },
        setStyle(style) {
            clickedStyle = style;
        },
        getStyle() {
            return clickedStyle;
        },
        setClickedElem(num) {
            clickedElemtIndex = num;
        },
        getClickedElem() {
            return clickedElemtIndex;
        }
    };
});

//NOTIFICATION
adminApp.factory('Notification', function ($timeout, $log) {
    return {
        showNotification : function (message) {
            if (!message) {
                $log.warn('NotificationService : '.concat('message = ') + message);
                return;
            }
            
            $('.NotificationMessage p').text(message);
            $('.NotificationWindow').show(300);
            
            $timeout(function () {
                $('.NotificationWindow').hide(300);
            }, 10000);
        }
    };
});

//EXCEPTION
adminApp.factory('Exception', function($log) {
    var ExceptionModal = $('.adminExWrapper');
    var exName = ExceptionModal.find('.ExceptionModalName');
    var exMess = ExceptionModal.find('.ExceptionModalMessage');
    var exCause = ExceptionModal.find('.ExceptionModalCause');
    var exStrace = ExceptionModal.find('.ExceptionModalStrace');
    
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
                exName.text(dataResponse.ExceptionName || '');
                exMess.text(dataResponse.message || '');
                exCause.text(dataResponse.cause || '');
                exStrace.html(dataResponse.strace || '');
            } catch (ex) {
                $log.error('Exception: fields are not init.');
                return;
            }
            ExceptionModal.show();
        }
    };
});

//ADMIN DATA
adminApp.factory('AdminData', function($log) {
    
    var searchConditions;
    var productTable;
    var bindProduct;
    var originalProduct;
    var searchCondCategories = [];
    
    function Product() {
        this.name; 
        this.description; 
        this.price; 
        this.quantity; 
        this.mark; 
        this.imgUrl;  
        this.categories = []; 
        this.comments = []; 
        this.allCategories = []; 
        this.init = function(data) { 
            this.name = data.product.name; 
            this.description = data.product.description;  
            this.price = data.product.price; 
            this.quantity = data.product.quantity; 
            this.mark = data.product.mark; 
            this.imgUrl = data.product.imgUrl;
            
            if (data.product.comments) {
                for (var i = 0; i < data.product.comments.length; i++) {
                    var comment = new Comment(data.product.comments[i].id, data.product.comments[i].nick, data.product.comments[i].body);
                    this.comments.push(comment);
                }
            }
            
            if (data.product.category) {
                for (var i = 0; i < data.product.category.length; i++) {
                    var category = new Category(data.product.category[i].id, data.product.category[i].name);
                    this.categories.push(category);
                }
            }
            
            if (data.allCategories) {
            for (var i = 0; i < data.allCategories.length; i++) {
                var category = new Category(data.allCategories[i].id, data.allCategories[i].name);
                this.allCategories.push(category);
            }
            }
        };
    }
    
    function Comment(id, nick, body) { 
        this.id = id; 
        this.nick = nick; 
        this.body = body; 
    }
    
    function Category(id, name) { 
        this.id = id; 
        this.name = name; 
    }
    
    return {
        setSearchCondCategories(catId) {
            if (catId) {
                if (searchCondCategories.length === 0) {
                    searchCondCategories.push(catId);
                    return;
                }
                
                var catIndex = -1;
                for (var i = 0; i < searchCondCategories.length; i++) {
                    if (searchCondCategories[i] === catId) {
                        catIndex = i;
                    }
                }
                
                if (catIndex === -1) {
                    searchCondCategories.push(catId);
                } else {
                    searchCondCategories.splice(catIndex, 1);
                }
            }
        },
        getSearchCondCategories() {
            return searchCondCategories;
        },
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
                        $('.adminProdImage').attr('src', ImgReader.result);
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
            element.bind('change', function(e) {
                var addElm = angular.element(e.target).prop('files')[0];
                ngModel.$setViewValue(addElm);
                var addImgReader = new FileReader();
                addImgReader.onload = function() {
                        angular.element('#prodBigImage').attr('src', addImgReader.result);
                };
                addImgReader.readAsDataURL(addElm);
            });  
        }
    };
});

adminApp.directive('validName', function ($http, $q, Notification) {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ngModel) {
            return ngModel.$asyncValidators.nameExist = function (viewValue) {
                if (viewValue === undefined || viewValue === '') {
                    return $q.resolve();
                }
                
                if (viewValue === null) {
                    return $q.resolve();
                }
                
                var url;
                if (attrs.validName.includes('addCatName') || attrs.validName.includes('upCatName')) {
                    var catId = attrs.validName.split(' ')[0];
                    if (catId !== 'addCatName' && catId !== 'upCatName') {
                        url = '/isCategoryNameExist/' + catId + '/';
                    } else {
                        url = '/isCategoryNameExist/-1/';
                    }  
                }
                
                return $http( { url: url + String(viewValue).trim() , method: 'POST' } )
                        .then(function (res) {
                            if (res.data.response === true) {
                                if (attrs.validName === 'addCatName') {
                                    scope.addCatName = viewValue;
                                } else if (attrs.validName === 'upCatName') {
                                    scope.upCatName = viewValue;
                                } 
                                
                                return $q.reject();
                            }

                            return $q.resolve();
                        }, function (ex) {
                            Notification.showNotification(ex.data.response);
                            return $q.resolve();
                        });
            };
        }
    };
});

adminApp.directive('validPid', function ($http, $q, Notification) {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ngModel) {
            return ngModel.$asyncValidators.pidExist = function (viewValue) {                
                if (viewValue === undefined || viewValue === '') {
                    return $q.resolve();
                }
                
                if (viewValue === null) {
                    return $q.resolve();
                }
                
                return $http( { url: '/isCategoryPidExist/' + String(viewValue).trim() , method: 'POST' } )
                        .then(function (res) {
                            if (res.data.response === true) {
                                if (attrs.validPid === 'addCatName') {
                                    scope.addCatName = viewValue;
                                } else if (attrs.validPid === 'upCatName') {
                                    scope.upCatName = viewValue;
                                } 
                                return $q.reject();
                            }

                            return $q.resolve();
                        }, function (ex) {
                            Notification.showNotification(ex.data.response);
                            return $q.resolve();
                        });
            };
        }
    };
});
