app.controller('mainCtrl', ['$scope', '$timeout', function ($scope, $timeout) {
    'use strict';

    $scope.$on('$routeChangeSuccess', function(scope, next, current){
        $timeout(function () {
            $scope.message = "Main Ctrl Active";
        }, 3000);
    });

    $scope.message = "Main Ctrl Active ----- !";
    $scope.imagePath = "mock-up/img/comic3.png";
}]);