'use strict';

var defaultResolve = {
    'userDataResolved': ['metaDataService','$q', function(metaDataService,$q) {
        var promiseUserData = metaDataService.update();
        return $q.all([promiseUserData]);
    }]
};

angular.module('factoryfxwebgui.view1', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/view1', {
        templateUrl: 'view/GenericEditor/GenericEditor.html',
        controller: 'GenericEditorController',
        resolve: defaultResolve
    });
}])

.controller('GenericEditorController', ['$scope','metaDataService', '$resource',
function                                ($scope,  metaDataService,   $resource) {

    $scope.headline = 'Basis Controller';
    $scope.metaData = metaDataService;
    
    $scope.selected={
        factory: undefined
    }
    
    $resource('../applicationServer/root').get(function(response){
        $scope.selected.factory=response;
    })
}]);