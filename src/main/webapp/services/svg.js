app.filter("svg", ['$sce', function($sce) {
    return function(htmlCode){
        var modifiedSVG = $sce.trustAsHtml(htmlCode);
        modifiedSVG;
        return modifiedSVG;
    }
}]);