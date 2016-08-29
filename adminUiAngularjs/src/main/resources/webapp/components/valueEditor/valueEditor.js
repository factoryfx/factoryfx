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
                    addonText: '=',
                    hidelabel: '=',
                    required: '='
               },
        restrict: 'E',
        templateUrl: 'components/valueEditor/valueEditor.html',
        replace: true,

        controller: ["$scope", "$timeout", function($scope, $timeout) {

            //angularjs workaround for file type  http://stackoverflow.com/questions/17922557/angularjs-how-to-check-for-changes-in-file-input-fields
            $scope.handleFileSelect = function(event) {
                var file = event.target.files[0];
                if (file) {
                    var reader = new FileReader();

                    reader.onload = function(readerEvt) {
                        var binaryString = readerEvt.target.result;
                        $scope.$apply(function () {
                            $scope.model = btoa(binaryString);
                        });
                    };

                    reader.readAsBinaryString(file);
                    // if (file.name){
                        // $scope.selectedFile=file.name.slice(0, -4);
                    // }
                }
            };

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

