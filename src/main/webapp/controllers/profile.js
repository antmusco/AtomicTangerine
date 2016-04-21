app.controller('profileCtrl', ['$scope', '$route','auth', '$http' , function ($scope, $route, auth, $http) {
    'use strict';
    $scope.status = '  ';
    $scope.message = "Profile Ctrl Active";
    $scope.user = null;
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


    $('#profilePic').on('change', function () {
        var file = $(this).get(0).files[0];
        var info = 'profilepic/' + file.name;
        var reader = new FileReader();
        reader.onload = function(readerEvt) {
            var binaryString = readerEvt.target.result;
            $http.post('/assets/' + info,  binaryString)
                .then(function success(resp) {
                    $scope.msg = 'Good';
                }, function error(resp) {
                    $scope.msg = 'Bad';
                });
        };
        reader.readAsBinaryString(file);
    });


    $scope.upload = function () {
        var fileInput = angular.element(document.querySelector('#profilePic'));
        fileInput.click();
    };

}]);

