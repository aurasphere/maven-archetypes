// Loading spinner
app
		.directive(
				'loading',
				function() {
					return {
						transclude : true,
						template : "<div class='align-middle'>"
								+ "<div data-ng-show='loading'><i class='fa fa-spinner fa-spin loading'></i></div>"
								+ "<div data-ng-hide='loading'><data-ng-transclude></data-ng-transclude></div>"
								+ "</div>",
						scope : {
							loading : "="
						},
						replace : true
					};
				});

// Directive for auto dismissing an alert.
app
		.directive(
				'autoDismissAlert',
				function() {
					return {
						template : '<div class="row alert my-3 fade-out-animation" data-ng-class="error ? \'alert-danger\' : \'alert-success\'" role="alert" data-ng-show="showAlert" data-ng-bind="message"></div>',
						replace : true
					};
				});