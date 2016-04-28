app.controller('createCtrl', ['$scope', '$http', function ($scope, $http) {
    'use strict';
    ////////////////////////////////////////////////////////////////////////////////////// Upload Stuff
    $('#comicFrame').unbind('change').on('change', function () {
        if ($(this).get(0).files.length > 0) {
            var file = $(this).get(0).files[0];
            var reader = new FileReader();
            reader.onload = function (readerEvt) {
                $("#submissionType").val("COMIC_DRAFT");
                $("#redirectAddress").val("/#/create");
                if($scope.comicTitle == ''){
                    $scope.comicTitle = 'NO TITLE!!!';
                    return;
                }
                $("#comicTitle").val($scope.comicTitle);
                // We don't touch the comic Frame input as it contains the file
                $("#comicFrameForm").submit();
                return $scope.getUpload();
            };
            reader.readAsBinaryString(file);
        }
    });
    $scope.getUpload = function () {
      $http.get("/comic/frames")
          .then(function success(resp) {
             $scope.frames = resp.data.FRAMES;
          }, function error() {
              $scope.frames = [];
          });
    };
    $http.get("/assets")
        .then(function success(resp) {
            $scope.uploadUrl = resp.data.uploadUrl;
        }, function error (resp) {
            console.log(resp);
        });
    $scope.upload = function () {
        var fileInput = angular.element(document.querySelector('#comicFrame'));
        fileInput.click();
    };
    $scope.save = function () {
        $("#submissionType").val("COMIC_DRAFT");
        $("#redirectAddress").val("/#/create");
        if($scope.comicTitle == ''){
            $scope.noTitle = true;
        }
        $("#comicTitle").val($scope.comicTitle);
        $("#comicFrame").val($scope.canvas.toSVG());
        $("#comicFrameForm").submit();
    };
    ////////////////////////////////////////////////////////////////////////////////////// Upload Stuff

    ////////////////////////////////////////////////////////////////////////////////////// Canvas Stuff
    $scope.canvas = new fabric.Canvas('theCanvas');

    $scope.$on('$routeChangeSuccess', function(scope, next, current){
        $scope.canvasOps();
    });

    $scope.lineWidth = '1';
    $('#linewidth').on('change', function () {
        var inputWidth = $(this).val();
        $scope.lineWidth = inputWidth
    });

    $scope.canvasOps = function canvasOps() {
        $scope.canvas.setCursor('url(img/brush_sm.png)');
        $scope.canvas.renderAll();
    };

    $scope.delete = function () {
      $scope.canvas.clear()
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

    ////////////////////////////////////////////////////////////////////////////////////// Canvas Stuff

}]);