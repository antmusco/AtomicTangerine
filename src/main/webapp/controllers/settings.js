 app.controller('settingsCtrl', ['$scope', '$rootScope', 'auth', function ($scope, $rootScope, auth) {
    'use strict';


     $scope.jobDesc = 'Atomic Artist';
     $scope.user = {
         FIRSTNAME: '',
         LASTNAME: '',
         GMAIL: '',
         BIRTHDAY: new Date(),
         BIO: ''
     };

     auth.getUser()
         .then(function success(user) {
             $scope.user = user;
         }, function error(msg) {
             $scope.msg = msg;
         });

     $scope.updateUserSettings = function () {
         auth.updateUser($scope.user)
             .then( function success(msg) {
                 $scope.confirm = "Saved User Settings!"
             }, function error(msg) {
                 $scope.confirm = "Uh oh, there's a server issue."
             })
     };



 }]);

