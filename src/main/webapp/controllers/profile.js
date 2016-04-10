app.controller('profileCtrl', ['$scope', '$route', function ($scope, $route) {
    'use strict';
    $scope.status = '  ';
    $scope.message = "Profile Ctrl Active";
    $scope.goToSettings = function () {
        $route.path('/settings');
    };
}]);

