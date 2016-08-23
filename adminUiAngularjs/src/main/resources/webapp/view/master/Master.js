'use strict';

/* jhint helpers: */
/* jshint -W097 */
/*global moment */
/*global angular */
/*global alert */

angular.module('factoryfxwebgui.navigationBar', ['ngRoute'])

.controller('MasterController', ['$scope', '$resource', '$location', 'guiModelService',
function                         ($scope,   $resource,   $location,   guiModelService ) {
    guiModelService.update().then(function(result) {
        $scope.guiModel=guiModelService.data;
        return result;
    });

    $scope.views=$resource('../applicationServer/views').query();


    $scope.$location=$location;
}]);