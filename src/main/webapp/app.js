var app = angular.module('atomicApp', ['ngRoute', 'ngMaterial']);

app.config(['$routeProvider', '$mdThemingProvider', function ($routeProvider, $mdThemingProvider) {
    'use strict';
    $routeProvider.
        when('/settings', {
            templateUrl: 'views/settings.html',
            controller: 'settingsCtrl'
        }).
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
    $http.get('/login')
        .then(function success(resp) {
            if (resp.data.hasOwnProperty('LOGIN')) {
                $rootScope.link = resp.data.LOGIN;
                $rootScope.acctBtnTxt = "Login!";
            } else if (resp.data.hasOwnProperty("LOGOUT")) {
                $rootScope.link = resp.data.LOGOUT;
                $rootScope.acctBtnTxt = "Logout!";
            } else {
                $rootScope.err += 'Bad Login: ' + resp.toString() + '\n';
            }
        }, function error(resp) {
            $rootScope.err += 'No Login: ' + resp.toString() + '\n';
        });

    
});