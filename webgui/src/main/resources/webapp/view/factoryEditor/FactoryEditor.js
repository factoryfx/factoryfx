'use strict';

/* jhint helpers: */
/*global moment */
/*global angular */
/*global alert */

var defaultResolve = {
    'userDataResolved': ['metaDataService','guiModelService','$q',
     function(            metaDataService,  guiModelService,  $q) {
        return $q.all([metaDataService.update(),guiModelService.update()]);
    }]
};

angular.module('factoryfxwebgui.factoryEditor', ['ngRoute'])

.filter("pagingFilter", function(){
    return function(input, pageSize, currentPage) {
        return input ?  input.slice((currentPage-1) * pageSize, (currentPage-1  + 1 ) * ( pageSize)) : [];
    };
})

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/factoryEditor', {
        templateUrl: 'view/factoryEditor/FactoryEditor.html',
        controller: 'GenericEditorController',
        resolve: defaultResolve
    });
}])

.controller('GenericEditorController', ['$scope','metaDataService','guiModelService', '$resource',
function                                ($scope,  metaDataService,  guiModelService,   $resource) {
    $scope.metaData = metaDataService.data;
    $scope.guiModel = guiModelService.data;

    $scope.pagingConfig={
        itemsPerPage: 5,
        currentPage: 1
    };
    
    $scope.selected={
        factory: undefined
    };

    $scope.loadRoot=function() {
        $scope.stagedChanges=false;
        $scope.deployResponse=null;
        return $resource('../applicationServer/root').get(function (response) {
            $scope.selected.factory = response.toJSON();
            $scope.resetDirtyTracking();
        }).$promise;
    };

    $scope.resetDirtyTracking = function(){
        $scope.mapCache={};
        $scope.selected.originalFactory={};
        angular.copy($scope.selected.factory,$scope.selected.originalFactory);
    };

    $scope.selectFactory=function(id){
        $scope.selected.factory=null;
        return $resource('../applicationServer/factory', {id:id}).get(function(response){
            $scope.selected.factory=response;
            $scope.resetDirtyTracking();
        }).$promise;
    };

    $scope.stagedChanges=false;
    $scope.save=function(){
        return $resource('../applicationServer/factory').save($scope.selected.factory, function(response){
            $scope.stagedChanges=true;
            $scope.resetDirtyTracking();
            $scope.deployResponse=response;
        }).$promise;
    };

    $scope.reset=function(){
        $scope.selected.factory=angular.copy($scope.selected.originalFactory);
    };

    $scope.deploy=function(){
        $scope.selected.factory=null;
        return $resource('../applicationServer/deploy').get(function(response){
            $scope.deployResponse=response;
        }).$promise;
    };
    $scope.isDirty=function(){
        return !angular.equals($scope.selected.factory,$scope.selected.originalFactory);
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

    $scope.initializingEditing=true;
    $resource('../applicationServer/loadCurrentFactory').get(function(response){
        $scope.loadRoot().then(function(result) {
            $scope.initializingEditing=false;
            $scope.save();//show previous chnages storedin session
            return result;
        });
    });

    $scope.factory={
        form: {}
    };
    $scope.auto={
        staging: true
    };
    $scope.$watch('selected.factory',function(newValue,oldvalue) {
        if($scope.factory.form && newValue && $scope.factory.form.$valid && $scope.auto.staging) {
            $scope.save();
        }

    },true);

    //cause angularjs limitation https://github.com/angular/angular.js/issues/2694#issuecomment-71328638
    $scope.mapCache={};
    $scope.getMap=function(attribute,attributeName){
        if ($scope.mapCache[attributeName]){
            return $scope.mapCache[attributeName];
        }
        var result=[];
        if (attribute){
            for (var property in attribute) {
                if (attribute.hasOwnProperty(property)) {
                    result.push({key: property,value:attribute[property]});
                }
            }
            $scope.mapCache[attributeName]=result;
        }
        return result;
    };
    $scope.deleteMapItem=function(key,attribute,attributeName){
        delete attribute[key];
        $scope.mapCache[attributeName]=undefined;//reset cache
        $scope.getMap(attribute,attributeName);
    };
    
}]);