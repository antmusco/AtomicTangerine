app.service('auth', function auth(crud, $q) {

    var authobj = this;

    var currentUser;

    authobj.getUserFromServer = function () {
        var later = $q.defer();
        crud.retrieve('/user')
            .then(function success(data) {
                if (data.USER !== null) {
                    if (data.USER.hasOwnProperty('BIRTHDAY')) {
                        data.USER.BIRTHDAY = new Date(data.USER.BIRTHDAY);
                    }
                    currentUser = data.USER;
                    later.resolve(null);
                } else {
                    currentUser = {USER: null};
                    later.reject(null);
                }
            }, function error() {
                currentUser = null;
                later.reject(null);
            });
        return later.promise;
    };

    authobj.getUser = function () {
        return currentUser;
    };

    authobj.updateUser = function (user) {
        if (user.BIRTHDAY instanceof Date) {
            user.BIRTHDAY_LONG = user.BIRTHDAY.getTime();
        }
        var later = $q.defer();
        crud.update('/user', {USER: user})
            .then(function success() {
                if (!(user.BIRTHDAY instanceof Date)) {
                    user.BIRTHDAY = new Date(user.BIRTHDAY * 1000);
                }
                currentUser = user;
                later.resolve("Updated.");
            }, function error() {
                if (!(user.BIRTHDAY instanceof Date)) {
                    user.BIRTHDAY = new Date(user.BIRTHDAY * 1000);
                }
                later.reject("Not Updated.");
            });
        return later.promise;
    };

    return authobj;

});