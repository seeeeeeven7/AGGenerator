/**
 * Created by Nettle on 2017/1/18.
 */

(function () {
    'use strict';

    angular.module('BlurAdmin.pages.dashboard')
        .controller('DashboardNetworkLineChartCtrl', DashboardNetworkLineChartCtrl);

    /** @ngInject */
    function DashboardNetworkLineChartCtrl($scope,$filter,$http,$timeout) {
        function mySort(a, b) {
            return a.t > b.t;
        }
        $timeout(function () {
            $http
                .get('http://162.105.30.71:9016/networks/'+$scope.id)
                .then(function (result) {
                    console.log(result);
                    $scope.networkName = result.data.name;
                    $http
                        .get('http://162.105.30.71:9016/networks/'+$scope.id+'/scores')
                        .then(function (result) {
                            console.log(result);
                            $scope.lineData = [];
                            for (var key in result.data) {
                                result.data[key].y = new $filter('date')(parseInt(key),'yyyy-MM-dd HH:mm:ss');
                                result.data[key].t = parseInt(key);
                                $scope.lineData.push(result.data[key]);
                            }
                            $scope.lineData.sort(mySort);
                            $scope.series = [];
                            $scope.radarData = [];
                            for (var key in $scope.lineData[0])
                                if (key != 'y' && key != 't') {
                                    $scope.series.push(key);
                                    $scope.radarData.push($scope.lineData[$scope.lineData.length - 1][key]);
                                }

                            console.log($scope.lineData);
                        }, function (result) {
                            console.error('error');
                        });
                }, function (result) {
                    console.error('error');
                });
        }, 1000);
    }

})();