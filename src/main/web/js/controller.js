'use strict';

/* Controllers */

var infoScreenApp = angular.module('infoScreenApp', []);

infoScreenApp.config(['$sceDelegateProvider', function ($sceDelegateProvider) {
    $sceDelegateProvider.resourceUrlWhitelist([
        'self',
        'http://fahrplan.oebb.at/bin/stboard.exe/**',
        'http://www.daswetter.com/getwid/**'
    ]);
}]);

infoScreenApp.controller('InfoScreenController', ['$scope', '$http', '$interval',
    function ($scope, $http, $interval) {
        $http.get('config').success(function (data) {
            $scope.config = data;
            $interval($scope.reloadData, 10000);
            $interval($scope.reloadWebcamPreview, 1000);
        });

        $scope.off = function () {
            $http.get('off');
        };

        $scope.loadWlData = function () {
            $http.get("wl").success(function (data) {
                $scope.wlLines = data.lines;
            });
        };
        $scope.loadCbwData = function () {
            $http.get("cbw").success(function (data) {
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
        $scope.reloadWebcamPreview = function () {
            if ($scope.isWebcam()) {
                $scope.webcamPreviewSrc = '/webcam/preview?' + (new Date().getTime());
            }
        };

        $scope.setTime = function () {
            $scope.time = moment().format('HH:mm:ss');
        };
        $interval($scope.setTime, 250);

        $scope.visibleTab = 'oebb';
        $scope.time = '';
        $scope.webcamPreviewSrc = 'data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==';
        $scope.webcamAlarm = 'error';
        $scope.loadWebcamAlarm = function() {
            $http.get('webcam/status').success(function(data) {
                $scope.webcamAlarm = data.status;
            });
        }
        $scope.loadWebcamAlarm();
        $scope.webcamAlarmToggle = function() {
            if ($scope.webcamAlarm == 'error') {
                $scope.loadWebcamAlarm();
            } else if ($scope.webcamAlarm == 'enabled') {
                $http.get('webcam/disable').success(function() {
                    $scope.loadWebcamAlarm();
                });
            } else if ($scope.webcamAlarm == 'disabled') {
                $http.get('webcam/enable').success(function() {
                    $scope.loadWebcamAlarm();
                });
            }
        }

        $scope.isOebb = function (index) {
            return $scope.visibleTab == 'oebb' + index
        };
        $scope.isWl = function () {
            return $scope.visibleTab == 'wl'
        };
        $scope.isWeather = function () {
            return $scope.visibleTab == 'weather'
        };
        $scope.isCbw = function () {
            return $scope.visibleTab == 'cbw'
        };
        $scope.isWebcam = function () {
            return $scope.visibleTab == 'webcam'
        };


        $scope.showOebb = function (index) {
            $scope.visibleTab = 'oebb' + index
        };
        $scope.showWl = function () {
            $scope.visibleTab = 'wl';
            $scope.loadWlData();
        };
        $scope.showWeather = function () {
            $scope.visibleTab = 'weather'
        };
        $scope.showCbw = function () {
            $scope.visibleTab = 'cbw';
            $scope.loadCbwData();
        };
        $scope.showWebcam = function () {
            $scope.visibleTab = 'webcam';
            $scope.loadCbwData();
        };
    }]);
