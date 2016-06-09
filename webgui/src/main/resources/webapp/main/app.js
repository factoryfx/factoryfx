'use strict';

// Declare app level module which depends on views, and components
angular.module('factoryfxwebgui', [
    'ngRoute',
    'ngResource',
    'factoryfxwebgui.services',
    'factoryfxwebgui.view1'
]).
config(['$routeProvider', function($routeProvider) {
    $routeProvider.otherwise({redirectTo: '/view1'});
}]);