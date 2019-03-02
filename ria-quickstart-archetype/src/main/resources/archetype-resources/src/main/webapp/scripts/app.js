var app = angular.module("${artifactId}", [ "ngRoute" ]);
app.config(function($routeProvider) {
	$routeProvider.when("/login", {
		templateUrl : "views/login.html"
	}).when("/password-recovery", {
		templateUrl : "views/password-recovery.html"
	}).when("/registration-confirmation", {
		templateUrl : "views/password-recovery.html"
	}).when("/error", {
		templateUrl : "views/error.html"
	}).otherwise({
		templateUrl : "views/404.html"
	});
});