'use strict';

var defaultResolve = {
    'userDataResolved': ['metaDataService','$q', function(metaDataService,$q) {
        var promiseUserData = metaDataService.update();
        return $q.all([promiseUserData]);
    }]
};

angular.module('factoryfxwebgui.factoryEditor', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/factoryEditor', {
        templateUrl: 'view/factoryEditor/FactoryEditor.html',
        controller: 'GenericEditorController',
        resolve: defaultResolve
    });
}])

.controller('GenericEditorController', ['$scope','metaDataService', '$resource',
function                                ($scope,  metaDataService,   $resource) {
    $scope.metaData = metaDataService;
    
    $scope.selected={
        factory: undefined
    }

    $scope.loadRoot=function() {
        $resource('../applicationServer/root').get(function (response) {
            $scope.selected.factory = response;
        })
    }

    $scope.selectFactory=function(id){
        $resource('../applicationServer/factory', {id:id}).get(function(response){
            $scope.selected.factory=response;
        })
    }

    $scope.save=function(id){
        $resource('../applicationServer/factory').save($scope.selected.factory);
    }

    $scope.deploy=function(){
        $scope.selected.factory=null;
        $scope.mergeDiff = $resource('../applicationServer/deploy').get();
    }
}]);