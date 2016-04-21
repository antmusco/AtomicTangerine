app.controller('createCtrl', ['$scope', '$http', function ($scope, $http) {
    'use strict';
    ////////////////////////////////////////////////////////////////////////////////////// Upload Stuff
    $('#comicPic').on('change', function () {
        var file = $(this).get(0).files[0];
        var info = 'comic/' + file.name;
        var reader = new FileReader();
        reader.onload = function(readerEvt) {
            var binaryString = readerEvt.target.result;
            // var encodedData = btoa(binaryString);
            $http.post('/assets/' + info, binaryString)
                .then(function success(resp) {
                    $scope.msg = 'Good';
                }, function error(resp) {
                    $scope.msg = 'Bad';
                });
        };
        reader.readAsBinaryString(file);
    });
    $scope.upload = function () {
        var fileInput = angular.element(document.querySelector('#comicPic'));
        fileInput.click();
    };
    ////////////////////////////////////////////////////////////////////////////////////// Upload Stuff

    ////////////////////////////////////////////////////////////////////////////////////// Canvas Stuff
    $scope.canvas = new fabric.Canvas('theCanvas');

    $scope.$on('$routeChangeSuccess', function(scope, next, current){
        $scope.canvasOps();
    });

    

    $scope.canvasOps = function canvasOps() {


        var rect = new fabric.Rect({
            left: 100,
            top: 100,
            fill: 'green',
            width: 40,
            height: 40,
            angle: 0
        });

        $scope.canvas.add(rect);
        $scope.canvas.renderAll();
    };
    
    $scope.draw = function () {
        $scope.canvas.isDrawingMode = !$scope.canvas.isDrawingMode;
    };

    $scope.canvasInit = function () {
       console.log('bloop');
       
    };
    $scope.canvasInit();
    ////////////////////////////////////////////////////////////////////////////////////// Canvas Stuff

}]);