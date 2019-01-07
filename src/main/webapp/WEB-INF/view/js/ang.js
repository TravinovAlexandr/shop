//$routeParams

var ng = angular.module('ngPage', ['ngRoute']);

ng.config(function($routeProvider, $locationProvider) {
    // РоутеПровайдер
    $routeProvider.when('/hi', {
        //tempalateUrl вставка штимельки во ng-view 
        //tempalateUrl: 'html/index.html',
        //template - вставка некоторого шаблона
        //используя деффер аргумент можно передать  его в темплейт
        template: '<p>Hey!{{$resolve.third}}</p>',        
        //controller - определения обработчика (привязка к ng-controller невозможна)
        controller: 'angController',
        
        //resolve - принимает объект, функции которого отрабатывают самыми первыми во всем провайдере.
        //можно использовать, к примеру, для проверки аутентификации пользователя, с подменой контроллера
        resolve: {
            //любое название
            first: function() {
                alert(1);
            },
            seccond: function($q, $timeout) {
                //$q.defer() - получаем отложенный объект
                let defer = $q.defer();
                $timeout(function(){
                    //обязателен или ничего не отработает.
                    defer.resolve();
                    alert(2);
                }, 1000);
                //defer.promise - тормозит исполнение в ожидании отработки defer.resolve();
                return defer.promise;
            },
            third: function($q) {
                let def = $q.defer();
                //в дефер можно передать аргумент который будет доступен в контроллере
                //соответственно для этого необходимо использовать отложенный объект
                def.resolve("Hello Alex");
                alert(3);
                return def.promise;
            }
        }
    });
    
    
    $routeProvider.when('/angular', {
        templateUrl: 'html/ang1.html'
    });
    
    $routeProvider.when('/ang', {});
    
    //ЛокейшнПровайдер необходим для включения внешних штимелин в ng-view
   //ниже представленна единственно возможная конфигурация
    $locationProvider.html5Mode({enabled: true, requireBase: false});
});

ng.controller('angController', function($scope, $q, sN) {
        let def = $q.defer();
        $scope.greet = sN.frstFld;
        //передача непосредственно из контроллера
        def.resolve("AAA");        
        def.promise.then(function(data) {
            //получить аргумент переданный в контроллер возможно только внутри промайса
            let thirdDef = $scope.$resolve.third;
            window.alert(`angController works   ${data}  ${thirdDef}`);
        });
});


//КАСТОМНЫЙ СЕРВИС
ng.service('sN', function() {
    let self = this;
    this.frstFld = '';
    
    this.frstFld = function(arg) {
        return (arg !== null && typeof(arg) === 'number') ? arg * arg : null;
    };
    this.getLength = function() {
        return self.frstFld.length;
    };
    
});