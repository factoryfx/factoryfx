'use strict';

/* jhint helpers: */
/*global moment */
/*global angular */
/*global alert */

angular.module('factoryfxwebgui.directives', []);

angular.module('factoryfxwebgui.directives').directive('fileOnChange', function() {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var onChangeHandler = scope.$eval(attrs.fileOnChange);
            element.bind('change', onChangeHandler);
        }
    };
});