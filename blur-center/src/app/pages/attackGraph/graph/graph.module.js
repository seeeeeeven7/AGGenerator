/**
 * Created by Nettle on 2017/4/4.
 */

(function () {
    'use strict';

    angular.module('BlurAdmin.pages.attackGraph.graph', [])
        .config(routeConfig)
        .controller('graphCtrl', function ($scope, $http, $state) {
        });

    /** @ngInject */
    function routeConfig($stateProvider) {
        $stateProvider
            .state('attackGraph.graph', {
                url: '/graph',
                templateUrl: 'app/pages/attackGraph/graph/graph.html',
                title: '攻击图',
                sidebarMeta: {
                    order: 1,
                }
            });
    }

})();