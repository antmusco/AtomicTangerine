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
                $scope.user.PROFILE_PIC_URL = 'data:image/jpeg;base64, ' + $scope.user.PROFILE_PIC_URL;
            }, function error(msg) {
                $scope.msg = msg;
            });
    };

    // For some reason, this entire file was being loaded twice on the profile view.
    // So we have to unbind the old change method before we put in the new one
    // or else we have multiple action listeners doing the same thing.
    $('#profilePic').unbind('change').on('change', function () {
        if ($(this).get(0).files.length > 0) {
            var file = $(this).get(0).files[0];
            var info = 'profilepic/' + file.name;
            var reader = new FileReader();
            reader.onload = function (readerEvt) {
                var binaryString = readerEvt.target.result;
                console.log("File size: " + file.size)
                $http.post('/assets/' + info, binaryString)
                    .then(function success(resp) {
                        $scope.msg = 'Good';
                    }, function error(resp) {
                        $scope.msg = 'Bad';
                    });
            };
            reader.readAsBinaryString(file);
        }
    });


    $scope.upload = function () {
        var fileInput = angular.element(document.querySelector('#profilePic'));
        fileInput.click();
    };

}]);

