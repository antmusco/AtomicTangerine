/*jslint browser: true*/
/*global $, jQuery, alert, Materialize*/

var cur = 1;
var imgs = ["img/comic1.jpg", "img/comic2.png", "img/comic3.png"];

function goToPrev() {
    "use strict";

    $("#theComic").fadeOut(function () {
        $(this).load(function () {
            $(this).fadeIn();
        });
        cur -= 1;
        $(this).attr("src", imgs[Math.abs(cur % 3)]);
    });
}

function goToNext() {
    "use strict";

    $("#theComic").fadeOut(function () {
        $(this).load(function () {
            $(this).fadeIn();
        });
        cur += 1;
        $(this).attr("src", imgs[Math.abs(cur % 3)]);
    });
}

