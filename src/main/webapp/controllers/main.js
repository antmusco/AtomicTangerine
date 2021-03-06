app.controller('mainCtrl', ['$scope', '$timeout', '$http', '$log', '$location', 'auth', '$q',
    function ($scope, $timeout, $http, $log, $location, auth, $q) {
        'use strict';

        var self = this;
        $scope.selectedItem;
        self.items = [];
        self.query = query;

        $scope.upthumbStyle = {};
        $scope.downthumbStyle = {};

        $scope.imgLoaded = false;

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
                            comic.resultType = "COMIC";
                            comic.mainText = comic.TITLE;
                            comic.subText = '';
                            searchResults.push(comic);
                        }

                        for (var i = 0; i < userArray.length; i++){
                            var user = userArray[i];
                            user.resultType = "USER";
                            user.mainText = user.HANDLE;
                            searchResults.push(user);
                        }

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

        self.evaluateSearchSelection = evaluateSearchSelection;

        function evaluateSearchSelection(selection) {
            if(selection === undefined || selection === null) return;
            if(selection.resultType === "USER") {
                if(selection.GMAIL === $scope.user.GMAIL) {
                    $location.path('/profile/self');
                } else {
                    $location.path('/profile/' + btoa(selection.GMAIL));
                }
            } else if (selection.resultType === "COMIC") {
                $scope.currentComic = selection;
            }
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
                $scope.imgLoaded = true;
                $scope.updateComments = function() {
                    $http.post('/comment',
                        {
                            REQUEST: 'GET_COMMENTS_FOR_COMIC',
                            USER_GMAIL: $scope.currentComic.USER_GMAIL,
                            TITLE: $scope.currentComic.TITLE
                        })
                        .then(function(resp) {
                            $scope.commentList = resp.data.COMMENTS;
                            $scope.commentList.sort(function(lhs, rhs) {
                                return rhs.SCORE - lhs.SCORE;
                            });
                            for(var i = 0; i < $scope.commentList.length; i++) {
                                $scope.commentList[i].DATE_POSTED = new Date($scope.commentList[i].DATE_POSTED);
                            }
                        });
                };



                $scope.comics = resp.data.COMICS;
                if($scope.comics == undefined) return;
                $scope.counter = $scope.comics.length - 1;
                $scope.currentComic = $scope.comics[$scope.counter];

                $scope.upthumbStyle = {};
                $scope.downthumbStyle = {};
                $http.post('/comic',
                    {REQUEST: 'GET_USER_VOTE',
                        TITLE: $scope.currentComic.TITLE,
                        USER_GMAIL: $scope.currentComic.USER_GMAIL}
                )
                    .then(function yes(resp) {
                        $scope.upthumbStyle = {};
                        $scope.downthumbStyle = {};
                        if(resp.data.VOTE === 'UPVOTE'){
                            $scope.upthumbStyle = {background: '#ab2323'};
                        }else if(resp.data.VOTE === 'DOWNVOTE'){
                            $scope.downthumbStyle = {background: '#ab2323'};
                        }
                    }, function no(resp) {
                        $log.error("Couldn't get voting, maybe they haven't voted yet. . .")
                    });

                auth.getUserByGmail($scope.currentComic.USER_GMAIL)
                    .then(function (user) {
                        $scope.artist = user.USER;
                    }, function (resp) {
                        $scope.artist = 'no ' + resp;
                    });
                $scope.next = function () {
                    $scope.upthumbStyle = {};
                    $scope.downthumbStyle = {};
                    $scope.counter = ($scope.counter + 1) % $scope.comics.length;
                    $scope.currentComic = $scope.comics[$scope.counter];
                    auth.getUserByGmail($scope.currentComic.USER_GMAIL)
                        .then(function (user) {
                            $scope.artist = user.USER;
                        }, function (resp) {
                            $scope.artist = 'no ' + resp;
                        });
                    $http.post('/comic',
                        {REQUEST: 'GET_USER_VOTE',
                        TITLE: $scope.currentComic.TITLE,
                        USER_GMAIL: $scope.currentComic.USER_GMAIL}
                    )
                        .then(function yes(resp) {
                            if(resp.data.VOTE === 'UPVOTE'){
                                $scope.upthumbStyle = {};
                                $scope.downthumbStyle = {};
                                $scope.upthumbStyle = {background: '#ab2323'};
                            }else if(resp.data.VOTE === 'DOWNVOTE'){
                                $scope.downthumbStyle = {background: '#ab2323'};
                            }
                        }, function no(resp) {
                            $log.error("Couldn't get voting, maybe they haven't voted yet. . .")
                        });
                    $scope.updateComments();
                };

                $scope.prev = function () {
                    $scope.upthumbStyle = {};
                    $scope.downthumbStyle = {};
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
                    $http.post('/comic',
                        {REQUEST: 'GET_USER_VOTE',
                        TITLE: $scope.currentComic.TITLE,
                        USER_GMAIL: $scope.currentComic.USER_GMAIL}
                    )
                        .then(function yes(resp) {
                            $scope.upthumbStyle = {};
                            $scope.downthumbStyle = {};
                            if(resp.data.VOTE === 'UPVOTE'){
                                $scope.upthumbStyle = {background: '#ab2323'};
                            }else if(resp.data.VOTE === 'DOWNVOTE'){
                                $scope.downthumbStyle = {background: '#ab2323'};
                            }
                        }, function no(resp) {
                            $log.error("Couldn't get voting, maybe they haven't voted yet. . .")
                        });
                    $scope.updateComments();
                };

                $scope.goToProfile = function () {
                    $location.path('/profile/' + btoa($scope.artist.GMAIL));
                };

                $scope.upVoteComment = function(comment) {
                    $http.post('/comment',
                        {
                            REQUEST: "VOTE",
                            USER_GMAIL: $scope.currentComic.USER_GMAIL,
                            COMMENTOR_GMAIL: comment.COMMENTOR_GMAIL,
                            TITLE: $scope.currentComic.TITLE,
                            DATE_POSTED: comment.DATE_POSTED.getTime(),
                            VOTE: "UPVOTE"
                        }
                    ).then( function() { $scope.updateComments(); });
                };


                $scope.downVoteComment = function(comment) {
                    $http.post('/comment',
                        {
                            REQUEST: "VOTE",
                            USER_GMAIL: $scope.currentComic.USER_GMAIL,
                            COMMENTOR_GMAIL: comment.COMMENTOR_GMAIL,
                            TITLE: $scope.currentComic.TITLE,
                            DATE_POSTED: comment.DATE_POSTED.getTime(),
                            VOTE: "DOWNVOTE"
                        }
                    ).then( function() { $scope.updateComments(); });
                };

                $scope.upVoteComic = function () {
                    $http.post('/comic',
                        {
                            REQUEST: "VOTE_FOR_COMIC",
                            USER_GMAIL: $scope.currentComic.USER_GMAIL,
                            TITLE: $scope.currentComic.TITLE,
                            VOTE: "UPVOTE"
                        }
                    ).then( 
                        function yes(resp) {
                            $scope.currentComic.SCORE = resp.data.SCORE;
                            $http.post('/comic',
                                {REQUEST: 'GET_USER_VOTE',
                                    TITLE: $scope.currentComic.TITLE,
                                    USER_GMAIL: $scope.currentComic.USER_GMAIL}
                            )
                                .then(function yes(resp) {
                                    $scope.upthumbStyle = {};
                                    $scope.downthumbStyle = {};
                                    if(resp.data.VOTE === 'UPVOTE'){
                                        $scope.upthumbStyle = {background: '#ab2323'};
                                    }else if(resp.data.VOTE === 'DOWNVOTE'){
                                        $scope.downthumbStyle = {background: '#ab2323'};
                                    }
                                }, function no(resp) {
                                    $log.error("Couldn't get voting, maybe they haven't voted yet. . .")
                                });

                        },
                    function no(resp) {
                        $log.error('bad vote');
                    });
                };

                $scope.downVoteComic = function () {
                    $http.post('/comic',
                        {
                            REQUEST: "VOTE_FOR_COMIC",
                            USER_GMAIL: $scope.currentComic.USER_GMAIL,
                            TITLE: $scope.currentComic.TITLE,
                            VOTE: "DOWNVOTE"
                        }
                    ).then( function(resp) {
                        $scope.currentComic.SCORE = resp.data.SCORE;

                            $http.post('/comic',
                                {REQUEST: 'GET_USER_VOTE',
                                    TITLE: $scope.currentComic.TITLE,
                                    USER_GMAIL: $scope.currentComic.USER_GMAIL}
                            )
                                .then(function yes(resp) {
                                    $scope.upthumbStyle = {};
                                    $scope.downthumbStyle = {};
                                    if(resp.data.VOTE === 'UPVOTE'){
                                        $scope.upthumbStyle = {background: '#ab2323'};
                                    }else if(resp.data.VOTE === 'DOWNVOTE'){
                                        $scope.downthumbStyle = {background: '#ab2323'};
                                    }
                                }, function no(resp) {
                                    $log.error("Couldn't get voting, maybe they haven't voted yet. . .")
                                });

                    },
                        function no(resp) {
                            $log.error('bad vote');
                        });
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


        $scope.onKeyDownHandler = function (ev) {
            var key;
            if(window.event){
                key = window.event.keyCode;
            }
            else{
                key = event.keyCode;
            }
            switch(key){
                case 39: //right
                    event.preventDefault();
                    $scope.next();
                    break;
                case 37: //left
                    event.preventDefault();
                    $scope.prev();
                    break;
                default:
                    break;
            }
        };
        document.onkeydown = $scope.onKeyDownHandler;

    }
]);