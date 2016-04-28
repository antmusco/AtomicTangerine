app.controller('createCtrl', ['$scope', '$http', function ($scope, $http) {
    'use strict';
    ////////////////////////////////////////////////////////////////////////////////////// Upload Stuff
    $('#comicPic').on('change', function () {
        var file = $(this).get(0).files[0];
        var info = 'comic/' + $scope.comicTitle + '/add/' + file.name;
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


        // var rect = new fabric.Rect({
        //     left: 100,
        //     top: 100,
        //     fill: 'green',
        //     width: 40,
        //     height: 40,
        //     angle: 0
        // });
        //
        // $scope.canvas.add(rect);
        $scope.canvas.renderAll();
    };
    
    $scope.draw = function () {
        $scope.canvas.isDrawingMode = !$scope.canvas.isDrawingMode;
        if ($scope.canvas.isDrawingMode){
            $scope.buttonStyle={background:'#808080'};


        }else{
            $scope.buttonStyle={background:'#ab2323'};
        }
    };

    
    $scope.pickColor = function(color){

        $scope.canvas.freeDrawingBrush.color = color;
        $scope.pickedcolorstyle={color:color};
     
    };
    
    $scope.drawshape=function(shape){
        if(shape=='cir'){
            var circle = new fabric.Circle({
                radius: 20, fill: 'yellow',
                left:400, top:400
            });
            $scope.canvas.add(circle);

        }else if(shape=='tri'){
            var triangle = new fabric.Triangle({
                width: 20, height: 30, fill: 'indigo',
                left:200, top:200
            });
            $scope.canvas.add(triangle);

        }else if(shape=='rect'){
            var rectangle = new fabric.Rect({

                fill: 'violet',
                width: 20,
                height: 40,
                angle: 0,
                left:300, top:300
            });
            $scope.canvas.add(rectangle);

        };
    };

    $scope.canvasInit = function () {
       console.log('bloop');
       
    };
    $scope.canvasInit();
    ////////////////////////////////////////////////////////////////////////////////////// Canvas Stuff

}]);