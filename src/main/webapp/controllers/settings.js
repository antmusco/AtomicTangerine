app.controller('settingsCtrl', ['$scope', '$http', '$rootScope', 'crud', function ($scope, $http, $rootScope, crud) {
    'use strict';
    $scope.$on('$routeChangeSuccess', function(scope, next, current){
        $scope.retrieveUserSettings();
    });

    $scope.jobDesc = 'Atomic Artist';
    $scope.user = {
        title: 'Atomic Artist',
        gmail: '',
        firstName: '',
        lastName: '',
        birthday: '',
        biography: ''
    };
    $scope.init = function () {

    };

    $scope.retrieveUserSettings = function () {
        crud.retrieve('/user')
            .then(function success(data) {
                if (data.USER !== null) {
                    $scope.user = data.USER;
                }else{
                    $scope.error = "You don't seemed to be logged in";
                }
            }, function error() {
                $scope.error = "Oh Jesus, take the wheel";
            })
            .catch(function () {
                $scope.error = "Why does the universe hate me";
            })
    };

    $scope.updateUserSettings = function () {
        crud.update('/user', $scope.user)
            .then(function success() {
                $scope.confirm = 'Saved your settings!';
            }, function error(resp) {
                $scope.confirm = 'Uh oh, maybe try again!' + resp.status;
            });
    };

    $scope.init();
}]);

