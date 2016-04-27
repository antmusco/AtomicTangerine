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

    $http.get("/assets")
        .then(function success(resp) {
            $scope.uploadUrl = resp.data.uploadUrl;
        }, function error (resp) {
            console.log(resp);
        });

    $('#profilePic').unbind('change').on('change', function () {
        if ($(this).get(0).files.length > 0) {
            var file = $(this).get(0).files[0];
            var reader = new FileReader();
            reader.onload = function (readerEvt) {
                $("#submissionType").val("PROFILE_PIC");
                $("#redirectAddress").val("/#/profile");
                $("#profilePicForm").submit();
            };
            reader.readAsBinaryString(file);
        }
    });


    $scope.upload = function () {
        var fileInput = angular.element(document.querySelector('#profilePic'));
        fileInput.click();
    };

}]);

