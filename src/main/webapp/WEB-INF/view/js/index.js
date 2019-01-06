let index = angular.module('index', []);

index.controller('navigationBarController', ['$scope', '$location', function($scope, $location){
       
        $scope.searchFormSubmit = function() {
          console.log($location); 
       };
}]);

//Привязка данных
//ng-bind: осуществляет привязку к свойству innerText html-элемента
//ng-bind-html: осуществляет привязку к свойству innerHTML html-элемента
//ng-bind-template: аналогична ng-bind за тем исключением, что позволяет установить привязку сразу к нескольким выражениям
//ng-model: создает двустороннюю привязку
//ng-non-bindable: определяет участок html-кода, в котором привязка не будет использоваться
