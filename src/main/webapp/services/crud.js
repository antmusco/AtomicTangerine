app.service('crud', function crud($http, $q, $rootScope) {
    'use strict';

    var crudobj = this;

    crudobj.create = function (url, data) {
        var defer = $q.defer();
        $http.put(url + '/c', data)
            .then(function success(resp) {
                defer.resolve(resp);
            }, function error(resp) {
                defer.reject(resp);
            });
        return defer.promise;
    };

    crudobj.retrieve = function (url) {
        var defer = $q.defer();
        $http.get(url + '/r')
            .then(function success(resp) {
                defer.resolve(resp.data);
            }, function error(resp) {
                defer.reject(resp);
            });
        return defer.promise;
    };

    crudobj.update = function (url, data) {
        var defer = $q.defer();
        $http.post(url + '/u', data)
            .then(function success(resp) {
                defer.resolve(resp);
            }, function error(resp) {
                defer.reject(resp);
            });
        return defer.promise;
    };

    crudobj.delete = function (url) {
        var defer = $q.defer();
        $http.delete(url + '/d')
            .then(function success(resp) {
                defer.resolve(resp);
            }, function error(resp) {
                defer.reject(resp);
            });
        return defer.promise;
    };

    return crudobj;

});