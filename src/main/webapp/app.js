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
            if (resp.data.hasOwnProperty("LOGIN")) {
                $rootScope.link = resp.data.LOGIN;
                $rootScope.acctBtnTxt = "Login!";
            } else if (resp.hasOwnProperty("LOGOUT")) {
                $rootScope.link = resp.data.LOGOUT;
                $rootScope.acctBtnTxt = "Logout!";
            } else {
                $rootScope.err += 'Bad Login: ' + resp.toString() + '\n';
            }
        }, function error(resp) {
            $rootScope.err += 'No Login: ' + resp.toString() + '\n';
        });

    $http.get("/user")
        .then(function success(resp) {
            if (resp.data.hasOwnProperty('infoNeeded')) {
                $rootScope.user = resp.data.USER;
            } else {
                $rootScope.user = resp.data.USER;
            }
        }, function error(resp) {
            $rootScope.err += 'No user: ' + resp.toString() + '\n';
        });
});