app.service("navigationHelper", function($location) {
	// Handles http errors.
	this.handleHttpError = function(response) {
		if (response.status === 401) {
			$location.path("/login");
		} else if (response.status === 403) {
			$location.path("/404");
		} else {
			$location.path("/error");
		}
	}
	
	// TODO: App base path. Leave empty if 
	// deployed on root path, otherwise add 
	// the app context.
	this.appBasePath = "/";
});