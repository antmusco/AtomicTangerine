app.controller('mainCtrl', ['$scope', '$timeout', '$http', '$log', '$sce', function ($scope, $timeout, $http, $log, $sce) {
    'use strict';

    $scope.$on('$routeChangeSuccess', function (scope, next, current) {

    });
    /*
    $scope.comics = [
        'https://storage.googleapis.com/comics-cse-308/comic.png',
        'https://storage.googleapis.com/comics-cse-308/cosmos.jpg',
        'https://storage.googleapis.com/comics-cse-308/frangipani.jpg',
        'https://storage.googleapis.com/comics-cse-308/marygold.jpg',
        'https://storage.googleapis.com/comics-cse-308/poppies.jpg',
        'https://storage.googleapis.com/comics-cse-308/purple-rose.jpg',
        'https://storage.googleapis.com/comics-cse-308/rose.jpg',
        'mock-up/img/comic3.png'
    ];
    */
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

}]);