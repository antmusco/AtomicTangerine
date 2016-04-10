app.controller('settingsCtrl', ['$scope', '$http', '$rootScope', function ($scope, $http, $rootScope, $location) {
    'use strict';
    $scope.jobDesc = 'Atomic Artist';
    $scope.user = {
        title: 'Atomic Artist',
        email: '',
        firstName: '',
        lastName: '',
        biography: ''
    };
    $http.get('/user')
        .then(function success(resp) {
            if (resp.data.USER !== null) {// || resp.data.username = null) {
                $rootScope.user = resp.data.USER;
                $scope.user = resp.data.USER;
                //$location.path('/settings')
            } else {
                //$location.path('/main');
            }
        }, function error(resp) {
            $rootScope.err += 'No user: ' + resp.toString() + '\n';
        });
    $scope.saveSettings = function () {
        $http.put('/user', $scope.user)
            .then(function sucess(resp) {
                $scope.confirm = 'Saved!';
            }, function error(resp) {
                $scope.confirm = 'Uh oh!';
            });
    };
}]);

