'use strict';

/* jhint helpers: */
/*global moment */
/*global angular */
/*global Pikaday */
/*global alert */


angular.module('factoryfxwebgui.directives')//re-open module definition

.directive('selectreference',function () {
    return {
        restrict: 'EA',
        scope: {
            attributename: '=attributename',
            factoryid: '=factoryid',
            attribute: '=attribute'
        },
        templateUrl: 'components/selectReference/selectReference.html',
        replace: true,

        controller: ["$scope", "$resource", function ($scope,$resource) {
            $scope.selectDialog={
                visible: false,
                possibleValues:[]
            }

            $scope.pagingConfig={
                itemsPerPage: 5,
                currentPage: 1
            };

            $scope.loadPossibleValues =function(){
                $resource('../applicationServer/possibleValues', {id:$scope.factoryid, attributeName: $scope.attributename}).query(function(response){
                    $scope.selectDialog.possibleValues=response;
                })
            }

            $scope.addFactory=function(id){
                if (Array.isArray($scope.attribute)){
                    $scope.attribute.push({id: id})
                } else {
                    $scope.attribute={id: id};
                }
            }

        }]
    };
});