app.factory('$navPoints', function($mdInkRipple) {
    'use strict';
    return {
        attach: function (scope, element, options) {
            return $mdInkRipple.attach(scope, element, angular.extend({
                center: false,
                dimBackground: true
            }, options));
        }
    };
});


app.controller('navCtrl', ['$scope', function ($scope) {
    'use strict';
    $scope.onClick = function (ev) {
        $navPoints.attach($scope, angular.element(ev.target), { center: true });
    }
    $scope.title = "Atomic Comics!";
    $scope.loginUrl = "";
}]);