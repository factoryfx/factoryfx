'use strict';

/* jhint helpers: */
/* jshint -W097 */
/*global moment */
/*global angular */
/*global alert */

angular.module('factoryfxwebgui.navigationBar', ['ngRoute'])

    .controller('MasterController', ['$scope', '$resource', '$location',
        function                         ($scope,   $resource,   $location ) {
            $scope.guiModel=$resource("../applicationServer/guimodel").get();
            //TODO load only once

            $scope.$location=$location;
        }]);