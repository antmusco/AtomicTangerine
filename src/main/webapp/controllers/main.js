app.controller('mainCtrl', ['$scope', '$timeout', '$http', '$log', '$location', 'auth', '$q',
    function ($scope, $timeout, $http, $log, $location, auth, $q) {
        'use strict';

        var self = this;
        $scope.selectedItem;
        self.items = [];
        self.query = query;

        function query(searchQuery) {
            var later = $q.defer();
            $http.post('/search', {REQUEST: 'SEARCH_ALL', SEARCH_KEY: searchQuery})
                .then(
                    function yes(resp) {
                        var data = resp.data;
                        var comicArray = data.COMICS;
                        var userArray = data.USERS;

                        var searchResults = [];

                        for (var i = 0; i < comicArray.length; i++){
                            var comic = comicArray[i];
                            comic.RESULT_TYPE = "COMIC";
                            comic.display = comic.TITLE;
                            searchResults.push(comic);
                        }

                        for (var i = 0; i < userArray.length; i++){
                            var user = userArray[i];
                            user.RESULT_TYPE = "USER";
                            user.display = user.GMAIL;
                            searchResults.push(user);
                        }

                        $log.info(searchResults);



                        later.resolve(searchResults);
                    },
                    function no(resp) {
                        var data = resp.data;
                        $log.error(data);
                        later.reject();
                    }
                );
            return later.promise;
        }

        $scope.favIcon = 'favorite_border';
        $scope.n = true;

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
                if($scope.comics == undefined) return;
                $scope.counter = $scope.comics.length - 1;
                $scope.currentComic = $scope.comics[$scope.counter];
                auth.getUserByGmail($scope.currentComic.USER_GMAIL)
                    .then(function (user) {
                        $scope.artist = user.USER;
                    }, function (resp) {
                        $scope.artist = 'no ' + resp;
                    });
                $scope.next = function () {
                    $scope.counter = ($scope.counter + 1) % $scope.comics.length;
                    $scope.currentComic = $scope.comics[$scope.counter];
                    auth.getUserByGmail($scope.currentComic.USER_GMAIL)
                        .then(function (user) {
                            $scope.artist = user.USER;
                        }, function (resp) {
                            $scope.artist = 'no ' + resp;
                        });
                    $scope.updateComments();
                };

                $scope.prev = function () {
                    if($scope.counter > 0) {
                        $scope.counter = ($scope.counter - 1) % $scope.comics.length;
                    } else {
                        $scope.counter = $scope.comics.length - 1;
                    }
                    $scope.currentComic = $scope.comics[$scope.counter];
                    auth.getUserByGmail($scope.currentComic.USER_GMAIL)
                        .then(function (user) {
                            $scope.artist = user.USER;
                        }, function (resp) {
                            $scope.artist = 'no ' + resp;
                        });
                    $scope.updateComments();
                };

                $scope.goToProfile = function () {
                    $location.path('/profile/' + btoa($scope.artist.GMAIL));
                };

                $scope.upVote = function () {
                    $scope.n = !$scope.n;
                    if($scope.n){
                        $scope.favIcon = 'favorite_border';
                    }else{
                        $scope.favIcon = 'favorite';
                    }
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