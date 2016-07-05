'use strict';

/* jhint helpers: */
/* jshint -W097 */
/*global moment */
/*global angular */
/*global alert */

var defaultResolve = {
    'userDataResolved': ['metaDataService','guiModelService','$q',
        function(            metaDataService,  guiModelService,  $q) {
            var promiseUserData = metaDataService.update();
            var guiModelService = guiModelService.update();
            return $q.all([promiseUserData,guiModelService]);
        }]
};

angular.module('factoryfxwebgui.loginView', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/login', {
        templateUrl: 'view/login/Login.html',
        controller: 'LoginController',
        resolve: defaultResolve
    });
}])

.controller('LoginController', ['$scope', '$resource', '$location','$routeParams','guiModelService',
function                        ($scope,   $resource,   $location,  $routeParams,  guiModelService  ) {
    $scope.guiModel=guiModelService.data;



    $scope.userData = {};
    $scope.userData.user="a";
    $scope.userData.password="a";
    $scope.userData.locale="en";
    if ($routeParams.user){
        userData.user=$routeParams.user;

    }
    if($routeParams.password){
        userDataService.password=$routeParams.password;
    }
    $scope.loginFailed = false;
    $scope.connect = function () {
        return $resource('../applicationServer/login').save($scope.userData,
            function (loginResult) {
                if (loginResult.successfully) {
                    $location.path('factoryEditor');
                    //userDataService.update();
                } else {
                    $scope.loginFailed=true;
                }
            }
        ).$promise;
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