var app = angular.module('atomicApp', ['ngRoute', 'ngMaterial', 'ngAnimate']);

app.config(['$routeProvider', '$mdThemingProvider', '$locationProvider', function($routeProvider, $mdThemingProvider , $locationProvider) {
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

    $locationProvider.html5Mode(true);

    $mdThemingProvider.theme('default')
        .primaryPalette('red')
        .accentPalette('blue-grey');
}
]);

app.run(function(crud, $rootScope) {
    'use strict';
    $rootScope.err = '';

    crud.retrieve('/login')
        .then(function success(data) {
            if (data.hasOwnProperty('LOGIN')) {
                $rootScope.link = data.LOGIN;
                $rootScope.acctBtnTxt = "Login!";
            } else if (data.hasOwnProperty("LOGOUT")) {
                $rootScope.link = data.LOGOUT;
                $rootScope.acctBtnTxt = "Logout!";
            }
        }, function err(resp) {
            $rootScope.err += 'Bad Login: ' + resp.status;
        });


});
