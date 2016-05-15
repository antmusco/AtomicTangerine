app.controller('createCtrl', ['$scope', '$http', '$mdDialog', '$mdSidenav', '$log', 'auth', 'crud', '$mdToast',
    function ($scope, $http, $mdDialog, $mdSidenav, $log, auth, crud, $mdToast) {
        'use strict';

        var used_color = '';
        var entering = true;
        $scope.comicTitle = '';

        $scope.disableControls = true;
        $scope.canvas = new fabric.Canvas('theCanvas');
        var prompt = new fabric.Text('Draw for fun or Press NEW to start comic', {fontFamily: 'Bangers', fill: '#ab2323'});
        $scope.canvas.add(prompt);
        prompt.center();

        $scope.startComic = function (ev) {
            if($scope.comicTitle === ''){
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('Set a title to create comic')
                        .position('top right')
                        .hideDelay(3000)
                        .theme('toast-error')
                );
                return;
            }
            $scope.canvas.remove(prompt);
            var data = {
                REQUEST:'COMIC_CREATE',
                USER_GMAIL:auth.getUser().GMAIL,
                TITLE: $scope.comicTitle
            };
            crud.create('/comic', data)
                .then(function yes(resp) {
                    $log.info('comic created ' + resp.data.RESULT);
                    $scope.save();
                }, function no(resp) {
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent('This title is already taken')
                            .position('top right')
                            .hideDelay(3000)
                            .theme('toast-error')
                    );
                    $scope.disableControls = true;
                    $log.error('comic failed to create ' + resp.data.RESULT);
                    $scope.disableControls = true;
                });
            $scope.disableControls = false;
        };

        $scope.closecomic = function(ev){
            var confirm = $mdDialog.confirm()
                .title('Are you sure?')
                .textContent('All of you current comic\'s progress will be lost')
                .ariaLabel('Comic Loss warning')
                .targetEvent(ev)
                .ok('Clean the slate!')
                .cancel('Oh no! go back!');
            $mdDialog.show(confirm).then(function yes() {
                $scope.canvas.clear();
                $scope.canvas.add(prompt);
            });
            $scope.comicTitle = '';
        };

        $scope.deleteForever = function (ev) {
            var confirm = $mdDialog.confirm()
                .title('Delete Comic Forever.')
                .textContent('Are you sure?')
                .ariaLabel('Comic Loss warning')
                .targetEvent(ev)
                .ok("It wasn't all that great")
                .cancel("No! my creation! take me back!");
            $mdDialog.show(confirm).then(function yes() {
                crud.update('/comic', {REQUEST:'DELETE_COMIC', TITLE: $scope.comicTitle})
                    .then(function yes() {
                        $scope.canvas.clear();
                        $scope.comicTitle = '';
                        $scope.canvas.add(prompt);
                        $mdToast.show(
                            $mdToast.simple()
                                .textContent('Comic Deleted')
                                .position('bottom right')
                                .hideDelay(3000)
                        );
                    }, function no() {
                        $mdToast.show(
                            $mdToast.simple()
                                .textContent('Unable to delete comic')
                                .position('bottom right')
                                .hideDelay(3000)
                        );
                    });
            });

        };

        ////////////////////////////////////////////////////////////////////////////////////// Upload Stuff
        $('#comicFrame').on('change', function (e) {
            var reader = new FileReader();
            reader.onload = function (event) {
                var imgObj = new Image();
                imgObj.src = event.target.result;
                imgObj.onload = function () {
                    var image = new fabric.Image(imgObj);
                    image.set({
                        left: 20,
                        top: 20,
                        angle: 0,
                        padding: 5,
                        cornersize: 10
                    });
                    $scope.canvas.add(image);
                    $scope.canvas.renderAll();
                }
            };
            reader.readAsDataURL(e.target.files[0]);

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
                TITLE: $scope.comicTitle,
                FRAME_INDEX: 0,
                JSON_DATA: $scope.canvas.toJSON(),
                THUMBNAIL: $scope.canvas.toDataURL("image/png")
            };

            crud.update('/comic', data).then(function success(resp) {
                $log.info('Saved comic! \n\n' + resp.data.RESULT + '\n\n');
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('Comic Saved!')
                        .position('bottom right')
                        .hideDelay(3000)
                );
            }, function error(resp) {
                $log.error('Did not save comic :( \n\n' + resp.data.RESULT + '\n\n');
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('Comic Not Saved!')
                        .position('bottom right')
                        .hideDelay(3000)
                );
            });


        };

        $scope.openTemplateSideBar = function () {
            crud.update('/comic', {REQUEST:'GET_USER_COMICS'})
                .then(function (resp) {
                        $scope.drafts = resp.data.COMICS;
                        $mdSidenav('right').toggle()
                            .then(function () {
                                $log.debug("toggle " + 'right' + " is done");
                            });
                    }, function () {
                        $scope.drafts = [];
                    }
                );
        };

        $scope.loadComic = function (indx) {
            $scope.canvas.clear();
            var comic = JSON.parse($scope.drafts[indx].JSON_DATA);
            fabric.util.enlivenObjects(comic.objects, function (objects) {
                var origRenderOnAddRemove = $scope.canvas.renderOnAddRemove;
                $scope.canvas.renderOnAddRemove = false;
                objects.forEach(function (o) {
                    $scope.canvas.add(o);
                });
                $scope.canvas.renderOnAddRemove = origRenderOnAddRemove;
                $scope.canvas.renderAll();
            });
            $scope.comicTitle = $scope.drafts[indx].TITLE;
            $scope.disableControls = false;
        };

        $scope.loadComicTemp = function (indx) {
            var curCanvas = $scope.canvas.toJSON();
            var comic = curCanvas.objects.concat(JSON.parse($scope.drafts[indx].JSON_DATA).objects);
            fabric.util.enlivenObjects(comic, function (objects) {
                var origRenderOnAddRemove = $scope.canvas.renderOnAddRemove;
                $scope.canvas.renderOnAddRemove = false;
                objects.forEach(function (o) {
                    $scope.canvas.add(o);
                });
                $scope.canvas.renderOnAddRemove = origRenderOnAddRemove;
                $scope.canvas.renderAll();
            });
            $scope.disableControls = false;
        };

        $scope.descripViewable = true;
        $scope.publish = function () {
            if($scope.descripViewable){
                $log.info('DESCRIP: ' + $scope.description);
                $scope.descripViewable = false;
            }else{
                $scope.descripViewable = true;
            }
        };

////////////////////////////////////////////////////////////////////////////////////////////// Upload Stuff

////////////////////////////////////////////////////////////////////////////////////////////// Canvas Stuff

        $scope.$on('$routeChangeSuccess', function (scope, next, current) {
            var user = auth.getUser();
            if (user === null || user == undefined)return;
            $http.post('/comic', {REQUEST: 'GET_COMIC_LIST_DEFAULT', DATE_CREATED: (new Date()).getTime() })
                .then(function (resp) {
                    $log.info(resp);
                }, function (resp) {
                    $log.info(resp);
                });


            $http.post('/comic', {REQUEST: 'GET_COMIC_LIST_DEFAULT', USER_GMAIL: auth.getUser().GMAIL})
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
            var from_color_picker = this.value;
            $scope.canvas.freeDrawingBrush.color = from_color_picker;
            $scope.pickedcolorstyle = {color: from_color_picker};
            used_color = from_color_picker;

        });

        // $scope.colorPickerHelper = function () {
        //     var ele = document.getElementById("pickColor");
        //     var from_color_picker = ele.value;
        //     $scope.canvas.freeDrawingBrush.color = from_color_picker;
        //     $scope.pickedcolorstyle = {color: from_color_picker};
        //     used_color = from_color_picker;
        //
        // };

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
            if (sign.objects === undefined) return;
            sign.objects.forEach(function (currentValue, index, arr) {
                currentValue.top += 450;
                currentValue.left += 400;
            });
            var curCanvas = $scope.canvas.toJSON();
            var newCanvas = curCanvas.objects.concat(sign.objects);
            $scope.canvas.clear();
            fabric.util.enlivenObjects(newCanvas, function (objects) {
                if (objects === undefined) return;
                var origRenderOnAddRemove = $scope.canvas.renderOnAddRemove;
                $scope.canvas.renderOnAddRemove = false;
                objects.forEach(function (o) {
                    $scope.canvas.add(o);
                });
                $scope.canvas.renderOnAddRemove = origRenderOnAddRemove;
                $scope.canvas.renderAll();
            });
        };




        $scope.onKeyDownHandler = function(event) {
            var key;
            if(window.event){
                key = window.event.keyCode;
            }
            else{
                key = event.keyCode;
            }
            switch(key){
                case 67: // Ctrl+C
                    if(event.ctrlKey){
                        event.preventDefault();
                        copy();
                    }
                    break;
                // Paste (Ctrl+V)
                case 86: // Ctrl+V
                    if(event.ctrlKey){
                        event.preventDefault();
                        paste();
                    }
                    break;
                case 8:
                case 46:
                    $scope.delete();
                    event.preventDefault();
                    break;
                default:
                    break;
            }
        };
        document.onkeydown = $scope.onKeyDownHandler;

        var copiedObject;
        var copiedObjects = [];
        function copy(){
            if($scope.canvas.getActiveGroup()){
                for(var i in canvas.getActiveGroup().objects){
                    var object = fabric.util.object.clone($scope.canvas.getActiveGroup().objects[i]);
                    object.set("top", object.top+5);
                    object.set("left", object.left+5);
                    copiedObjects[i] = object;
                }
            }
            else if($scope.canvas.getActiveObject()){
                var object = fabric.util.object.clone($scope.canvas.getActiveObject());
                object.set("top", object.top+5);
                object.set("left", object.left+5);
                copiedObject = object;
                copiedObjects = [];
            }
        }

        function paste(){
            if(copiedObjects.length > 0){
                for(var i in copiedObjects){
                    $scope.canvas.add(copiedObjects[i]);
                }
            }
            else if(copiedObject){
                $scope.canvas.add(copiedObject);
            }
            $scope.canvas.renderAll();
        }



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