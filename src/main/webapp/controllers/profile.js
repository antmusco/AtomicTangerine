app.controller('profileCtrl', ['$scope', '$route','auth', '$http' , function ($scope, $route, auth, $http) {
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


    $('#profilePic').on('change', function () {
        var info = 'profilepic';
        var file = $(this).get(0).files[0];
        var reader = new FileReader();
        reader.onload = function(readerEvt) {
            var binaryString = readerEvt.target.result;
            // var encodedData = btoa(binaryString);
            $http.post('/assets/' + info, binaryString)
                .then(function success(resp) {
                    $scope.msg = resp;
                }, function error(resp) {
                    $scope.msg = resp;
                });
        };
        reader.readAsBinaryString(file);
    });


    $scope.upload = function () {
        var fileInput = angular.element(document.querySelector('#profilePic'));
        fileInput.click();
    };

}]);

