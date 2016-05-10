app.controller('mainCtrl', ['$scope', '$timeout', function ($scope, $timeout) {
    'use strict';

    $scope.$on('$routeChangeSuccess', function (scope, next, current) {

    });
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
    $scope.message = "Main Ctrl Active ----- !";
    $scope.imagePath = $scope.comics[$scope.comics.length - 1];
    var counter = $scope.comics.length - 1;
    $scope.next = function () {
        counter += 1;
        $scope.imagePath = $scope.comics[counter % $scope.comics.length];
    };

    $scope.prev = function () {
        counter -= 1;
        $scope.imagePath = $scope.comics[counter % $scope.comics.length];
    };

}]);