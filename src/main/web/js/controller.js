'use strict';

/* Controllers */

var infoScreenApp = angular.module('infoScreenApp', []);

infoScreenApp.config(['$sceDelegateProvider', function($sceDelegateProvider) {
  $sceDelegateProvider.resourceUrlWhitelist([
    'self',
    'http://fahrplan.oebb.at/bin/stboard.exe/**',
    'http://www.daswetter.com/getwid/**'
  ]);
}]);

infoScreenApp.controller('InfoScreenController', ['$scope', '$http',
  function($scope, $http) {
    $http.get('config').success(function(data) {
      $scope.config = data;
      setInterval($scope.reloadData, 30000);
    });

    $scope.off = function () {
      $http.get('off');
    };

    $scope.loadWlData = function () {
      $http.get("wl").success(function(data) {
        $scope.wlLines = data.lines;
      });
    };
    $scope.loadCbwData = function () {
      $http.get("cbw").success(function(data) {
        $scope.cbwStations = data.stations;
      })
    };

    $scope.reloadData = function () {
      if ($scope.isCbw()) {
        $scope.loadCbwData();
      } else if ($scope.isWl()) {
        $scope.loadWlData();
      }
    };

    $scope.visibleTab = 'oebb';

    $scope.isOebb = function () { return $scope.visibleTab == 'oebb'};
    $scope.isWl = function () { return $scope.visibleTab == 'wl'};
    $scope.isWeather= function () { return $scope.visibleTab == 'weather'};
    $scope.isCbw= function () { return $scope.visibleTab == 'cbw'};

    $scope.showOebb = function() { $scope.visibleTab = 'oebb'};
    $scope.showWl = function() {
      $scope.visibleTab = 'wl';
      $scope.loadWlData();
    };
    $scope.showWeather = function () { $scope.visibleTab = 'weather' };
    $scope.showCbw = function () {
      $scope.visibleTab = 'cbw';
      $scope.loadCbwData();
    };
  }]);
