app.controller('profileCtrl', ['$scope', '$route','auth', 'crud', function ($scope, $route, auth, crud) {
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
        var file = $(this).get(0).files[0];
        var reader = new FileReader();
        reader.onload = function(readerEvt) {
            var binaryString = readerEvt.target.result;
            var encodedData = btoa(binaryString);
            crud.update('/user', {PROFILE: encodedData})
                .then(function success() {
                    $scope.msg = "Uploaded!";
                }, function () {
                    $scope.msg = "Not Uploaded.";
                })
        };
        reader.readAsBinaryString(file);
    });


    $scope.upload = function () {
        var fileInput = angular.element(document.querySelector('#profilePic'));
        fileInput.click();
    };

}]);

