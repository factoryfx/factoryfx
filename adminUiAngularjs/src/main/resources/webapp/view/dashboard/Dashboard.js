'use strict';

/* jhint helpers: */
/*global moment */
/*global angular */
/*global alert */

var defaultResolve = {
    'userDataResolved': ['metaDataService','guiModelService','$q',
     function(            metaDataService,  guiModelService,  $q) {
        return $q.all([metaDataService.update(),guiModelService.update()]);
    }]
};

angular.module('factoryfxwebgui.dashboard', ['ngRoute'])
    
.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/dashboard', {
        templateUrl: 'view/dashboard/Dashboard.html',
        controller: 'DashboardController',
        resolve: defaultResolve
    });
}])

.controller('DashboardController', ['$scope','metaDataService','guiModelService', '$resource',
function                            ($scope,  metaDataService,  guiModelService,   $resource) {
    $scope.metaData = metaDataService.data;
    $scope.guiModel = guiModelService.data;

    $scope.update=function(id){
        $scope.dashboard=null;
        return $resource('../applicationServer/dashboard', {id:id}).get(function(response){
            $scope.dashboard=response;
        }).$promise;
    };
    $scope.update();

    
}]);