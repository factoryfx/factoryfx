'use strict';

// Declare app level module which depends on views, and components
var mod = angular.module('factoryfxwebgui', [
    'ngRoute',
    'ngResource',
    'ngAnimate',
    'factoryfxwebgui.services',
    'factoryfxwebgui.view1',
    'factoryfxwebgui.loginView',
    'factoryfxwebgui.errorView'
]).
config(['$routeProvider', function($routeProvider) {
    $routeProvider.otherwise({redirectTo: '/login'});
}]);

mod.factory('myHttpInterceptor', function ($q, $location, errorDataService) {
    return {
        'request': function (config) {
            return config || $q.when(config);
        },

        'requestError': function (rejection) {
            return $q.reject(rejection);
        },

        'response': function (response) {
            return response || $q.when(response);
        },

        'responseError': function (rejection) {
            errorDataService.errorResponse=rejection;
            $location.path("error");
            console.log(rejection);
            return $q.reject(rejection);
        }

    };
});
mod.config(function ($httpProvider) {
    $httpProvider.interceptors.push('myHttpInterceptor');
});