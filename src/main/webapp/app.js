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

app.run(function ($http) {
    'use strict';
    $http.get("/user")
        .then(function success(resp) {
            if(resp.userInfo.hasOwnProperty('user')){
                $rootScope.user = resp.userInfo.user;
                $rootScope.logoutUrl = resp.userInfo.logoutUrl;
                $rootScope.loginUrl = '';
            } else {
                $rootScope.loginUrl = resp.userInfo.loginUrl;
            }
        }, function error(resp) {
            $rootScope.user = null;
            $rootScope.loginResp = resp;
        });
});