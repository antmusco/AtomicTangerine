 app.controller('settingsCtrl', ['$scope', '$rootScope', 'auth', function ($scope, $rootScope, auth) {
    'use strict';

     $scope.canvas = new fabric.Canvas('signCanvas');
     $scope.canvas.isDrawingMode = true;


     $scope.jobDesc = 'Atomic Artist';
     $scope.user = {
         FIRSTNAME: '',
         LASTNAME: '',
         GMAIL: '',
         BIRTHDAY: new Date(),
         BIRTHDAY_LONG: 0,
         BIO: '',
         SIGNATURE: null
     };


     auth.getUser()
         .then(function success(user) {
             $scope.user = user;
             if($scope.user.SIGNATURE != null){
                 $scope.canvas.loadFromJSON($scope.user.SIGNATURE, $scope.canvas.renderAll.bind($scope.canvas))
             }
         }, function error(msg) {
             $scope.msg = msg;
         });

     $scope.updateUserSettings = function () {
         $scope.user.SIGNATURE = $scope.canvas.toJSON();
         auth.updateUser($scope.user)
             .then( function success(msg) {
                 $scope.confirm = "Saved User Settings!"
             }, function error(msg) {
                 $scope.confirm = "Uh oh, there's a server issue."
             })
     };


 }]);

