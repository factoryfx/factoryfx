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

angular.module('factoryfxwebgui.factoryView', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/factoryView', {
        templateUrl: 'view/factoryView/FactoryView.html',
        controller: 'FactoryViewController',
        resolve: defaultResolve
    });
}])

.controller('FactoryViewController', ['$scope','metaDataService','guiModelService', '$resource', '$location',
function                              ($scope,  metaDataService,  guiModelService,   $resource,   $location) {
    $scope.metaData = metaDataService.data;
    $scope.guiModel = guiModelService.data;

    $scope.update=function(id){
        $scope.factoryView=null;
        return $resource('../applicationServer/view', {id:$location.search().id}).get(function(response){
            $scope.factoryView=response;
        }).$promise;
    };
    $scope.update();

    $scope.factory=null;
    $scope.showFactory= function(factoryId){
        return $resource('../applicationServer/factory', {id:factoryId}).get(function(response){
            $scope.factory=response;
        }).$promise;
    };

    
}]);