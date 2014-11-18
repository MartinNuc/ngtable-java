function MembersCtrl($scope, apiFactory, ngTableParams) {

    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        count: 10,          // count per page
        sorting: {
            name: 'asc'     // initial sorting
        }
    }, {
        total: 0,           // length of data
        getData: function($defer, params) {
            apiFactory.search((params.page() - 1) * params.count(), params.count(), params.filter(), params.sorting())
                .success(function (result) {
                    $scope.items = result.items;
                    params.total(result.totalCount);
                    $defer.resolve(result.items);
                });
        }
    });

}