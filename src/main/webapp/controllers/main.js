app.controller('mainCtrl', ['$scope', '$timeout', '$http', '$log', '$sce', 'auth',
    function ($scope, $timeout, $http, $log, $sce, auth) {
        'use strict';

        $scope.$on('$routeChangeSuccess', function (scope, next, current) {

        });

        $scope.loginAjax.promise.then(function() {
            $scope.user = auth.getUser();
            $scope.userIsLoggedIn = ($scope.user !== undefined && $scope.user !== null);
        });


        $http.post('/comic', {REQUEST: 'GET_COMIC_LIST_DEFAULT', DATE_CREATED: (new Date()).getTime() })
            .then(function (resp) {
                $scope.comics = resp.data.COMICS;
                $scope.message = "Main Ctrl Active ----- !";
                $scope.counter = $scope.comics.length - 1;
                $scope.currentComic = $scope.comics[$scope.counter];
                $scope.currentComicSvg = $sce.trustAsHtml($scope.currentComic.SVG_DATA);
                $scope.next = function () {
                    $scope.counter = ($scope.counter + 1) % $scope.comics.length;
                    $scope.currentComic = $scope.comics[$scope.counter];
                    $scope.currentComicSvg = $sce.trustAsHtml($scope.currentComic.SVG_DATA);
                };

                $scope.prev = function () {
                    $scope.counter = ($scope.counter - 1) % $scope.comics.length;
                    $scope.currentComic = $scope.comics[$scope.counter];
                    $scope.currentComicSvg = $sce.trustAsHtml($scope.currentComic.SVG_DATA);
                };

                $log.info(resp);
            }, function (resp) {
                $log.info(resp);
            });

        $("#submitCommentButton").click(function() {
            $http.put('/comment',
                {
                    COMMENTOR_GMAIL: $scope.user.GMAIL,
                    USER_GMAIL: $scope.currentComic.USER_GMAIL,
                    TITLE: $scope.currentComic.TITLE,
                    COMMENT: $scope.newComment
                })
        });

    }
]);