app.controller('settingsCtrl', ['$scope', '$rootScope', 'auth', '$log', '$location',
    function ($scope, $rootScope, auth, $log, $location) {
        'use strict';

        $scope.canvas = new fabric.Canvas('signCanvas');
        $scope.canvas.isDrawingMode = true;
        $scope.canvas.freeDrawingBrush.width = 3;

        $scope.jobDesc = 'Atomic Artist';
        // $scope.user = {
        //     FIRSTNAME: '',
        //     LASTNAME: '',
        //     GMAIL: '',
        //     BIRTHDAY: new Date(),
        //     BIRTHDAY_LONG: 0,
        //     BIO: '',
        //     SIGNATURE: null
        // };


        $scope.user = auth.getUser();

        if ($scope.user === undefined || $scope.user == null) {
            $location.path('#/main');
        }

        try {
            if ($scope.user.SIGNATURE != null) {
                $scope.canvas.loadFromJSON(JSON.parse($scope.user.SIGNATURE), $scope.canvas.renderAll.bind($scope.canvas));
            }
        } catch (e) {
            $log.error('No signature data');
        }

        $scope.updateUserSettings = function () {
            $scope.user.SIGNATURE = JSON.stringify($scope.canvas.toJSON());
            auth.updateUser($scope.user)
                .then(function success(msg) {
                    $scope.confirm = "Saved User Settings!"
                }, function error(msg) {
                    $scope.confirm = "Uh oh, there's a server issue."
                })
        };


    }]);

