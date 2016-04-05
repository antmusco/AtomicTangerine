var app = angular.module('atomicApp', ['ngRoute', 'ngMaterial']);

app.config(['$routeProvider', '$mdThemingProvider', function ($routeProvider, $mdThemingProvider) {
    'use strict';
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
    $mdThemingProvider.theme('default')
        .primaryPalette('red')
        .accentPalette('orange');
}]);

app.run(function ($http, $rootScope) {
    'use strict';
    $rootScope.err = '';
    $http.get("/login")
        .then(function success(resp) {
            if (resp.hasOwnProperty("LOGIN")) {
                $rootScope.loginUrl = resp.LOGIN;
                $rootScope.userExists = false;
            } else if (resp.hasOwnProperty("LOGOUT")) {
                $rootScope.logoutUrl = resp.LOGOUT;
                $rootScope.userExists = true;
            } else {
                $rootScope.err += 'Bad Login: ' + resp + '\n';
            }
        }, function error(resp) {
            $rootScope.err += 'No Login: ' + resp + '\n';
        });

    $http.get("/user")
        .then(function success(resp) {
            if (resp.userInfo.hasOwnProperty('infoNeeded')) {
                $rootScope.user = resp.USER;
            } else {
                $rootScope.user = resp.USER;
            }
        }, function error(resp) {
            $rootScope.err += 'No user: ' + resp + '\n';
        });
});