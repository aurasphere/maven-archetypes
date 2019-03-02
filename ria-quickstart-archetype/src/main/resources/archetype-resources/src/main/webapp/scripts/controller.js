app
		.controller(
				'loginController',
				function($scope, $rootScope, $http, $location, navigationHelper,
						$routeParams) {
					// Shows a confirmation message if everything went well.
					if ($routeParams.setPassword) {
						$scope.message = "Succesfully changed password, now you can log in.";
					}

					// Performs login.
					$scope.doLogin = function() {
						var url = navigationHelper.appBasePath + "/rest/login?username="
								+ $scope.email + "&password="
								+ $scope.password;
						$http
								.post(url)
								.then(
										function(response) {
											if (response.data.outcome) {
												$scope.loginError = false;
												$rootScope.isAdmin = response.data.user.authorities
														.some(function(element) {
															return element.authority === "ROLE_ADMIN";
														});
												$location.path("/");
											} else {
												$scope.loginError = true;
												$scope.message = response.data.errorMessage;
											}
										}, navigationHelper.handleHttpError);
					}

					// Shows the option to recover a password.
					$scope.showPasswordRecovery = function() {
						return $scope.loginError === true
								&& $scope.message === "Wrong email or password."
								&& $scope.loginForm.email.$valid;
					}

					// Sends a password recovery mail.
					$scope.sendRecoverPasswordEmail = function() {
						$http
								.get(
										navigationHelper.appBasePath + "/rest/user/sendRecoverPasswordEmail?email="
												+ $scope.email)
								.then(
										function(response) {
											if (response.data.outcome) {
												$scope.loginError = false;
												$scope.message = "We sent you an email with instruction to reset your password at the last email you inserted.";
											} else {
												navigationHelper
														.handleHttpError(response);
											}
										}, navigationHelper.handleHttpError);
					}
				});

app.controller("passwordRecoveryController", function($scope, $routeParams,
		$location, $http, navigationHelper) {
	$scope.message = "New password must be at least 8 characters long.";
	$scope.passwordRecovery = function() {
		var email = $routeParams.email;
		var token = $routeParams.token;
		// Error if no parameters are passed.
		if (!email || !token) {
			$location.path("/error");
			return;
		}

		$http.post(navigationHelper.appBasePath + "/rest/user/passwordRecovery", {
			email : email,
			token : token,
			newPassword : $scope.newPassword
		}).then(function(response) {
			if (response.data.outcome) {
				$location.search({
					setPassword : true
				});
				$location.path("/login");
			} else {
				$scope.message = response.data.errorMessage;
				$scope.error = true;
			}
		}, navigationHelper.handleHttpError);
	}
});

app.controller('navbarController', function($scope, $http, $location, navigationHelper) {
	// Performs logout.
	$scope.doLogout = function() {
		$http.post(navigationHelper.appBasePath + "/rest/logout").then(function() {
			$location.path("/login")
		}, navigationHelper.handleHttpError);
	}
});