'use strict';

/* jhint helpers: */
/* jshint -W097 */
/*global moment */
/*global angular */
/*global alert */


angular.module('factoryfxwebgui.directives')//re-open module definition


.directive('waitingButton', function () {
    return {
        scope: {
                    disabled: '&',
                    click: '&', // function must return promise e.g for resource !!!!.$promise!!!! update(updatedData, function(response){...}).$promise;
                    text: '@',
                    bootstrapicon: '@',
                    bootsrapbtntype: '@'
               },
        restrict: 'E',
        templateUrl: 'components/waitingButton/waitingButton.html',
        replace: true,

        controller: ["$scope", "$timeout", function($scope, $timeout) {
            $scope.bootsrapbtntype = $scope.bootsrapbtntype || 'btn-default';

            $scope.isloading={
                value: false
            };

            $scope.executeClick = function(){
                $scope.isloading.value=true;
                var promise = $scope.click();
                if (promise){
                    promise.then(function(){
                        $scope.isloading.value=false;
                        // $scope.success=true;
                        // $timeout(function(){
                        //     $scope.success=false;
                        // },1000);
                        $scope.success=false;
                    });
                } else {
                    $scope.isloading.value=false;
                }
            };

            $scope.getButtonCssClass= function(){
                var result=$scope.bootsrapbtntype;
                if ($scope.success){
                    result= result + " btn-success";
                }
                return result;
            };
        }]

    };
});

