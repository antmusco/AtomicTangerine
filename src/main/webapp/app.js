var app = angular.module('atomicApp', ['ngRoute', 'ngMaterial', 'ngAnimate', 'ngSanitize']);

app.config(['$routeProvider', '$mdThemingProvider', '$locationProvider', function ($routeProvider, $mdThemingProvider, $locationProvider) {
    'use strict';

    $routeProvider.when('/settings', {
        templateUrl: 'views/settings.html'
        //controller: 'settingsCtrl'
    }).when('/main', {
        templateUrl: 'views/main.html'
        //controller: 'mainCtrl'
    }).when('/create', {
        templateUrl: 'views/create.html'
        //controller: 'createCtrl'
    }).when('/profile', {
        templateUrl: 'views/profile.html'
        //controller: 'profileCtrl'
    }).when('/team', {
        templateUrl: 'views/team.html'
        //controller: 'teamCtrl'
    }).otherwise({
        redirectTo: '/main'
    });

    $mdThemingProvider.theme('default')
        .primaryPalette('red')
        .accentPalette('blue-grey');
}
]);

app.run(function (crud, $rootScope, auth, $log, $q) {
    'use strict';
    $rootScope.err = '';
    $rootScope.loginAjax = $q.defer();
    crud.retrieve('/login')
        .then(function success(data) {
            if (data.hasOwnProperty('LOGIN')) {
                $rootScope.link = data.LOGIN;
                $rootScope.acctBtnTxt = "Login!";
                $rootScope.loginAjax.resolve();
            } else if (data.hasOwnProperty("LOGOUT")) {
                $rootScope.link = data.LOGOUT;
                $rootScope.acctBtnTxt = "Logout!";
                auth.getUserFromServer()
                    .then(function success() {
                        $log.info('User Logged in, and data received')
                        $rootScope.loginAjax.resolve();
                    }, function () {
                        $log.error('User Logged in, and data NOT received')
                        $rootScope.loginAjax.resolve();
                    });
            }
        }, function err(resp) {
            $rootScope.err += 'Bad Login: ' + resp.status;
        });
});
