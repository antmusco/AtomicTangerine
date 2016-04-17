app.controller('createCtrl', ['$scope', 'crud', function ($scope, crud) {
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


    $('#comicPic').on('change', function () {
        var file = $(this).get(0).files[0];
        var reader = new FileReader();
        reader.onload = function(readerEvt) {
            var binaryString = readerEvt.target.result;
            var encodedData = btoa(binaryString);
            crud.update('/comic', {COMIC: encodedData})
                .then(function success() {
                    $scope.msg = "Uploaded!";
                }, function () {
                    $scope.msg = "Not Uploaded.";
                })
        };
        reader.readAsBinaryString(file);
    });


    $scope.upload = function () {
        var fileInput = angular.element(document.querySelector('#comicPic'));
        fileInput.click();
    };

}]);