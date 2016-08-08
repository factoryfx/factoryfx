'use strict';

/* jhint helpers: */
/* jshint -W097 */
/*global moment */
/*global angular */
/*global alert */

angular.module('factoryfxwebgui.errorView', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/error', {
        templateUrl: 'view/error/Error.html',
        controller: 'ErrorController'
    });
}])

.controller('ErrorController', ['$scope', '$routeParams', 'errorDataService',
function(                        $scope,   $routeParams,   errorDataService) {
    $scope.errorResponse = errorDataService.errorResponse;
}]);
