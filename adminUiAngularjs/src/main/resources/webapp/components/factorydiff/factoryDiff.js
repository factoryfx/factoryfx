'use strict';

/* jhint helpers: */
/*global moment */
/*global angular */
/*global Pikaday */
/*global alert */


angular.module('factoryfxwebgui.directives')//re-open module definition

.directive('factorydiff',function () {
    return {
        restrict: 'EA',
        scope: {
            mergeDiff: '=mergeDiff',
            guiModel: '=guiModel'

        },
        templateUrl: 'components/factorydiff/factorydiff.html',
        replace: true,

        controller: ["$scope", "$resource", "$sce", function ($scope,$resource,$sce) {
            $scope.selected={
                diffdetail: undefined
            };
            $scope.getDiffDetail=function(mergeResultEntryInfo){
                $scope.selected.info=mergeResultEntryInfo;
                return $resource('../applicationServer/diffdetail').save(mergeResultEntryInfo,function(response){
                    $scope.selected.diffdetail=response.text;
                });
            };

            $scope.resetSelected=function(){
                $scope.selected.diffdetail=undefined;
                $scope.selected.info=undefined;
            };
            $scope.$watch('mergeDiff',function(newValue,oldvalue) {
                $scope.resetSelected();
            });

            
        }]
    };
});