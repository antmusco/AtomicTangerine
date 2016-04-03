var app = angular.module('atomicApp', ['ngRoute', 'ngMaterial']);

app.config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
        when('/main', {
            templateUrl: 'views/main.html',
            controller: 'mainCtrl'
        }).
        when('/create', {
            templateUrl: 'views/create.html',
            controller: 'createCtrl'
        }).
        when('/profile', {
            templateUrl: 'views/profile.html',
            controller: 'profileCtrl'
        }).
        otherwise({
            redirectTo: '/main'
        });
}]);