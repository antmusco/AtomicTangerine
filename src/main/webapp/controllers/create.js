app.controller('createCtrl', ['$scope', function ($scope) {
    'use strict';
    $scope.message = "Create Ctrl Active";

    var canvas = new fabric.Canvas('theCanvas');

    var rect = new fabric.Rect({
        left: 100,
        top: 100,
        fill: 'red',
        width: 20,
        height: 20,
        angle: 45
    });

    canvas.add(rect);
    rect.set({ left: 20, top: 50 });
    canvas.renderAll();

    $scope.upload = function () {
        angular.element(document.querySelector('#fileInput')).click();
    };

}]);