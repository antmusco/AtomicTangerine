app.controller('profileCtrl', ['$scope', '$route', 'auth',
    '$http', '$log', '$location', '$mdToast',
    function ($scope, $route, auth, $http, $log, $location, $mdToast) {
        'use strict';

        $scope.status = '  ';
        $scope.message = "Profile Ctrl Active";
        $scope.user = null;
        $scope.goToSettings = function () {
            $route.path('/settings');
        };

        $scope.$on('$routeChangeSuccess', function (scope, next, current) {
            $scope.user = auth.getUser();
            if($scope.user === undefined || $scope.user == null){
                $location.path(current.$$route.originalPath);
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('You must log in to view your profile')
                        .action('OK')
                        .position('top right')
                        .hideDelay(3000)
                );
            }
        });

        $http.get("/assets")
            .then(function success(resp) {
                $scope.uploadUrl = resp.data.uploadUrl;
            }, function error(resp) {
                $log.error(resp);
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

