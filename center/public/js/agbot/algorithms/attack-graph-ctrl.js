/**
 * Created by Nettle on 2016/11/25.
 */

agbotApp.config(['$stateProvider', function($stateProvider) {
    $stateProvider.state('algorithm_attack_graph', {
        url: '/algorithms/{algorithm_id:[0-9]+}/attack_graph/{id:[0-9]+}',
        templateUrl: '/html/algorithms/attack_graph',
        controller: 'algorithmAttackGraphCtrl'
    });
}]).controller('algorithmAttackGraphCtrl', ['$scope', '$stateParams', '$http', '$q', function($scope, $stateParams, $http, $q) {
    // console.log($stateParams.id);
    $scope.id = $stateParams.id;
    $scope.algorithm_id = $stateParams.algorithm_id;

    if ($scope.algorithm_id == 3) {
        $http
            .get(['api', 'tasks', $stateParams.id].join('/'))
            .success(function (data) {
                $scope.input = JSON.parse(data.input);
                $scope.analysis = JSON.parse(JSON.parse(data.output).result);
                $http
                    .get(['api', 'tasks', $scope.input.result_id].join('/'))
                    .success(function (data) {
                        $scope.nodes = JSON.parse(data.output).nodes;
                        $scope.links = JSON.parse(data.output).edges;
                    })
            });
    }
    else {
        var request = [
            $http.get('/api/algorithms/' + $scope.algorithm_id),
            $http.get('/api/tasks/' + $scope.id)
        ];
        $q.all(request).then(function (result) {
            if (result[0].data.type == 1) {
                $scope.analysis = JSON.parse(result[1].data.output);
                $http.get(['api', 'algorithms', $scope.analysis.input.algorithm_id, 'results', $scope.analysis.input.result_id].join('/'))
                    .then(function (result) {
                        $scope.nodes = JSON.parse(result.data.output).nodes;
                        $scope.links = JSON.parse(result.data.output).edges;
                    });
            } else {
                $scope.nodes = JSON.parse(result[1].data.output).nodes;
                $scope.links = JSON.parse(result[1].data.output).edges;
            }
        });
    }
}]);