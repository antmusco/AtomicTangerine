app.service('auth', function auth(crud, $q) {

    var authobj = this;

    authobj.getUser = function () {
        var later = $q.defer();
        crud.retrieve('/user')
            .then(function success(data) {
                if (data.USER !== null) {
                    if(data.USER.hasOwnProperty('BIRTHDAY')){
                        data.USER.BIRTHDAY = new Date(data.USER.BIRTHDAY);
                    }
                    later.resolve(data.USER);
                }else{
                    later.reject("Not logged in");
                }
            }, function error() {
                later.reject("Server error");
            });
        return later.promise;
    };

    authobj.updateUser = function (user) {
        if (user.BIRTHDAY instanceof Date){
            user.BIRTHDAY_LONG = user.BIRTHDAY.getTime();
        }
        var later = $q.defer();
        crud.update('/user', { USER: user})
            .then(function success() {
                later.resolve("Updated.");
                if (!(user.BIRTHDAY instanceof Date)){
                    user.BIRTHDAY = new Date(user.BIRTHDAY * 1000);
                }
            }, function error() {
                later.reject("Not Updated.");
                if (!(user.BIRTHDAY instanceof Date)){
                    user.BIRTHDAY = new Date(user.BIRTHDAY * 1000);
                }
            });
        return later.promise;
    };

    return authobj;

});