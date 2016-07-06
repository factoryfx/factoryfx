'use strict';

var defaultResolve = {
    'userDataResolved': ['metaDataService','guiModelService','$q',
     function(            metaDataService,  guiModelService,  $q) {
        var promiseUserData = metaDataService.update();
        var guiModelService = guiModelService.update();
        return $q.all([promiseUserData,guiModelService]);
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

.controller('GenericEditorController', ['$scope','metaDataService','guiModelService', '$resource',
function                                ($scope,  metaDataService,  guiModelService,   $resource) {
    $scope.metaData = metaDataService.data;
    $scope.guiModel = guiModelService.data;

    $scope.pagingConfig={
        itemsPerPage: 5,
        currentPage: 1
    };
    
    $scope.selected={
        factory: undefined
    }

    $scope.loadRoot=function() {
        $scope.stagedChanges=false;
        $scope.deployResponse=null;
        return $resource('../applicationServer/root').get(function (response) {
            $scope.selected.factory = response.toJSON();
            $scope.selected.originalFactory={};
            angular.copy($scope.selected.factory,$scope.selected.originalFactory);
        }).$promise;
    }

    $scope.selectFactory=function(id){
        $scope.selected.factory=null;
        return $resource('../applicationServer/factory', {id:id}).get(function(response){
            $scope.selected.factory=response;
            $scope.selected.originalFactory={};
            angular.copy(response,$scope.selected.originalFactory);
        }).$promise;
    }

    $scope.stagedChanges=false;
    $scope.save=function(){
        return $resource('../applicationServer/factory').save($scope.selected.factory, function(response){
            $scope.stagedChanges=true;
            $scope.selected.originalFactory={};
            angular.copy(response,$scope.selected.originalFactory);
        }).$promise;
    }

    $scope.reset=function(){
        $scope.selected.factory=angular.copy($scope.selected.originalFactory);
    }

    $scope.deploy=function(){
        $scope.selected.factory=null;
        return $resource('../applicationServer/deploy').get(function(response){
            $scope.deployResponse=response;
        }).$promise;
    }
    $scope.isDirty=function(){
        return !angular.equals($scope.selected.factory,$scope.selected.originalFactory);
    }

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

    $scope.initializingEditing=true;
    $resource('../applicationServer/loadCurrentFactory').get(function(response){
        $scope.loadRoot().then(function(result) {
            $scope.initializingEditing=false;
            return result;
        });
    });

    
}]);