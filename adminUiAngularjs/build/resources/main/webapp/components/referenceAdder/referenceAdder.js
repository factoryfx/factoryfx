'use strict';

/* jhint helpers: */
/*global moment */
/*global angular */
/*global Pikaday */
/*global alert */


angular.module('factoryfxwebgui.directives')//re-open module definition

.directive('referenceadder',function () {
    return {
        restrict: 'EA',
        scope: {
            attributename: '=attributename',
            factory: '=factory',
            originalFactory: '=originalFactory',
            attribute: '=attribute'

        },
        templateUrl: 'components/referenceAdder/referenceAdder.html',
        replace: true,

        controller: ["$scope", "$resource", function ($scope,$resource) {
            $scope.selectDialog={
                visible: false,
                possibleValues:[]
            };

            $scope.pagingConfig={
                itemsPerPage: 5,
                currentPage: 1
            };

            $scope.loadPossibleValues =function(){
                $resource('../applicationServer/possibleValues', {id:$scope.factory.factory.id, attributeName: $scope.attributename}).query(function(response){
                    $scope.selectDialog.possibleValues=response;
                });
            };

            $scope.addFactory=function(id){
                if (Array.isArray($scope.attribute)){
                    $scope.attribute.push({id: id});
                } else {
                    $scope.attribute={id: id};
                }
            };

            $scope.addNewFactory = function () {
                $resource('../applicationServer/newEntry', {
                    id: $scope.factory.factory.id,
                    attributeName: $scope.attributename
                }).get(function (response) {
                    $scope.factory = response;
                    $scope.originalFactory={};
                    angular.copy($scope.factory,$scope.originalFactory);
                });
            };

            $scope.isDirty=function(){
                return !angular.equals($scope.factory,$scope.originalFactory);
            };


        }]
    };
});