'use strict';

/* jhint helpers: */
/* jshint -W097 */
/*global moment */
/*global angular */
/*global console */


angular.module('factoryfxwebgui.directives')//re-open module definition


.directive('valueeditor', function () {
    return {
        scope: {
                    datatype: '=',
                    model: '=',
                    formmodel: '=',
                    name: '=',
                    labelText: '=',
                    required: '='
               },
        restrict: 'E',
        templateUrl: 'components/valueEditor/valueEditor.html',
        replace: true,

        controller: ["$scope", "$timeout", function($scope, $timeout) {

            // $scope.model[$scope.name]="dgdgfdgfdgf";
            // $scope.modelValue=$scope.model[$scope.name];
            // $scope.$watch('modelValue', function(newValue) {
            //     console.log("dsfdsdfs");
            //     $scope.model[$scope.name]=newValue;
            // });

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


        }]

    };
});

