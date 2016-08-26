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

angular.module('factoryfxwebgui.historyView', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/history', {
        templateUrl: 'view/history/History.html',
        controller: 'HistoryController',
        resolve: defaultResolve
    });
}])

.controller('HistoryController', ['$scope','metaDataService','guiModelService', '$resource',
function                          ($scope,  metaDataService,  guiModelService,   $resource) {
    $scope.metaData = metaDataService.data;
    $scope.guiModel = guiModelService.data;

    $scope.update=function(id){
        $scope.history=null;
        return $resource('../applicationServer/history', {id:id}).query(function(response){
            $scope.history=response;
        }).$promise;
    };
    $scope.update();

    $scope.selected={
        factory: null,
        mergeDiff:null
    };
    $scope.showDiff=function(row){
        $scope.selected.factory=row;
        $resource('../applicationServer/diff', {id:row.id}).get(function(response){
            $scope.selected.mergeDiff=response;
        });
    };

}]);