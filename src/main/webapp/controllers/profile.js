app.controller('profileCtrl', ['$scope', '$route','auth', function ($scope, $route, auth) {
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
        auth.getUser()
            .then(function success(user) {
                $scope.user = user;
            }, function error(msg) {
                $scope.msg = msg;
            });
    };
}]);

