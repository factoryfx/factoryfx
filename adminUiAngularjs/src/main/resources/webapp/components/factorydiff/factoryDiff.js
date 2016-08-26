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
        templateUrl: 'components/factorydiff/factoryDiff.html',
        replace: true,

        controller: ["$scope", "$resource", function ($scope,$resource) {
            


        }]
    };
});