app.service('crud', function crud($http, $q, $rootScope) {
    'use strict';

    var crudobj = this;

    crudobj.create = function (url, data) {
        var defer = $q.defer();
        $http.put(url, data)
            .then(function success(resp) {
                defer.resolve(resp);
            }, function error(resp) {
                defer.reject(resp);
            });
        return defer.promise;
    };

    crudobj.retrieve = function (url) {
        var defer = $q.defer();
        $http.get(url)
            .then(function success(resp) {
                defer.resolve(resp.data);
            }, function error(resp) {
                defer.reject(resp);
            });
        return defer.promise;
    };

    crudobj.update = function (url, data) {
        var defer = $q.defer();
        $http.post(url, data)
            .then(function success(resp) {
                defer.resolve(resp);
            }, function error(resp) {
                defer.reject(resp);
            });
        return defer.promise;
    };

    crudobj.delete = function (posturl) {
        var defer = $q.defer();
        $http.delete(url)
            .then(function success(resp) {
                defer.resolve(resp);
            }, function error(resp) {
                defer.reject(resp);
            });
        return defer.promise;
    };

    return crudobj;

});