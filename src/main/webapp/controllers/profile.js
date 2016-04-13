app.controller('profileCtrl', ['$scope', '$route','crud', function ($scope, $route,crud) {
    'use strict';
    $scope.status = '  ';
    $scope.message = "Profile Ctrl Active";
    $scope.user;
    $scope.goToSettings = function () {
        $route.path('/settings');
    };

    $scope.$on('$routeChangeSuccess', function(scope, next, current){
        $scope.displayUser();
    });

    $scope.displayUser = function () {
        crud.retrieve('/user')
            .then(function success(data) {
                if (data.USER !== null) {
                    $scope.user = data.USER;
                    $scope.user.birthday = new Date($scope.user.birthday);
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
}]);

