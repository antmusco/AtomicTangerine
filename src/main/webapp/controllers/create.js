app.controller('createCtrl', ['$scope', '$http', '$mdDialog', '$mdSidenav', '$log', 'auth', 'crud',
    function ($scope, $http, $mdDialog, $mdSidenav, $log, auth, crud) {
        'use strict';

        var used_color = '';
        var entering = true;
        
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
                    $scope.canvas.clear();
                    $scope.comicStarted = true;
                    $http.get()
                        .then(function s() {

                        })
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

            var data = {
                REQUEST: "UPLOAD_FRAME",
                USER_GMAIL: auth.getUser().GMAIL,
                UPLOAD_URL: $scope.uploadUrl,
                REDIRECT_URL: '/#/create',
                TITLE: $scope.comicTitle,
                SVG_DATA: $scope.canvas.toSVG({suppressPreamble: true})
            };

            crud.update('/comic', data).then(function success(resp) {
                $log.info('Saved comic! \n\n' + resp.data.RESP + '\n\n');
            }, function error(resp) {
                $log.error('Did not save comic :( \n\n' + resp.data.RESP + '\n\n');
            });


        };

        $scope.openTemplateSideBar = function () {
            $scope.drafts = crud.update('/comic', {REQUEST:'USER_COMICS'}).
                then(function (resp) {
                    $scope.drafts = resp.DRAFTS;
            }, function () {
                $scope.drafts = [];
            });
            $mdSidenav('right').toggle()
                .then(function () {
                    $log.debug("toggle " + 'right' + " is done");
                });
        };

        $scope.publish = function () {
            $log.info('publish clicked');
        };
        
////////////////////////////////////////////////////////////////////////////////////////////// Upload Stuff

////////////////////////////////////////////////////////////////////////////////////////////// Canvas Stuff

        $scope.$on('$routeChangeSuccess', function (scope, next, current) {
            var user = auth.getUser();
            if (user === null || user == undefined)return;
            $http.post('/comic', {REQUEST: 'COMIC_LIST_DEFAULT', CREATED_DATE: (new Date() / 1000)})
                .then(function (resp) {
                    $log.info(resp);
                }, function (resp) {
                    $log.info(resp);
                });


            $http.post('/comic', {REQUEST: 'COMIC_LIST_DEFAULT', USER_GMAIL: auth.getUser().GMAIL})
                .then(function (resp) {
                    $log.info(resp);
                }, function (resp) {
                    $log.info(resp);
                });
        });

        $scope.addTexts = function () {
            var element = document.getElementById("addtext");
            var row_text = element.value;
            var default_color = '#000000';
            if (used_color != '') {
                default_color = used_color;
            }
            var text = new fabric.Text(row_text, {left: 100, top: 200, fontFamily: 'Bangers', fill: default_color});
            $scope.canvas.add(text);
            element.value = "";
        };


        $scope.delete = function () {


            if ($scope.canvas.getActiveObject() != null) {

                 $scope.canvas.getActiveObject().remove();
             }else{
                var group = new fabric.Group();
                 $scope.canvas.getActiveGroup().forEachObject(function(elem) {
                     $scope.canvas.remove(elem);



                });

                $scope.canvas.renderAll();

            }

        };
        $scope.Paint= function () {



            if($scope.canvas.getActiveObject() != null){
                var object = $scope.canvas.getActiveObject();
                alert(object);
                object.fill =used_color;
                $scope.canvas.renderAll();
            }

        };


        $scope.draw = function () {
            $scope.canvas.isDrawingMode = !$scope.canvas.isDrawingMode;
            if ($scope.canvas.isDrawingMode) {
                $scope.buttonStyle = {background: '#808080'};
            } else {
                $scope.buttonStyle = {background: '#ab2323'};
            }
        };

        $('#pickColor').unbind('change').on('change', function () {

        });

        $scope.colorPickerHelper = function () {
            var ele = document.getElementById("pickColor");
            var from_color_picker = ele.value;
            $scope.canvas.freeDrawingBrush.color = from_color_picker;
            $scope.pickedcolorstyle = {color: from_color_picker};
            used_color = from_color_picker;

        };

        $scope.pickColor = function (color) {
            $scope.canvas.freeDrawingBrush.color = color;
            $scope.pickedcolorstyle = {color: color};
            used_color = color;
        };

        $scope.drawshape = function (shape) {

            var shape_default = '#000000';
            if (used_color != '') {
                shape_default = used_color;
            }

            if (shape == 'cir') {
                var circle = new fabric.Circle({
                    radius: 20, fill: shape_default,
                    left: 400, top: 400
                });
                $scope.canvas.add(circle);

            } else if (shape === 'tri') {
                var triangle = new fabric.Triangle({
                    width: 20, height: 30, fill: shape_default,
                    left: 200, top: 200
                });
                $scope.canvas.add(triangle);

            } else if (shape === 'rect') {
                var rectangle = new fabric.Rect({
                    fill: shape_default, width: 20,
                    height: 40, angle: 0,
                    left: 300, top: 300
                });
                $scope.canvas.add(rectangle);

            }
        };

        $scope.addSignature = function () {
            var sign = JSON.parse(auth.getUser().SIGNATURE);
            sign.objects.forEach(function (currentValue, index, arr) {
                currentValue.top += 450;
                currentValue.left += 400;
            });
            var curCanvas = $scope.canvas.toJSON();
            var newCanvas = curCanvas.objects.concat(sign.objects);
            $scope.canvas.clear();
            fabric.util.enlivenObjects(newCanvas, function (objects) {
                var origRenderOnAddRemove = canvas.renderOnAddRemove;
                $scope.canvas.renderOnAddRemove = false;
                objects.forEach(function (o) {
                    $scope.canvas.add(o);
                });
                $scope.canvas.renderOnAddRemove = origRenderOnAddRemove;
                $scope.canvas.renderAll();
            });
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