
angular.module('factoryfxwebgui.services', ['ngResource'])

.service('metaDataService',
    function($resource, $q) {
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
    }
)

.service('guiModelService',
    function($resource, $q) {
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
    }
)

.factory('errorDataService', function($rootScope) {
    var result={
        errorResponse : null,
        previousPath: null
    };
    $rootScope.$on("$locationChangeStart", function(e, currentLocation, previousLocation){
        result.previousPath = previousLocation;
    });

    return result;
});