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
        reloadOnSearch: false,
        resolve: defaultResolve
    });
}])

.controller('GenericEditorController', ['$scope','metaDataService','guiModelService', '$resource', '$location',
function                                ($scope,  metaDataService,  guiModelService,   $resource,   $location) {
    $scope.metaData = metaDataService.data;
    $scope.guiModel = guiModelService.data;

    $scope.pagingConfig = {
        itemsPerPage: 5,
        currentPage: 1
    };

    $scope.selected = {
        factory: undefined
    };

    $scope.loadRoot = function () {
        $scope.stagedChanges = false;
        $scope.deployResponse = null;
        return $resource('../applicationServer/root').get(function (response) {
            $scope.selected.factory = response.toJSON();
            $scope.resetDirtyTracking();
        }).$promise;
    };

    $scope.resetDirtyTracking = function () {
        $scope.mapCache = {};
        $scope.selected.originalFactory = {};
        angular.copy($scope.selected.factory, $scope.selected.originalFactory);
    };

    $scope.selectFactory = function (id) {
        $scope.selected.factory = null;
        return $resource('../applicationServer/factory', {'id': id}).get(function (response) {
            $scope.selected.factory = response;
            $scope.resetDirtyTracking();
            $location.search('id', id);
        }).$promise;

    };

    $scope.stagedChanges = false;
    $scope.save = function () {
        return $resource('../applicationServer/factory').save({factory: $scope.selected.factory.factory}, function (response) {
            $scope.stagedChanges = true;
            $scope.resetDirtyTracking();
            $scope.deployResponse = response;
        }).$promise;
    };

    $scope.reset = function () {
        $scope.selected.factory = angular.copy($scope.selected.originalFactory);
    };

    $scope.deploy = function () {
        $scope.selected.factory = null;
        return $resource('../applicationServer/deploy').get(function (response) {
            $scope.deployResponse = response;
        }).$promise;
    };

    $scope.deployReset = function () {
        return $resource('../applicationServer/deployReset').get(function (response) {
            $scope.deployResponse = response;
            if ($scope.selected.factory.factory && $scope.selected.factory.factory.id) {
                $scope.selectFactory($scope.selected.factory.factory.id);
            }
        }).$promise;
    };

    $scope.isDirty = function () {
        return !angular.equals($scope.selected.factory, $scope.selected.originalFactory);
    };

    $scope.getInputCssClass = function (error) {
        for (var prop in error) {
            if (error.hasOwnProperty(prop)) {
                if (error[prop]) {
                    return 'has-error';
                }
            }
        }
        return '';
    };

    $scope.initializingEditing = true;
    if ($location.search().id) {
        $scope.initializingEditing = false;
        $scope.selectFactory($location.search().id);
    } else {
        $resource('../applicationServer/loadCurrentFactory').get(function (response) {
            $scope.loadRoot().then(function (result) {
                $scope.initializingEditing = false;
                $scope.save();//show previous changes store in session
                return result;
            });
        });
    }

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

    $scope.getNestedFactoriesDisplayText=function(item){
        if (!item){
            return "<empty>";
        }
        var nestedFactoriesDisplayText = $scope.selected.factory.nestedFactoriesDisplayText[item.id];
        if (!nestedFactoriesDisplayText){
            nestedFactoriesDisplayText = $scope.selected.factory.nestedFactoriesDisplayText[item];
        }
        return nestedFactoriesDisplayText;
    };

    //jackson serialise the first object ref as object and other only as id
    //if we delete the first object we must search for other ids else the object and reference is lost
    $scope.deleteReference=function(attributeName){
        if (!$scope.selected.factory.factory[attributeName].id){
            $scope.selected.factory.factory[attributeName]=null;
        } else {
            var id=$scope.selected.factory.factory[attributeName].id;
            var removed=$scope.selected.factory.factory[attributeName];
            $scope.selected.factory.factory[attributeName]=null;
            for (var attribute in $scope.selected.factory.factory) {
                if (attribute!==attributeName){
                    if ($scope.selected.factory.factory.hasOwnProperty(attribute)) {
                        var attributeValue=$scope.selected.factory.factory[attribute].list;
                        if (Array.isArray(attributeValue)){
                            for (var i = 0; i < attributeValue.length; i++) {
                                if (attributeValue[i]===id){
                                    $scope.selected.factory.factory[attribute].list.splice( i, 1 );
                                    $scope.selected.factory.factory[attribute].list.push(removed);
                                    return;
                                }

                            }
                        }
                    }
                }
            }
        }
    };
    $scope.deleteListReference=function(attributeName,item){
        if (!item.id){
            $scope.selected.factory.factory[attributeName].splice($scope.selected.factory.factory[attributeName].indexOf(item),1);
        } else {
            //TODO same problem as deleteReference try to unify?
            $scope.selected.factory.factory[attributeName].splice($scope.selected.factory.factory[attributeName].indexOf(item),1);
        }
    };

    $scope.$on('$routeUpdate', function(){
        var factoryId=$location.search().id;
        if (factoryId){
            if ($scope.selected.factory && $scope.selected.factory.id!==factoryId){
                $scope.selectFactory(factoryId);
            }
        } else {
            $scope.loadRoot();
        }

    });


    
}]);