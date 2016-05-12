app.controller('mainCtrl', ['$scope', '$timeout', '$http', '$log', '$sce', 'auth',
    function ($scope, $timeout, $http, $log, $sce, auth) {
        'use strict';

        $scope.$on('$routeChangeSuccess', function (scope, next, current) {

        });

        $scope.loginAjax.promise.then(function() {
            $scope.user = auth.getUser();
            $scope.userIsLoggedIn = ($scope.user !== undefined && $scope.user !== null);
        });

        $scope.commentList = [];

        $http.post('/comic', {REQUEST: 'GET_COMIC_LIST_DEFAULT', DATE_CREATED: (new Date()).getTime() })
            .then(function (resp) {

                $scope.updateComments = function() {
                    $http.post('/comment',
                        {
                            REQUEST: 'GET_COMMENTS_FOR_COMIC',
                            USER_GMAIL: $scope.currentComic.USER_GMAIL,
                            TITLE: $scope.currentComic.TITLE
                        })
                        .then(function(resp) {
                            $scope.commentList = resp.data.COMMENTS;
                        });
                };

                $scope.comics = resp.data.COMICS;
                $scope.message = "Main Ctrl Active ----- !";
                $scope.counter = $scope.comics.length - 1;
                $scope.currentComic = $scope.comics[$scope.counter];
                $scope.currentComicSvg = $sce.trustAsHtml($scope.currentComic.SVG_DATA);
                $scope.next = function () {
                    $scope.counter = ($scope.counter + 1) % $scope.comics.length;
                    $scope.currentComic = $scope.comics[$scope.counter];
                    $scope.currentComicSvg = $sce.trustAsHtml($scope.currentComic.SVG_DATA);
                    $scope.updateComments();
                };

                $scope.prev = function () {
                    if($scope.counter > 0) {
                        $scope.counter = ($scope.counter - 1) % $scope.comics.length;
                    } else {
                        $scope.counter = $scope.comics.length - 1;
                    }
                    $scope.currentComic = $scope.comics[$scope.counter];
                    $scope.currentComicSvg = $sce.trustAsHtml($scope.currentComic.SVG_DATA);
                    $scope.updateComments();
                };
                $scope.updateComments();


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
                }).then(function (resp) {
                    $scope.newComment = '';
                    $scope.updateComments();
                }
            );
        });

    }
]);