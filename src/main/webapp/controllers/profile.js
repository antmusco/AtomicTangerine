app.controller('profileCtrl', ['$scope', '$mdDialog', '$mdMedia', function ($scope, $mdDialog, $mdMedia) {
    'use strict';
    $scope.status = '  ';
    $scope.message = "Profile Ctrl Active";
    $scope.customFullscreen = $mdMedia('xs') || $mdMedia('sm');
    $scope.showUserSettings = function (ev) {
        var useFullScreen = ($mdMedia('sm') || $mdMedia('xs')) && $scope.customFullscreen;
        $mdDialog.show({
            controller: DialogController,
            templateUrl: 'views/userSettings.html',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: false,
            fullscreen: useFullScreen
        })
            .then(function (answer, user) {
                $scope.status = 'You said the information was "' + answer + '".' + user;
            }, function () {
                $scope.status = 'You cancelled the dialog.';
            });

        $scope.$watch(function () {
            return $mdMedia('xs') || $mdMedia('sm');
        }, function (wantsFullScreen) {
            $scope.customFullscreen = (wantsFullScreen === true);
        });
    };

}]);

function DialogController($scope, $mdDialog) {
    'use strict';
    $scope.user = {
        handle: 'PhiliCheese',
        birthday: '',
        firstName: 'Phyllis',
        lastName: 'Peng',
        biography: 'Loves bunnies! Wishes there were more bunnies with Tangerines on their heads everywhere. Mission: Increase bunnies starting with Atomic Comics!',
    };
    $scope.hide = function () {
        $mdDialog.hide();
    };
    $scope.cancel = function () {
        $mdDialog.cancel();
    };
    $scope.answer = function (answer, user) {
        $mdDialog.hide(answer, user);
    };
}