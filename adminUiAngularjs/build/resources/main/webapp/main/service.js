'use strict';

/* jhint helpers: */
/*global moment */
/*global angular */
/*global alert */

angular.module('factoryfxwebgui.services', ['ngResource'])

.service('metaDataService',
    ["$resource", "$q", function($resource, $q) {
        var metaDataService = {};

        metaDataService.update= function(){
            if (metaDataService.data){
                return $q.when(metaDataService);
            }

            return $resource('../applicationServer/metaData').get(function (response) {
                metaDataService.data=response;
            }).$promise;
        };

        return metaDataService;
    }]
)

.service('guiModelService',
    ["$resource", "$q", function($resource, $q) {
        var guiModelService = {};

        guiModelService.update= function(){
            if (guiModelService.data){
                return $q.when(guiModelService);
            }

            return $resource('../applicationServer/guimodel').get(function (response) {
                guiModelService.data=response;
            }).$promise;
        };

        return guiModelService;
    }]
)

.factory('errorDataService', ["$rootScope", function($rootScope) {
    var result={
        errorResponse : null,
        previousPath: null
    };
    $rootScope.$on("$locationChangeStart", function(e, currentLocation, previousLocation){
        result.previousPath = previousLocation;
    });

    return result;
}]);