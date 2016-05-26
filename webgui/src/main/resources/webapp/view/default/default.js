'use strict';

angular.module('myApp.view1', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/view1', {
        templateUrl: 'view/default/default.html',
        controller: 'View1Ctrl'
    });
}])

.controller('View1Ctrl', [function() {

}]);