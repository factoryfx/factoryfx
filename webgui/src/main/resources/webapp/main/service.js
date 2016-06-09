
angular.module('factoryfxwebgui.services', ['ngResource'])

.service('metaDataService',
    function($resource, $q) {
        var metaDataService = {};

        metaDataService.update= function(){
            if (metaDataService.userDataloaded){
                return $q.when(metaDataService);
            }

            return $resource('../applicationServer/metaData').get(function (response) {
                metaDataService.data=response;
            }).$promise;
        };

        return metaDataService;
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