'use strict';

/* jhint helpers: */
/*global moment */
/*global angular */
/*global alert */

// Declare app level module which depends on views, and components
var mod = angular.module('factoryfxwebgui', [
    'ngRoute',
    'ngResource',
    'ngAnimate',
    'ngAria',
    'ui.bootstrap',
    'factoryfxwebgui.directives',
    'factoryfxwebgui.navigationBar',
    'factoryfxwebgui.services',
    'factoryfxwebgui.factoryEditor',
    'factoryfxwebgui.loginView',
    'factoryfxwebgui.errorView',
    'factoryfxwebgui.dashboard',
    'factoryfxwebgui.historyView'
]).
config(['$routeProvider', function($routeProvider) {
    $routeProvider.otherwise({redirectTo: '/login'});
}]);
mod.config(['$compileProvider', function ($compileProvider) {
    // $compileProvider.debugInfoEnabled(false);//https://docs.angularjs.org/guide/production
}]);

mod.factory('myHttpInterceptor', ["$q", "$location", "errorDataService",function ($q, $location, errorDataService) {
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
            return $q.reject(rejection);
        }

    };
}]);
mod.config(["$httpProvider", function ($httpProvider) {
    $httpProvider.interceptors.push('myHttpInterceptor');
}]);