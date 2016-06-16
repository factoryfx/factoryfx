'use strict';

/* jhint helpers: */
/* jshint -W097 */
/*global moment */
/*global angular */
/*global alert */

angular.module('factoryfxwebgui.loginView', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/login', {
        templateUrl: 'view/login/Login.html',
        controller: 'LoginController'
    });
}])

.controller('LoginController', ['$scope', '$resource', '$location','$routeParams',
function                        ($scope,   $resource,   $location,  $routeParams ) {
    $scope.userData = {};
    if ($routeParams.user){
        userData.user=$routeParams.user;

    }
    if($routeParams.password){
        userDataService.password=$routeParams.password;
    }
    $scope.loginFailed = false;
    $scope.connect = function () {
        $resource('../applicationServer/login').save($scope.userData,
            function (loginResult) {
                if (loginResult.successfully) {
                    $location.path('factoryEditor');
                    //userDataService.update();
                } else {
                    $scope.loginFailed=true;
                }
            }
        );
    };

    $scope.locales=$resource('../applicationServer/locales').query();

    $scope.getInputCssClass = function(error){
        for (var prop in error) {
            if (error.hasOwnProperty(prop)) {
                if (error[prop]){
                    return 'has-error';
                }
            }
        }
        return '';
    };
}]);