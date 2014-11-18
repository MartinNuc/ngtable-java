angular.module('ngtablejava', ['membersService', 'ngTable', 'ngRoute']).config(
        [ '$routeProvider', function($routeProvider) {
            $routeProvider.
            when('/home', {
                templateUrl : 'partials/home.html',
                controller : MembersCtrl
            }).otherwise({
                redirectTo : '/home'
            });
        } ]);