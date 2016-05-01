app.controller('createCtrl', ['$scope', '$http', '$mdDialog', '$log', '$mdSidenav',
    function ($scope, $http, $mdDialog, $mdSidenav, $log) {
    'use strict';

    $scope.comicTitle = '';
    $scope.comicStarted = false;
    var canvas = document.getElementById("theCanvas");
    var ctx = canvas.getContext("2d");
    ctx.font = "30px Bangers, sans-serif";
    ctx.fillStyle = "#ab2323";
    ctx.textAlign = "center";
    ctx.fillText("Click on NEW to start a comic", canvas.width / 2, canvas.height / 2);
    $scope.startComic = function (ev) {
        if ($scope.comicTitle == '') {
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            $scope.comicTitle = "PLEASE SET A COMIC TITLE";
            return;
        }
        if ($scope.comicStarted) {
            var confirm = $mdDialog.confirm()
                .title('Are you sure?')
                .textContent('All of you current comic\'s progress will be lost')
                .ariaLabel('Comic Loss warning')
                .targetEvent(ev)
                .ok('Clean the slate!')
                .cancel('Oh no! go back!');
            $mdDialog.show(confirm).then(function yes() {
                $scope.delete();
                $scope.comicStarted = true;
            });
        } else {
            $scope.comicStarted = true;
            $scope.canvas = new fabric.Canvas('theCanvas');
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////// Upload Stuff
    $('#comicFrame').unbind('change').on('change', function () {
        if ($(this).get(0).files.length > 0) {
            var file = $(this).get(0).files[0];
            var reader = new FileReader();
            reader.onload = function (readerEvt) {
                $("#submissionType").val("COMIC_FRAME");
                $("#redirectAddress").val("/#/create");
                if ($scope.comicTitle == '') {
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
        }, function error(resp) {
            console.log(resp);
        });
    $scope.upload = function () {
        var fileInput = angular.element(document.querySelector('#comicFrame'));
        fileInput.click();
    };
    $scope.save = function () {
        $("#submissionType").val("COMIC_DRAFT");
        $("#redirectAddress").val("/#/create");
        if ($scope.comicTitle == '') {
            $scope.noTitle = true;
        }
        $("#comicTitle").val($scope.comicTitle);
        $("#comicFrame").val($scope.canvas.toSVG());
        $("#comicFrameForm").submit();
    };
    ////////////////////////////////////////////////////////////////////////////////////// Upload Stuff

    ////////////////////////////////////////////////////////////////////////////////////// Canvas Stuff

    $scope.$on('$routeChangeSuccess', function (scope, next, current) {
        $scope.canvasOps();
    });

    $scope.lineWidth = '1';
    $('#linewidth').on('change', function () {
        $scope.lineWidth =  $(this).val();
    });

    $scope.canvasOps = function canvasOps() {
        //$scope.canvas.setCursor('url(img/brush_sm.png)');
        //$scope.canvas.renderAll();
    };

    $scope.delete = function () {
        $scope.canvas.clear()
    };

    $scope.draw = function () {
        $scope.canvas.isDrawingMode = !$scope.canvas.isDrawingMode;
        if ($scope.canvas.isDrawingMode) {
            $scope.buttonStyle = {background: '#808080'};


        } else {
            $scope.buttonStyle = {background: '#ab2323'};
        }
    };


    $scope.pickColor = function (color) {

        $scope.canvas.freeDrawingBrush.color = color;
        $scope.pickedcolorstyle = {color: color};

    };

    $scope.drawshape = function (shape) {
        if (shape == 'cir') {
            var circle = new fabric.Circle({
                radius: 20, fill: 'yellow',
                left: 400, top: 400
            });
            $scope.canvas.add(circle);

        } else if (shape == 'tri') {
            var triangle = new fabric.Triangle({
                width: 20, height: 30, fill: 'indigo',
                left: 200, top: 200
            });
            $scope.canvas.add(triangle);

        } else if (shape == 'rect') {
            var rectangle = new fabric.Rect({

                fill: 'violet',
                width: 20,
                height: 40,
                angle: 0,
                left: 300, top: 300
            });
            $scope.canvas.add(rectangle);

        }
    };


    $scope.addSignature = function () {
        
    };

    $scope.openTemplateSideBar = function () {
        $mdSidenav('right').toggle()
            .then(function () {
                $log.debug("toggle " + 'right' + " is done");
            });

    };

    $scope.publish = function () {

    };

    $scope.addSignature = function () {

    };

    ////////////////////////////////////////////////////////////////////////////////////// Canvas Stuff

}]);

app.controller('RightCtrl', function ($scope, $timeout, $mdSidenav, $log) {
    $scope.close = function () {
        $mdSidenav('right').close()
            .then(function () {
                $log.debug("close RIGHT is done");
            });
    };
});