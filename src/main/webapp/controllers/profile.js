app.controller('profileCtrl', ['$scope', '$route', 'auth',
    '$http', '$log', '$location', '$mdToast', '$routeParams', 'crud',
    function ($scope, $route, auth, $http, $log, $location, $mdToast, $routeParams, crud) {
        'use strict';
        $scope.subText = 'Subscribe';
        $scope.status = '  ';
        $scope.message = "Profile Ctrl Active";
        $scope.user = null;
        $scope.goToSettings = function () {
            $route.path('/settings');
        };

        $scope.$on('$routeChangeSuccess', function (scope, next, current) {
            if($routeParams.artistName === "self"){
                $scope.user = auth.getUser();
                crud.update('/comic', {REQUEST:'GET_COMIC_LIST_DEFAULT', USER_GMAIL:$scope.user.GMAIL})
                    .then(function (resp) {
                            $scope.comics = resp.data.COMICS;
                        }, function () {
                            $scope.comics = [];
                        }
                    );
                crud.update('/comic', {REQUEST:'GET_SUBSCRIPTION_LIST'})
                    .then(function (resp) {
                            $scope.subs = resp.data.SUBSCRIPTIONS;
                        }, function () {
                            $scope.subs = [];
                        }
                    );
                if ($scope.user === undefined || $scope.user === null) {
                    $location.path(current.$$route.originalPath);
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent('You must log in to view your profile')
                            .action('OK')
                            .position('top right')
                            .hideDelay(3000)
                    );
                    return;
                }
                $scope.me = true;
            }else{
                auth.getUserByGmail(atob($routeParams.artistName))
                    .then(function (user) {
                        $scope.user = user.USER;
                        $scope.me = (auth.getUser().GMAIL ===  $scope.user.GMAIL);
                        crud.update('/comic', {REQUEST:'GET_COMIC_LIST_DEFAULT', USER_GMAIL:$scope.user.GMAIL})
                            .then(function (resp) {
                                    $scope.comics = resp.data.COMICS;
                                }, function () {
                                    $scope.comics = [];
                                }
                            );
                        crud.update('/user', {REQUEST: 'IS_USER_SUBSCRIBED', USER_GMAIL:$scope.user.GMAIL})
                            .then(
                                function yes() {
                                    $scope.subText = 'Unsubscribe';
                                },
                                function no() {
                                    $scope.subText = 'Subscribe';
                                }
                            )
                    }, function (resp) {
                        $scope.user = 'no ' + resp;
                    });
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


        $scope.subscribe = function () {
            if($scope.subText === 'Subscribe'){
                crud.update('/user', {REQUEST: 'SUBSCRIBE', USER_GMAIL:$scope.user.GMAIL})
                    .then(
                        function yes() {
                            $scope.subText = 'Unsubscribe';
                        },
                        function no() {
                            $scope.subText = 'Subscribe';
                            $mdToast.show(
                                $mdToast.simple()
                                    .textContent('Unable to subscribe')
                                    .action('OK')
                                    .position('top right')
                                    .hideDelay(3000)
                            )
                        });
            }else{
                crud.update('/user', {REQUEST: 'UNSUBSCRIBE', USER_GMAIL:$scope.user.GMAIL})
                    .then(
                        function yes() {
                            $scope.subText = 'Subscribe';
                        },
                        function no() {
                            $scope.subText = 'Unsubscribe';
                            $mdToast.show(
                                $mdToast.simple()
                                    .textContent('Unable to unsubscribe')
                                    .action('OK')
                                    .position('top right')
                                    .hideDelay(3000)
                            )
                        });
            }
        };

        $scope.upload = function () {
            var fileInput = angular.element(document.querySelector('#profilePic'));
            fileInput.click();
        };



    }]);

