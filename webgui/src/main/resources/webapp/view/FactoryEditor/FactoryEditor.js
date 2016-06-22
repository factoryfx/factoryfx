'use strict';

var defaultResolve = {
    'userDataResolved': ['metaDataService','$q', function(metaDataService,$q) {
        var promiseUserData = metaDataService.update();
        return $q.all([promiseUserData]);
    }]
};

angular.module('factoryfxwebgui.factoryEditor', ['ngRoute'])

.filter("pagingFilter", function(){
    return function(input, pageSize, currentPage) {
        return input ?  input.slice((currentPage-1) * pageSize, (currentPage-1  + 1 ) * ( pageSize)) : [];
    };
})

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

    $scope.pagingConfig={
        itemsPerPage: 5,
        currentPage: 1
    };
    
    $scope.selected={
        factory: undefined
    }

    $scope.loadRoot=function() {
        $scope.mergeDiff=null;
        $resource('../applicationServer/root').get(function (response) {
            $scope.selected.factory = response;
            $scope.selected.originalFactory={};
            angular.copy(response,$scope.selected.originalFactory);
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
    $scope.isDirty=function(){
        return !angular.equals($scope.selected.factory,$scope.selected.originalFactory);
    }
    
}]);