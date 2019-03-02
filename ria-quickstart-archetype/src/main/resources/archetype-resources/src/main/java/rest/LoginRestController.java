package ${package}.rest;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ${package}.model.User;
import ${package}.rest.model.BaseRestResponse;
import ${package}.rest.model.ConfirmAccountRequest;
import ${package}.rest.model.ConfirmAccountResponse;
import ${package}.rest.model.CreateUserResponse;;
import ${package}.rest.model.PasswordRecoveryRequest;
import ${package}.rest.model.PasswordRecoveryResponse;
import ${package}.service.LoginService;

/**
 * REST controller for login related operations.
 * 
 * @author Donato Rimenti
 */
@RestController
@RequestMapping("/login")
public class LoginRestController {

	/**
	 * Logger.
	 */
	private final static Logger LOG = LoggerFactory.getLogger(UserRestController.class);

	/**
	 * The service.
	 */
	@Autowired
	private LoginService service;

	/**
	 * Creates a new user.
	 * 
	 * @param user
	 *            the user to create
	 * @return the outcome of the operation
	 * @throws IOException
	 */
	@PostMapping(value = "/createUser", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public CreateUserResponse createUser(@RequestBody User user) throws IOException {
		LOG.info("UserRestController.createUser(*)");
		return service.createUser(user);
	}

	/**
	 * Sends a confirmation email to activate an account.
	 *
	 * @param email
	 *            the email to activate
	 * @return the outcome of the operation
	 */
	@GetMapping(value = "/sendAccountActivationEmail", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BasesRestResponse sendAccountActivationEmail(@RequestParam("email") String email) {
		LOG.info("UserRestController.sendAccountActivationEmail({})", email);
		service.sendConfirmationEmailAgain(email);
		return new BaseRestResponse(true, "Email sent");
	}

	/**
	 * Activates an account.
	 *
	 * @param request
	 *            contains the data of the account to activate
	 * @return the outcome of the operation
	 */
	@PostMapping(value = "/confirmAccount", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ConfirmAccountResponse confirmAccount(@RequestBody ConfirmAccountRequest request) {
		LOG.info("UserRestController.confirmAccount(*)");
		boolean outcome = service.activateAccount(request.getEmail(), request.getToken());
		String message;
		if (outcome) {
			message = "Succesfully activated the account.";
		} else {
			message = "The link you followed is expired or invalid.";
		}
		return new ConfirmAccountResponse(outcome, message);
	}

	/**
	 * Sends an email to recover an account password.
	 *
	 * @param email
	 *            the email whose password needs to be recovered
	 * @return the outcome of the operation
	 */
	@GetMapping(value = "/sendRecoverPasswordEmail", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BaseRestResponse sendRecoverPasswordEmail(@RequestParam("email") String email) {
		LOG.info("UserRestController.sendRecoverPasswordEmail({})", email);
		service.sendRecoverPasswordEmail(email);
		return new BaseRestResponse(true, "Email sent");
	}

	/**
	 * Confirms the password recovery by storing the new password.
	 *
	 * @param request
	 *            contains the data of the account whose password needs to be
	 *            recovered
	 * @return the outcome of the operation
	 */
	@PostMapping(value = "/passwordRecovery", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public PasswordRecoveryResponse passwordRecovery(@RequestBody PasswordRecoveryRequest request) {
		LOG.info("UserRestController.passwordRecovery(*)");
		boolean outcome = service.passwordRecovery(request.getEmail(), request.getToken(), request.getNewPassword());
		String message;
		if (outcome) {
			message = "New password has been saved.";
		} else {
			message = "The link you followed is expired or invalid.";
		}
		return new PasswordRecoveryResponse(outcome, message);
	}

	/**
	 * This webservice does nothing. It is used only to extend the user session
	 * if needed.
	 */
	@GetMapping("/extendUserSession")
	public void extendUserSession() {
	}

}