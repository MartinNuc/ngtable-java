angular.module('membersService', []).
    factory('apiFactory', function($http){

        var apiFactory = {};
        apiFactory.search = function (firstResult, itemsPerPage, filterByFields, orderBy) {
            var param = {
                "firstResult": firstResult,
                "itemsPerPage": itemsPerPage,
                "filterByFields": filterByFields,
                "orderBy": orderBy
            };
            return $http.post('rest/members/filter', JSON.stringify(param));
        };

        return apiFactory;
    });